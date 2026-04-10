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


Q3.
