# ADR 001: Modular monolith instead of microservices


## Context

This project is an ecommerce backend. The goals are to model a real
shop domain, to practice system design and design patterns, and to end up with a system
that can grow. The target feature set is wide: catalog, cart, orders, stock, payments,
shipping, search, and notifications.

## Decision

We build a modular monolith on Spring Boot and Spring Modulith.

* Every domain module is a direct subpackage of the application package.
* A module exposes a small facade and its events. Everything else in the module is
  internal, and no other module may touch it.
* Modules that need an immediate answer call a facade. Modules that only need to react
  to a fact listen to domain events.
* Each module keeps its tables in its own PostgreSQL schema. Joins and foreign keys
  across schemas are not allowed.
* A test based on Spring Modulith verifies the boundaries in every build, so a broken
  rule fails the build instead of becoming a habit.
