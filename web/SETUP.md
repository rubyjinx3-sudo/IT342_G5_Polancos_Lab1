# React Web Application Setup Guide

## 📁 Project Structure
```
web/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   └── ProtectedRoute.js
│   ├── context/
│   │   └── AuthContext.js
│   ├── pages/
│   │   ├── Register.js
│   │   ├── Login.js
│   │   ├── Dashboard.js
│   │   ├── Auth.css
│   │   └── Dashboard.css
│   ├── services/
│   │   └── api.js
│   ├── App.js
│   ├── App.css
│   ├── index.js
│   └── index.css
├── .gitignore
└── package.json
```

## 🚀 Installation Steps

### 1. Navigate to web folder
```bash
cd IT342_G5_Polancos_Lab1/web
```

### 2. Install dependencies
```bash
npm install
```

### 3. Start the development server
```bash
npm start
```

The app will open at **http://localhost:3000**

## 🔧 Configuration

### API Base URL
The app connects to the backend at `http://localhost:8080/api`

If your backend runs on a different port, update `src/services/api.js`:
```javascript
const API_BASE_URL = 'http://localhost:YOUR_PORT/api';
```

## 📄 Pages

### 1. Register Page (`/register`)
- Full Name input
- Username input
- Email input
- Password input (min 6 characters)
- Link to Login page

### 2. Login Page (`/login`)
- Email input
- Password input
- Link to Register page

### 3. Dashboard Page (`/dashboard`) - Protected
- User profile information
- Logout button
- Account statistics

## 🔐 Authentication Flow

1. **Register**: User creates account → Redirects to Login
2. **Login**: User enters credentials → Session created → Redirects to Dashboard
3. **Dashboard**: Protected route, requires authentication
4. **Logout**: Destroys session → Redirects to Login

## 🎨 Features

✅ Session-based authentication
✅ Protected routes
✅ Global state management (AuthContext)
✅ Error handling
✅ Loading states
✅ Responsive design
✅ Modern UI with gradients

## 🔗 API Endpoints Used

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/user/me` - Get current user (protected)
- `POST /api/auth/logout` - Logout

## ⚠️ Requirements

- Node.js 14+ installed
- Backend must be running on port 8080
- MySQL database configured

## 🐛 Troubleshooting

### CORS Errors
Make sure your backend SecurityConfig allows:
```java
configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
```

### Session Not Persisting
Check that `withCredentials: true` is set in api.js

### 404 Errors
Ensure backend is running before starting the web app
