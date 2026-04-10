# SmartCampusAPI
Client Server Architecture CW 

Part 1 
Q1. ## JAX-RS Resource Lifecycle

By default, JAX-RS resource classes follow a **per-request lifecycle**. This means a new instance of the resource class is created for each incoming HTTP request, rather than being treated as a singleton.

This approach aligns with the stateless nature of HTTP, ensuring that each request is handled independently. As a result, instance variables are not shared across requests and do not require synchronization.

However, in this project, in-memory data structures such as `HashMap` or `ArrayList` are often declared as `static` so that data persists across multiple requests. These shared data structures can be accessed by multiple threads simultaneously, leading to potential race conditions.

To ensure thread safety:
- Thread-safe collections such as `ConcurrentHashMap` can be used
- Critical sections can be synchronized
- Shared mutable state should be minimized

Although per-request instantiation avoids issues with instance variables, developers must still carefully manage shared resources to prevent data inconsistency and concurrency issues.
