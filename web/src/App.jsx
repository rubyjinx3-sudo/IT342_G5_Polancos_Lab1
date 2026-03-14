import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import LoginPage from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import EventDetailPage from './pages/EventDetailPage';
import ProfilePage from './pages/ProfilePage';
import OrganizerPage from './pages/OrganizerPage';
import './index.css';

function WithNav({ children }) {
  return (
    <>
      <Navbar />
      <div className="app-shell">
        {children}
      </div>
    </>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<LoginPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route
            path="/dashboard"
            element={(
              <ProtectedRoute requiredRole="student">
                <WithNav><DashboardPage /></WithNav>
              </ProtectedRoute>
            )}
          />

          <Route
            path="/dashboard/events"
            element={(
              <ProtectedRoute requiredRole="student">
                <WithNav><DashboardPage /></WithNav>
              </ProtectedRoute>
            )}
          />

          <Route
            path="/dashboard/my-events"
            element={(
              <ProtectedRoute requiredRole="student">
                <WithNav><DashboardPage /></WithNav>
              </ProtectedRoute>
            )}
          />

          <Route
            path="/dashboard/event/:eventId"
            element={(
              <ProtectedRoute>
                <WithNav><EventDetailPage /></WithNav>
              </ProtectedRoute>
            )}
          />

          <Route
            path="/organizer"
            element={(
              <ProtectedRoute requiredRole="organizer">
                <WithNav><OrganizerPage /></WithNav>
              </ProtectedRoute>
            )}
          />

          <Route
            path="/profile"
            element={(
              <ProtectedRoute>
                <WithNav><ProfilePage /></WithNav>
              </ProtectedRoute>
            )}
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
