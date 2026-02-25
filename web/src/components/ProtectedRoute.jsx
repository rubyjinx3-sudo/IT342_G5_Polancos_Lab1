import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children, requiredRole }) {
  const { user } = useAuth();

  // Not logged in → go to login page
  if (!user) return <Navigate to="/" replace />;

  // ✅ FIX: normalize both sides to lowercase before comparing
  if (requiredRole && user.role?.toLowerCase() !== requiredRole.toLowerCase()) {
    return <Navigate to={user.role?.toLowerCase() === 'organizer' ? '/organizer' : '/dashboard'} replace />;
  }

  return children;
}
