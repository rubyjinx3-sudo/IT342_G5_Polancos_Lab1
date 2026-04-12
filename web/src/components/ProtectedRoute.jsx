import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children, requiredRole }) {
  const { user } = useAuth();
  const role = user?.role?.toLowerCase();
  const isAdmin = role === 'admin' || role === 'organizer';

  if (!user) return <Navigate to="/" replace />;

  if (requiredRole?.toLowerCase() === 'admin' && !isAdmin) {
    return <Navigate to="/dashboard" replace />;
  }

  if (requiredRole?.toLowerCase() === 'student' && role !== 'student') {
    return <Navigate to={isAdmin ? '/admin' : '/dashboard'} replace />;
  }

  if (requiredRole && !['admin', 'student'].includes(requiredRole.toLowerCase()) && role !== requiredRole.toLowerCase()) {
    return <Navigate to={isAdmin ? '/admin' : '/dashboard'} replace />;
  }

  return children;
}
