# Design Patterns Refactoring Report

## Project
Campus Event Tracker

## Before vs After Description

### Original implementation
The original backend was functional, but some responsibilities were mixed inside the same service classes.

In `AuthService`, the service directly created `User` objects during registration and also prepared response data for the API.

In `EventService`, the service handled event category checking, metadata defaults, and registration enrichment in one class.

### Problems in the original implementation
- The service classes were getting longer and harder to read.
- Some logic was tightly coupled to a single class.
- If I wanted to change user creation or event categorization, I had to edit the service methods directly.
- Reusable logic was not separated clearly.

### Refactored implementation
I refactored the backend service layer by moving some responsibilities into smaller helper classes. The goal was to keep the same system behavior while improving code organization and maintainability.

The main service classes still control the business flow, but repeated or separate logic was extracted into dedicated classes.

## Applied Design Patterns

### 1. Factory Pattern
**Name of pattern:** Factory Pattern

**Where it was applied:** User registration flow in `AuthService`

**Files involved:**
- `backend/src/main/java/com/lab2/authsystem/service/AuthService.java`
- `backend/src/main/java/com/lab2/authsystem/service/auth/UserFactory.java`

**Justification:**
During registration, the system needed to create a `User` object, assign a default role when needed, and encode the password. Instead of doing all of this directly inside `AuthService`, I moved the object creation logic into `UserFactory`.

**Improvement it brought:**
This made `AuthService` shorter and made user creation easier to maintain in one place.

**Code snippet:**
```java
User user = userFactory.createFrom(request);
```

### 2. Adapter Pattern
**Name of pattern:** Adapter Pattern

**Where it was applied:** User response formatting

**Files involved:**
- `backend/src/main/java/com/lab2/authsystem/service/auth/AuthResponseAdapter.java`
- `backend/src/main/java/com/lab2/authsystem/service/auth/UserProfileAdapter.java`

**Justification:**
The internal `User` entity is not exactly the same as the response format needed by the frontend. I used adapter classes to convert `User` into the response DTOs in a cleaner way.

**Improvement it brought:**
This separated domain logic from response formatting and reduced repeated mapping code inside the service.

**Code snippet:**
```java
return authResponseAdapter.adapt(user, "Login successful");
```

### 3. Strategy Pattern
**Name of pattern:** Strategy Pattern

**Where it was applied:** Event category matching

**Files involved:**
- `backend/src/main/java/com/lab2/authsystem/service/EventService.java`
- `backend/src/main/java/com/lab2/authsystem/service/event/EventCategoryResolver.java`
- `backend/src/main/java/com/lab2/authsystem/service/event/CategoryRule.java`

**Justification:**
The project needed a way to assign event categories based on the event title. Instead of keeping all matching logic inside `EventService`, I moved it into rule-based classes.

**Improvement it brought:**
This made the category logic easier to update and easier to read.

**Code snippet:**
```java
event.setCategory(categoryResolver.resolve(event.getTitle()));
```

### 4. Decorator Pattern
**Name of pattern:** Decorator Pattern

**Where it was applied:** Registration enrichment for organizer view

**Files involved:**
- `backend/src/main/java/com/lab2/authsystem/service/event/RegistrationViewDecorator.java`
- `backend/src/main/java/com/lab2/authsystem/service/event/StudentRegistrationViewDecorator.java`

**Justification:**
For organizer viewing, registration records needed extra student details like name and email. I used a decorator so that the additional behavior could be added without putting everything directly into `EventService`.

**Improvement it brought:**
This kept the registration retrieval logic cleaner and made the enrichment behavior easier to isolate.

**Code snippet:**
```java
return registrationView.build(registrationRepository.findByEventId(eventId));
```

## Summary
The refactoring did not change the main purpose of the system, but it improved the structure of the backend code.

The main improvements were:
- cleaner service classes
- better separation of concerns
- easier maintenance of user creation and event categorization logic
- improved readability of the backend implementation

## Files I Focused On
These were the main files involved in the refactor:
- `backend/src/main/java/com/lab2/authsystem/service/AuthService.java`
- `backend/src/main/java/com/lab2/authsystem/service/auth/UserFactory.java`
- `backend/src/main/java/com/lab2/authsystem/service/auth/AuthResponseAdapter.java`
- `backend/src/main/java/com/lab2/authsystem/service/EventService.java`
- `backend/src/main/java/com/lab2/authsystem/service/event/EventCategoryResolver.java`
- `backend/src/main/java/com/lab2/authsystem/service/event/StudentRegistrationViewDecorator.java`
