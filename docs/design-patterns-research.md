# Research on Software Design Patterns

## Introduction
Software design patterns are common solutions to recurring problems in software development. They help organize code better and make systems easier to maintain, reuse, and extend.

Design patterns are usually grouped into three categories:
- Creational patterns
- Structural patterns
- Behavioral patterns

For this research, I selected two patterns from each category.

## Creational Patterns

### 1. Factory Pattern
**Category:** Creational

**Problem it solves:**
Sometimes object creation becomes repeated in many parts of the system. This can make the code harder to manage.

**How it works:**
A factory class is used to create objects in one place. Instead of building an object directly inside a service or controller, the class asks the factory to create it.

**Real-world example:**
In a backend system, a registration service may create user accounts with default roles and encoded passwords.

**Possible use case in this project:**
In the Campus Event Tracker project, a `UserFactory` can be used to create `User` objects during registration.

### 2. Builder Pattern
**Category:** Creational

**Problem it solves:**
Some objects have many fields, especially optional ones. Creating them with many constructor arguments or repeated setter calls can become confusing.

**How it works:**
The builder pattern creates an object step by step before returning the final version.

**Real-world example:**
A mobile app notification object may include title, message, image, link, and priority. A builder can organize that creation process.

**Possible use case in this project:**
An `EventBuilder` could be used in the future if event creation becomes more complex and needs safer step-by-step construction.

## Structural Patterns

### 3. Adapter Pattern
**Category:** Structural

**Problem it solves:**
Sometimes one class does not match the format needed by another part of the system.

**How it works:**
An adapter converts one object or interface into another format that the system can use more easily.

**Real-world example:**
A backend may store a `User` entity internally but return a different DTO format to the frontend.

**Possible use case in this project:**
In this project, adapters can convert a `User` object into login and profile response objects.

### 4. Decorator Pattern
**Category:** Structural

**Problem it solves:**
There are cases where extra behavior needs to be added to an object without changing its original structure too much.

**How it works:**
A decorator wraps another object and adds behavior before or after the original action.

**Real-world example:**
A registration record may be enriched with student details only for organizer viewing.

**Possible use case in this project:**
A decorator can be used to attach student name and email to registration records when an organizer checks event participants.

## Behavioral Patterns

### 5. Strategy Pattern
**Category:** Behavioral

**Problem it solves:**
A system may need several possible ways to perform the same task.

**How it works:**
Each algorithm or rule is placed in its own class, and the main system chooses which one to use through a shared interface.

**Real-world example:**
An application can classify items using different matching rules depending on the data.

**Possible use case in this project:**
The Campus Event Tracker can use strategy classes to assign categories to events based on event titles.

### 6. Chain of Responsibility Pattern
**Category:** Behavioral

**Problem it solves:**
Sometimes a request should pass through several checks until one of them handles it.

**How it works:**
Handlers are arranged in sequence. If one handler cannot process the request, it passes it to the next handler.

**Real-world example:**
Validation, request filtering, and classification pipelines often use this pattern.

**Possible use case in this project:**
Category rules for events can be checked one by one until the first matching rule is found.

## Conclusion
Software design patterns are useful because they provide organized solutions to common development problems. They also help make code more understandable and maintainable.

In this project, patterns such as Factory, Adapter, Strategy, and Decorator are especially useful because they fit naturally with the backend service layer and help separate responsibilities more clearly.
