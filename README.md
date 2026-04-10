# SmartCampusAPI
Client Server Architecture CW 

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
- Simpler maintenance: Changes to endpoints or structure require fewer updates on the client side.

In contrast, with static documentation, developers must manually follow predefined endpoints, which may become outdated and lead to errors. HATEOAS makes APIs more dynamic, intuitive, and robust by embedding navigation directly into responses.


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

### Q2.
###


## Part 3
### Q1.
####

### Q2.
###


## Part 4
### Q1.
####

### Q2.
###


## Part 5
### Q1.
####

### Q2.
###

### Q3.
###




Therefore, there is a trade-off between efficiency and convenience. Returning IDs is more efficient in terms of bandwidth, while returning full objects is more convenient for the client and reduces the number of requests.
