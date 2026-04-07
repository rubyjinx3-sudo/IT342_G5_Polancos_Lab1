# Design Patterns Refactoring Report

## Project
Campus Event Tracker

## Before vs After

### Original implementation
- `AuthService` created `User` objects directly and also built multiple response DTOs itself.
- `EventService` handled event metadata defaults, category inference, and registration enrichment in one large service class.
- Business rules were embedded as conditional logic, making the service layer harder to extend and explain.

### Problems in the original code
- Low cohesion because object creation, mapping, normalization, and enrichment were all mixed together.
- Repeated mapping logic increased maintenance cost.
- Event categorization rules were tightly coupled to `EventService`.
- Registration enrichment depended on manual loops inside the service method instead of a reusable abstraction.

### Refactored implementation
- User creation now goes through `UserFactory`.
- User-to-response conversion now goes through `AuthResponseAdapter` and `UserProfileAdapter`.
- Event metadata normalization now uses `EventMetadataService`.
- Event category resolution now uses rule-based strategies in `EventCategoryResolver`.
- Organizer registration enrichment now uses `StudentRegistrationViewDecorator`.

## Applied Design Patterns

### 1. Factory Pattern
- Applied in: `backend/src/main/java/com/lab2/authsystem/service/auth/UserFactory.java`
- Where it was used: Registration flow in `AuthService`.
- Justification: User creation required consistent password encoding and safe role parsing.
- Improvement: Centralized creation logic, reduced duplication, and made registration behavior easier to extend.

### 2. Adapter Pattern
- Applied in:
  - `backend/src/main/java/com/lab2/authsystem/service/auth/AuthResponseAdapter.java`
  - `backend/src/main/java/com/lab2/authsystem/service/auth/UserProfileAdapter.java`
- Where it was used: `AuthService` response generation.
- Justification: `User` is a domain entity, while API responses need DTO-specific shapes.
- Improvement: Separated domain logic from presentation mapping and simplified service methods.

### 3. Strategy Pattern
- Applied in:
  - `backend/src/main/java/com/lab2/authsystem/service/event/CategoryRule.java`
  - `backend/src/main/java/com/lab2/authsystem/service/event/RegexCategoryRule.java`
  - `backend/src/main/java/com/lab2/authsystem/service/event/EventCategoryResolver.java`
- Where it was used: Event category inference.
- Justification: Category matching is an algorithm that may change as more event types are added.
- Improvement: New category rules can be added with minimal changes to the service layer.

### 4. Chain of Responsibility Pattern
- Applied in: `backend/src/main/java/com/lab2/authsystem/service/event/EventCategoryResolver.java`
- Where it was used: Ordered event category resolution.
- Justification: Category rules are evaluated in sequence until one matches.
- Improvement: The classification flow is easier to read, test, and extend than a long `if` chain.

### 5. Decorator Pattern
- Applied in:
  - `backend/src/main/java/com/lab2/authsystem/service/event/RegistrationViewDecorator.java`
  - `backend/src/main/java/com/lab2/authsystem/service/event/StudentRegistrationViewDecorator.java`
- Where it was used: Organizer-facing registration enrichment.
- Justification: Registration data needed extra student details only in some views.
- Improvement: Added enrichment behavior without overloading the base registration retrieval logic.

## Code Snippets

### Before
```java
User user = new User();
user.setFullName(request.getFullName());
user.setEmail(request.getEmail());
user.setPassword(passwordEncoder.encode(request.getPassword()));
```

### After
```java
User user = userFactory.createFrom(request);
```

### Before
```java
if (safeTitle.matches("(?i).*(tech|hack|science|code|program|robot|innovation|digital|ict).*")) {
    return "technology";
}
```

### After
```java
return categoryRules.stream()
    .filter(rule -> rule.matches(title))
    .map(CategoryRule::getCategory)
    .findFirst()
    .orElse(DEFAULT_CATEGORY);
```

## Testing
- Added unit tests for `UserFactory`.
- Added unit tests for `EventCategoryResolver`.
- Added unit tests for `EventMetadataService`.

## Summary of Improvements
- Better separation of concerns in the backend service layer.
- Lower duplication in user creation and response mapping.
- Easier extension of event categorization logic.
- Cleaner and more maintainable organizer registration enrichment.
