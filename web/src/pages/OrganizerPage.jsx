import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import eventService from '../services/eventService';
import { Plus, Calendar, Clock, MapPin, Users, X, CheckCircle, AlertCircle } from 'lucide-react';
import './OrganizerPage.css';

const CATEGORY_COLORS = {
  academic: { bg: '#EEF2FF', color: '#4F46E5' },
  cultural: { bg: '#FDF2F8', color: '#BE185D' },
  career:   { bg: '#ECFDF5', color: '#059669' },
  social:   { bg: '#FFF7ED', color: '#EA580C' },
  sports:   { bg: '#F0FDF4', color: '#16A34A' },
};

const inferCategory = (title = '') => {
  if (/tech|hack|academic|science|code/i.test(title)) return 'academic';
  if (/cultur|music|art|festival/i.test(title)) return 'cultural';
  if (/career|fair|job|summit|entrepreneur/i.test(title)) return 'career';
  if (/sport|game|tournament/i.test(title)) return 'sports';
  return 'social';
};

function formatDate(d) {
  if (!d) return '';
  return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

function formatTime(t) {
  if (!t) return '';
  const [h, m] = t.split(':').map(Number);
  const ampm = h >= 12 ? 'PM' : 'AM';
  return `${h % 12 || 12}:${String(m).padStart(2, '0')} ${ampm}`;
}

const OrganizerPage = () => {
  const { user } = useAuth();
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null); // for viewing registrations
  const [registrations, setRegistrations] = useState([]);
  const [regLoading, setRegLoading] = useState(false);
  const [msg, setMsg] = useState(null);

  // Create event form
  const [form, setForm] = useState({
    title: '', description: '', date: '', time: '', endTime: '', location: '',
  });
  const [creating, setCreating] = useState(false);

  useEffect(() => {
    if (user) loadMyEvents();
  }, [user]);

  const loadMyEvents = async () => {
    try {
      setLoading(true);
      const evs = await eventService.getEventsByOrganizer(user.userId);
      setEvents(evs);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateEvent = async (e) => {
    e.preventDefault();
    setMsg(null);
    setCreating(true);
    try {
      await eventService.createEvent({
        ...form,
        organizerId: user.userId,
        organizerName: user.name || user.fullName,
      });
      setMsg({ type: 'success', text: 'Event created successfully!' });
      setForm({ title: '', description: '', date: '', time: '', endTime: '', location: '' });
      setShowForm(false);
      loadMyEvents();
    } catch (err) {
      setMsg({ type: 'error', text: err.response?.data || err.message || 'Failed to create event.' });
    } finally {
      setCreating(false);
    }
  };

  const handleViewRegistrations = async (event) => {
    setSelectedEvent(event);
    setRegLoading(true);
    try {
      const regs = await eventService.getEventRegistrations(event.id);
      setRegistrations(regs);
    } catch {
      setRegistrations([]);
    } finally {
      setRegLoading(false);
    }
  };

  const setF = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  if (loading) return (
    <div className="page-loading">
      <div className="spinner" />
      <p>Loading your events...</p>
    </div>
  );

  return (
    <div className="organizer-page">
      <div className="organizer-header">
        <div>
          <h2 className="organizer-title">My Events</h2>
          <p className="organizer-subtitle">Manage events you've created</p>
        </div>
        <button className="create-btn" onClick={() => { setShowForm(true); setMsg(null); }}>
          <Plus size={16} /> Create Event
        </button>
      </div>

      {msg && (
        <div className={`org-msg ${msg.type === 'success' ? 'msg-success' : 'msg-error'}`}>
          {msg.type === 'success' ? <CheckCircle size={15} /> : <AlertCircle size={15} />}
          <span>{msg.text}</span>
        </div>
      )}

      {/* Create Event Form */}
      {showForm && (
        <div className="create-event-card">
          <div className="create-card-header">
            <h3 className="create-card-title">Create New Event</h3>
            <button className="close-btn" onClick={() => setShowForm(false)}><X size={18} /></button>
          </div>
          <form onSubmit={handleCreateEvent} className="create-form">
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Event Title *</label>
                <input className="form-input" placeholder="Enter event title"
                  value={form.title} onChange={setF('title')} required disabled={creating} />
              </div>
              <div className="form-group">
                <label className="form-label">Location *</label>
                <input className="form-input" placeholder="e.g. Main Auditorium"
                  value={form.location} onChange={setF('location')} required disabled={creating} />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Date *</label>
                <input type="date" className="form-input"
                  value={form.date} onChange={setF('date')} required disabled={creating} />
              </div>
              <div className="form-group">
                <label className="form-label">Start Time *</label>
                <input type="time" className="form-input"
                  value={form.time} onChange={setF('time')} required disabled={creating} />
              </div>
              <div className="form-group">
                <label className="form-label">End Time</label>
                <input type="time" className="form-input"
                  value={form.endTime} onChange={setF('endTime')} disabled={creating} />
              </div>
            </div>
            <div className="form-group">
              <label className="form-label">Description</label>
              <textarea className="form-input form-textarea" placeholder="Describe your event..."
                value={form.description} onChange={setF('description')} disabled={creating} rows={3} />
            </div>
            <div className="create-actions">
              <button type="button" className="cancel-btn" onClick={() => setShowForm(false)}>Cancel</button>
              <button type="submit" className="submit-btn" disabled={creating}>
                {creating ? 'Creating...' : 'Create Event'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Events List */}
      {events.length === 0 ? (
        <div className="empty-state">
          <Calendar size={40} className="empty-icon" />
          <p>You haven't created any events yet.</p>
          <button className="create-btn" onClick={() => setShowForm(true)}>
            <Plus size={15} /> Create Your First Event
          </button>
        </div>
      ) : (
        <div className="org-events-grid">
          {events.map((ev) => {
            const cat = inferCategory(ev.title);
            const catStyle = CATEGORY_COLORS[cat] || CATEGORY_COLORS.social;
            return (
              <div key={ev.id} className="org-event-card">
                <div className="org-event-img">
                  <img src={ev.image} alt={ev.title} />
                  <span className="org-event-tag" style={{ background: catStyle.bg, color: catStyle.color }}>
                    {cat.charAt(0).toUpperCase() + cat.slice(1)}
                  </span>
                </div>
                <div className="org-event-body">
                  <h3 className="org-event-title">{ev.title}</h3>
                  <div className="org-event-meta">
                    <span><Calendar size={13} /> {formatDate(ev.date)}</span>
                    <span><Clock size={13} /> {formatTime(ev.time)}</span>
                    <span><MapPin size={13} /> {ev.location}</span>
                  </div>
                  <button
                    className="view-regs-btn"
                    onClick={() => handleViewRegistrations(ev)}
                  >
                    <Users size={14} /> View Registrations
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Registrations Panel */}
      {selectedEvent && (
        <div className="regs-panel">
          <div className="regs-panel-header">
            <h3 className="regs-panel-title">
              Registrations — <span>{selectedEvent.title}</span>
            </h3>
            <button className="close-btn" onClick={() => setSelectedEvent(null)}><X size={18} /></button>
          </div>
          {regLoading ? (
            <div className="reg-loading"><div className="spinner" /> Loading...</div>
          ) : registrations.length === 0 ? (
            <div className="empty-state" style={{ padding: '2rem' }}>
              <Users size={32} className="empty-icon" />
              <p>No registrations yet.</p>
            </div>
          ) : (
            <div className="regs-table-wrap">
              <table className="regs-table">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Student Name</th>
                    <th>Email</th>
                    <th>Registered At</th>
                  </tr>
                </thead>
                <tbody>
                  {registrations.map((reg, i) => (
                    <tr key={reg.id}>
                      <td>{i + 1}</td>
                      <td>{reg.studentName || '—'}</td>
                      <td>{reg.studentEmail || '—'}</td>
                      <td>{reg.registeredAt
                        ? new Date(reg.registeredAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
                        : '—'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default OrganizerPage;
