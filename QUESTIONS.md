# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```
Using store.persist() means the Store entity is following Panache Active Record, while the Product code using PanacheRepository follows the Repository pattern. Mixing the two in the same codebase is technically supported by Quarkus, but it creates an inconsistent mental model for how persistence is handled. As a developer, you have to remember which aggregates persist themselves and which require a repository, and that friction shows up quickly in testing, refactoring, and onboarding.

Active Record can make sense for very simple CRUD-style entities, especially early on, because it’s concise and easy to read. Repositories tend to scale better as soon as business rules, queries, or cross-aggregate logic appear, because they keep persistence concerns out of the domain model and make use cases easier to test. In this project, where Warehouses already follow a use-case plus store abstraction, the repository approach is more consistent with the overall direction.

So my preference would be to refactor toward a single pattern, ideally repositories plus explicit use cases, and keep REST resources thin. If time is limited, I wouldn’t necessarily refactor everything at once, but I’d avoid introducing new Active Record usage and gradually migrate Store to a repository-based approach so the architecture stays coherent over time.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```
In this system we’ve used both approaches, and each has trade-offs. For the Warehouse API, which is a stable, shared capability, an OpenAPI-first approach works well because it forces an explicit contract, enables parallel work, and reduces the risk of accidental breaking changes. 

For Product and Store, which are more domain-driven and still evolving, we went code-first to keep iteration fast and let the API shape follow real use cases. My general preference is a hybrid model: use OpenAPI-first for public or long-lived APIs where the contract matters most, and code-first for internal or fast-changing domains, while still generating OpenAPI specs from code so documentation and behavior stay aligned.

```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```
Given the time and scope of the assignment, I focused testing effort where it provides the most confidence for the least overhead. I prioritised unit tests around the core domain and use cases, since that is where the business rules and constraints live and where regressions would be most costly. 

On top of that, I added a small number of integration tests at the API level to validate request handling, persistence, and error semantics for the main Warehouse flows, rather than trying to exhaustively test every endpoint. I intentionally limited end-to-end tests to only the most critical paths, as they are slower and more brittle.

```