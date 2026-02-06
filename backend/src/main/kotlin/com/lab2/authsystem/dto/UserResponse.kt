package com.lab2.authsystem.dto

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val createdAt: String
)
```

---

## **Final Folder Structure Should Look Like:**
```
backend/src/main/kotlin/com/lab2/authsystem/
├── AuthSystemApplication.kt  (already exists)
├── model/
│   └── User.kt  ✅ NEW
├── repository/
│   └── UserRepository.kt  ✅ NEW
└── dto/
    ├── RegisterRequest.kt  ✅ NEW
    ├── LoginRequest.kt  ✅ NEW
    ├── AuthResponse.kt  ✅ NEW
    └── UserResponse.kt  ✅ NEW