import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import eventService from '../services/eventService';
import { ArrowLeft, Calendar, Clock, MapPin, User, CheckCircle, AlertCircle } from 'lucide-react';
import './EventDetailPage.css';

const CATEGORY_COLORS = {
  academic: { bg: '#EEF2FF', color: '#4F46E5' },
  cultural: { bg: '#FDF2F8', color: '#BE185D' },
  career:   { bg: '#ECFDF5', color: '#059669' },
  social:   { bg: '#FFF7ED', color: '#EA580C' },
  sports:   { bg: '#F0FDF4', color: '#16A34A' },
};

function formatDate(d) {
  if (!d) return '';
  return new Date(d).toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' });
}

const EventDetailPage = () => {
  const { eventId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [event, setEvent] = useState(null);
  const [isRegistered, setIsRegistered] = useState(false);
  const [loading, setLoading] = useState(true);
  const [registering, setRegistering] = useState(false);
  const [regError, setRegError] = useState('');

  useEffect(() => {
    load();
  }, [eventId]);

  const load = async () => {
    try {
      setLoading(true);
      const [ev, registered] = await Promise.all([
        eventService.getEventById(eventId),
        user ? eventService.isRegistered(eventId) : Promise.resolve(false),
      ]);
      setEvent(ev);
      setIsRegistered(registered);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async () => {
    if (!user) { navigate('/'); return; }
    setRegError('');
    setRegistering(true);
    try {
      await eventService.registerForEvent(eventId);
      setIsRegistered(true);
    } catch (err) {
      setRegError(err.response?.data || err.message || 'Registration failed.');
    } finally {
      setRegistering(false);
    }
  };

  if (loading) return (
    <div className="page-loading">
      <div className="spinner" />
      <p>Loading event...</p>
    </div>
  );

  if (!event) return (
    <div className="not-found">
      <h2>Event not found</h2>
      <button className="back-btn" onClick={() => navigate(-1)}>← Go Back</button>
    </div>
  );

  const catStyle = CATEGORY_COLORS[event.category] || CATEGORY_COLORS.social;

  return (
    <div className="event-detail-page">
      <button className="back-btn" onClick={() => navigate(-1)}>
        <ArrowLeft size={16} /> Back
      </button>

      <div className="event-detail-card">
        {event.image && (
          <div className="detail-img-wrap">
            <img src={event.image} alt={event.title} />
          </div>
        )}

        <div className="detail-content">
          <div className="detail-header">
            <h1 className="detail-title">{event.title}</h1>
            <span className="detail-tag" style={{ background: catStyle.bg, color: catStyle.color }}>
              {event.category.charAt(0).toUpperCase() + event.category.slice(1)}
            </span>
          </div>

          <div className="detail-meta">
            <div className="meta-item">
              <Calendar size={16} className="meta-icon" />
              <span>{formatDate(event.date)}</span>
            </div>
            <div className="meta-item">
              <Clock size={16} className="meta-icon" />
              <span>
                {event.timeFormatted || event.time}
                {event.endTimeFormatted ? ` – ${event.endTimeFormatted}` : ''}
              </span>
            </div>
            <div className="meta-item">
              <MapPin size={16} className="meta-icon" />
              <span>{event.location}</span>
            </div>
            {event.organizerName && (
              <div className="meta-item">
                <User size={16} className="meta-icon" />
                <span>Organized by {event.organizerName}</span>
              </div>
            )}
          </div>

          {event.description && (
            <div className="detail-description">
              <h3 className="desc-heading">About This Event</h3>
              <p>{event.description}</p>
            </div>
          )}

          {/* Register / Already registered */}
          <div className="detail-action">
            {regError && (
              <div className="reg-error">
                <AlertCircle size={15} />
                <span>{regError}</span>
              </div>
            )}
            {isRegistered ? (
              <div className="registered-box">
                <CheckCircle size={20} className="check-icon" />
                <span>You're registered for this event!</span>
              </div>
            ) : (
              <button
                className="register-btn"
                onClick={handleRegister}
                disabled={registering}
              >
                {registering ? 'Registering...' : 'Register for Event'}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default EventDetailPage;
