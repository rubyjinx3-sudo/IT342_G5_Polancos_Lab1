# Research & Application of Software Design Patterns

## Creational Patterns

### 1. Factory Pattern
- Category: Creational
- Problem it solves: Object creation logic becomes repetitive and scattered across services.
- How it works: A factory centralizes the logic for creating objects so callers do not need to know the construction details.
- Real-world example: A backend registration service creating different user accounts with encoded passwords and validated roles.
- Possible use case in this project: `UserFactory` creates `User` entities during registration with default role handling and password encoding.

### 2. Builder Pattern
- Category: Creational
- Problem it solves: Creating complex objects with many optional fields can lead to long constructors or error-prone setter chains.
- How it works: A builder assembles an object step by step before producing the final instance.
- Real-world example: Building a mobile notification payload with title, body, image, deep link, and priority.
- Possible use case in this project: A future `EventBuilder` could safely assemble event payloads for admin tools or mobile synchronization.

## Structural Patterns

### 3. Adapter Pattern
- Category: Structural
- Problem it solves: Internal domain models often do not match the shape required by APIs or clients.
- How it works: An adapter converts one interface or object structure into another without changing the original model.
- Real-world example: A backend adapting `User` entities into login responses and profile DTOs for web and mobile clients.
- Possible use case in this project: `AuthResponseAdapter` and `UserProfileAdapter` convert `User` into API-ready DTOs.

### 4. Decorator Pattern
- Category: Structural
- Problem it solves: Extra behavior is needed for certain outputs, but adding it directly to the base class would make it rigid.
- How it works: A decorator wraps an existing component and adds behavior before or after delegating to it.
- Real-world example: Enriching an API response with related user details only for organizer-facing views.
- Possible use case in this project: `StudentRegistrationViewDecorator` enriches registration records with student name and email.

## Behavioral Patterns

### 5. Strategy Pattern
- Category: Behavioral
- Problem it solves: A system needs multiple interchangeable algorithms for the same job.
- How it works: Each algorithm is placed in its own strategy class, and the service chooses among them through a shared interface.
- Real-world example: Categorizing uploaded events by title keywords, rules, or AI-based classification.
- Possible use case in this project: `CategoryRule` strategies are used by `EventCategoryResolver` to determine event categories.

### 6. Chain of Responsibility Pattern
- Category: Behavioral
- Problem it solves: A request should pass through multiple decision rules until one of them handles it.
- How it works: Handlers are linked in sequence, and each handler either processes the request or passes it to the next one.
- Real-world example: Request validation pipelines, middleware chains, or event classification rules.
- Possible use case in this project: The ordered category rules in `EventCategoryResolver` act like a lightweight rule chain, where the first matching rule supplies the category.
