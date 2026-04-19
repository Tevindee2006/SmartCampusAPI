# SmartCampusAPI
Client Server Architecture CW 

## API Design Overview
The API follows REST principles and is structured into:
- Rooms (/rooms)
- Sensors (/sensors)
- Sensor Readings (/sensors/{id}/readings)

It uses JAX-RS with Jersey and follows a layered design:
- Resource layer (endpoints)
- Model layer (data objects)
- Data layer (in-memory storage)


## How to Build and Run

1. Open the project in Apache NetBeans
2. Ensure JDK 17+ is installed
3. Right-click the project → Clean and Build
4. Right-click → Run
5. The server will start on:
   http://localhost:8080/SmartCampusAPI/api/v1
6. Use Postman or browser to test endpoints

## Curl Commands

 1. Create Room
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"ROOM-1","name":"Room A","capacity":30}'

 2. Get All Rooms
curl http://localhost:8080/SmartCampusAPI/api/v1/rooms

 3. Get Room by ID
curl http://localhost:8080/SmartCampusAPI/api/v1/rooms/ROOM-1

 4. Delete Room (success)
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/ROOM-2

 5. Delete Room (ERROR - has sensors → 409)
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/ROOM-1

 6. Create Sensor (VALID)
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"TEMP-1","type":"Temperature","status":"ACTIVE","currentValue":25,"roomId":"ROOM-1"}'

 7. Create Sensor (ERROR - invalid room → 422)
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"TEMP-ERR","type":"Temperature","status":"ACTIVE","currentValue":22,"roomId":"INVALID"}'

 8. Get All Sensors
curl http://localhost:8080/SmartCampusAPI/api/v1/sensors

 9. Filter Sensors by Type
curl "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=Temperature"

 10. Add Sensor Reading (VALID)
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-1/readings \
-H "Content-Type: application/json" \
-d '{"id":"R1","timestamp":1713550000000,"value":27}'


## Conceptual Answers
## Part 1 
### Q1.
#### JAX-RS Resource Lifecycle

By default, JAX-RS resource classes follow a **per-request lifecycle**. This means a new instance of the resource class is created for each incoming HTTP request, rather than being treated as a singleton.

This approach aligns with the stateless nature of HTTP, ensuring that each request is handled independently. As a result, instance variables are not shared across requests and do not require synchronization.

However, in this project, in-memory data structures such as `HashMap` or `ArrayList` are often declared as `static` so that data persists across multiple requests. These shared data structures can be accessed by multiple threads simultaneously, leading to potential race conditions.

To ensure thread safety:
- Thread-safe collections such as `ConcurrentHashMap` can be used
- Critical sections can be synchronized
- Shared mutable state should be minimized

Although per-request instantiation avoids issues with instance variables, developers must still carefully manage shared resources to prevent data inconsistency and concurrency issues.


### Q2.
#### REST HATEOAS and Hypermedia

Hypermedia (also referred to as HATEOAS) is regarded as an important aspect of advanced RESTful design. It implies that API responses contain links which guide clients on what actions they can take next.

The server does not just send data, but also includes navigation information in the response. For example, the response to a room resource can contain links to its sensors or delete the room.

This approach provides several advantages to client developers:

- Self-discoverability: Clients do not have to depend heavily on external documentation since the API provides links dynamically in responses.
- Reduced coupling: Clients do not rely on fixed endpoint structures. If the API changes, clients can still operate by following the provided links.
- Enhanced flexibility: The server controls the application flow by directing clients to the available actions.
- Easier maintenance: Changes to endpoints or structure require fewer updates on the client side.

In contrast, with static documentation, developers must manually follow predefined endpoints, which may become outdated and lead to errors. HATEOAS makes APIs more dynamic, intuitive, and robust by embedding navigation directly into responses.

For example, a response for a room resource may include:

{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "links": {
    "self": "/api/v1/rooms/LIB-301",
    "sensors": "/api/v1/rooms/LIB-301/sensors"
  }
}

This allows clients to dynamically navigate the API without hardcoding endpoint paths.


## Part 2 
### Q1.
#### Returning IDs vs Full Objects

When returning a list of rooms in a REST API, there are two approaches: returning only room IDs or returning full room objects. Each approach has different implications in terms of network bandwidth and client-side processing.

Returning only IDs reduces the amount of data transferred over the network, which improves performance and reduces bandwidth usage. For example, a response like:

[1, 2, 3]

is much smaller compared to sending full objects. However, this approach requires the client to make additional requests to retrieve full details for each room, increasing the number of HTTP calls.

On the other hand, returning full room objects provides all necessary information in a single response. For example:

[
  { "id": 1, "name": "Room A", "capacity": 50 },
  { "id": 2, "name": "Room B", "capacity": 30 }
]

This reduces the need for additional requests and simplifies client-side processing. However, it increases the size of the response and may consume more bandwidth.

Therefore, there is a trade-off between efficiency and convenience. Returning IDs is more efficient in terms of bandwidth, while returning full objects is more convenient for the client and reduces the number of requests.

In real-world APIs, pagination is often used to balance performance and usability by limiting the number of results returned in a single response.

### Q2.
#### Idempotency of DELETE Operation with Business Logic

Yes, the DELETE operation in this implementation is idempotent.

Idempotency means that performing the same operation multiple times results in the same final state as performing it once. In the context of DELETE, this means that repeated deletion requests should not further change the system after the first successful operation.

In this implementation, the DELETE /rooms/{id} operation includes a business rule: a room cannot be deleted if it still has active sensors assigned to it.

If a client sends a DELETE request for a room that has sensors:
- The request is blocked
- A custom error response is returned (e.g., 409 Conflict)
- The room remains unchanged

If the same DELETE request is repeated:
- The same error response is returned
- The system state remains unchanged

This is still idempotent because no changes occur regardless of how many times the request is repeated.

If a room has no sensors:
- The first DELETE request successfully removes the room (e.g., returns 200 OK or 204 No Content)
- Any subsequent DELETE requests for the same room will return 404 Not Found, since the room no longer exists

Again, the system state remains the same (the room is already deleted), demonstrating idempotency.

Therefore, even with the business constraint in place, the DELETE operation remains idempotent because repeated requests do not introduce additional side effects or change the final state of the system.


## Part 3
### Q1.
#### @Consumes and Media Type Handling

The @Consumes(MediaType.APPLICATION_JSON) annotation defines the media type that the server is able to consume. It specifies that the POST method only accepts requests with the Content-Type: application/json header.

If a client sends data in a different format (e.g., text/plain or application/xml), JAX-RS will handle the mismatch as follows:

- Rejection of the Request: JAX-RS will automatically reject the request and return an HTTP 415 Unsupported Media Type response. This indicates that the server refuses to accept the request because the payload format is not supported.
- No Processing: The method will not be invoked, and the request body will not be processed. This prevents errors caused by attempting to parse invalid data.
- Clear Client Feedback: The client receives a clear error response, allowing them to correct the Content-Type header and resend the request in the correct format.

This behavior is part of content negotiation in RESTful services, where the server ensures that only supported media types are processed.


### Q2.
#### Query Parameters vs Path Parameters for Filtering

In this implementation, filtering is achieved using @QueryParam, for example:
GET /api/v1/sensors?type=CO2

An alternative design could use a path parameter, such as:
GET /api/v1/sensors/type/CO2

However, the query parameter approach is generally considered superior for filtering and searching collections for several reasons:

- Semantic correctness: Query parameters are intended for filtering, sorting, and searching within a collection, whereas path parameters are used to identify specific resources. Using query parameters better aligns with RESTful design principles.

- Flexibility: Query parameters allow multiple filters to be combined easily, such as:
  GET /api/v1/sensors?type=CO2&status=active
  This is more difficult and less clean to represent using path parameters.

- Optionality: Query parameters are naturally optional. If no filter is provided, the API can simply return all sensors:
  GET /api/v1/sensors
  This makes the API more intuitive and easier to use.

- Scalability: As the number of filtering options increases, query parameters scale better without making the URL structure overly complex.

In contrast, embedding filters in the path can make the API rigid and harder to extend. Therefore, using query parameters provides a more flexible, scalable, and semantically correct approach for filtering and searching collections.

Additionally, query parameters are more suitable for caching and allow different filtered responses to be handled efficiently by caching mechanisms.


## Part 4
### Q1.
#### Sub-Resource Locator Pattern

The Sub-Resource Locator pattern is used in JAX-RS to delegate handling of nested resources to separate classes. Instead of defining all endpoints within a single resource class, a method returns another resource class that is responsible for handling a specific sub-path.

For example, a SensorResource can delegate requests for sensor readings to a SensorReadingResource using a sub-resource locator such as:
GET /sensors/{id}/readings

This approach provides several architectural benefits:

- Improved separation of concerns: Each resource class is responsible for a specific part of the API. For example, SensorResource handles sensors, while SensorReadingResource handles readings. This makes the code more modular and easier to understand.

- Better maintainability: Splitting logic across multiple classes makes it easier to update, debug, and extend the API without affecting unrelated parts of the system.

- Reduced complexity: Avoiding a single large controller class prevents code from becoming overly complex and difficult to manage. Large monolithic classes are harder to read and maintain.

- Scalability: As the API grows, new sub-resources can be added without significantly increasing the complexity of existing classes.

- Reusability: Sub-resource classes can be reused or extended independently, improving code organization.

In contrast, defining all nested paths (such as sensors/{id}/readings/{rid}) in one large controller leads to tightly coupled and hard-to-maintain code. The Sub-Resource Locator pattern promotes a cleaner, modular, and scalable architecture for building large RESTful APIs.


### Q2.
#### Historical Data Management and Data Consistency

The SensorReadingResource is responsible for managing historical readings for each sensor. It provides two main operations:

- GET /sensors/{id}/readings: Retrieves the full history of readings for a specific sensor.
- POST /sensors/{id}/readings: Adds a new reading to the sensor’s history.

When a new reading is added using the POST operation, it is important to maintain consistency between the historical data and the current state of the sensor.

Therefore, a successful POST request not only stores the new reading in the sensor’s history but also updates the `currentValue` field of the corresponding parent Sensor object.

This design ensures:

- Data consistency: The latest reading is always reflected in the sensor’s currentValue field.
- Synchronization between resources: The historical data (readings) and the main sensor resource remain aligned.
- Improved efficiency: Clients can retrieve the current value directly from the sensor without needing to process the entire reading history.

For example, if a new temperature reading of 25°C is added, the system appends it to the readings list and updates the sensor’s currentValue to 25.

This approach ensures that the API maintains an accurate and up-to-date representation of sensor data across all endpoints.


## Part 5
### Q1.
#### HTTP 422 vs 404 for Dependency Validation

HTTP 422 Unprocessable Entity is often considered more semantically accurate than 404 Not Found when the issue is a missing reference inside a valid JSON payload.

A 404 Not Found response is typically used when the requested resource itself cannot be found. For example, if a client requests:
GET /rooms/10
and room 10 does not exist, then a 404 response is appropriate because the resource being requested is missing.

However, in the case of POST /sensors, the client is not requesting a resource but attempting to create a new one. The request itself is valid and well-formed JSON, but it contains a logical error — the provided roomId does not correspond to an existing room.

In this situation:
- The request structure is correct
- The endpoint exists
- The payload is syntactically valid
- But the data is semantically incorrect

Therefore, HTTP 422 Unprocessable Entity is more appropriate because it indicates that the server understands the request but cannot process it due to invalid data.

Using 422 provides clearer feedback to the client, distinguishing between:
- A missing resource (404)
- Invalid or inconsistent data within a valid request (422)

This improves API usability and helps clients handle errors more accurately.

### Q2.
#### Risks of Exposing Internal Stack Traces

From a cybersecurity standpoint, exposing internal Java stack traces to external API consumers is a significant security risk.

A stack trace contains detailed information about the internal structure and execution of the application. If this information is exposed, an attacker can gather sensitive details about the system, which can be used to exploit vulnerabilities.

Some of the key risks include:

- Information disclosure: Stack traces may reveal internal class names, package structures, and file paths. This gives attackers insight into how the application is designed.

- Technology exposure: Attackers can identify the frameworks and technologies being used (e.g., JAX-RS, specific libraries), which helps them target known vulnerabilities.

- Code logic leakage: Stack traces may expose method names and execution flow, allowing attackers to understand how the application processes requests.

- Server details: Information about the server environment, such as operating system paths or configurations, may be revealed.

- Attack planning: With knowledge of the system structure, attackers can craft more precise attacks, such as injection attacks or targeting specific endpoints. For example, a stack trace may reveal exact class names such as SensorResource or internal file paths, which can help attackers identify vulnerable endpoints or exploit specific parts of the application.

To prevent these risks, a global ExceptionMapper<Throwable> should be implemented to catch all unexpected errors and return a generic HTTP 500 Internal Server Error response without exposing internal details. This ensures that sensitive information is not leaked while still informing the client that an error has occurred.

### Q3.
#### Use of Filters for Logging (Cross-Cutting Concerns)

Using JAX-RS filters for cross-cutting concerns such as logging is advantageous compared to manually inserting Logger statements inside every resource method.

Filters allow logging logic to be centralized in one place, rather than being duplicated across multiple resource classes. This improves code organization and reduces repetition.

The key advantages include:

- Separation of concerns: Logging is handled independently from business logic. Resource classes remain focused on handling requests and responses, while filters manage logging.

- Reduced code duplication: Without filters, Logger statements would need to be added to every method, leading to repetitive and cluttered code.

- Consistency: Filters ensure that all incoming requests and outgoing responses are logged in a uniform way, regardless of which resource handles them.

- Maintainability: Changes to logging behavior can be made in a single filter class instead of updating multiple resource methods.

- Scalability: As the API grows, filters automatically apply to new endpoints without requiring additional logging code.

By using ContainerRequestFilter and ContainerResponseFilter, logging is applied globally to all requests and responses. This results in cleaner, more maintainable, and more scalable code compared to embedding logging logic within each resource method.

This approach follows the principle of separation of concerns, ensuring that business logic and cross-cutting concerns such as logging remain independent.





