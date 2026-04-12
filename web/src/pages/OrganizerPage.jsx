import { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import eventService from '../services/eventService';
import { uploadEventImage } from '../services/supabaseStorage';
import { Plus, Calendar, Clock, MapPin, Users, X, CheckCircle, AlertCircle, Pencil } from 'lucide-react';
import './OrganizerPage.css';

const CATEGORY_COLORS = {
  academic: { bg: '#EEF2FF', color: '#4F46E5' },
  cultural: { bg: '#FDF2F8', color: '#BE185D' },
  career:   { bg: '#ECFDF5', color: '#059669' },
  technology: { bg: '#ECFEFF', color: '#0F766E' },
  social:   { bg: '#FFF7ED', color: '#EA580C' },
  sports:   { bg: '#F0FDF4', color: '#16A34A' },
};

const CATEGORY_OPTIONS = [
  { value: 'academic', label: 'Academic', template: { title: 'Research Seminar 2026', description: 'Academic seminar for students and faculty focused on current research, learning, and professional development.' } },
  { value: 'cultural', label: 'Cultural', template: { title: 'Buwan ng Wika Cultural Festival', description: 'Cultural celebration featuring performances, exhibits, and student participation across colleges.' } },
  { value: 'career', label: 'Career', template: { title: 'Career Fair 2026', description: 'Career development event with recruiters, alumni speakers, and internship opportunities.' } },
  { value: 'sports', label: 'Sports', template: { title: 'Inter-College Sports Tournament', description: 'Campus sports event with inter-college participation and athletic competitions.' } },
  { value: 'technology', label: 'Technology', template: { title: 'Hackathon and Innovation Expo', description: 'Technology-focused event featuring coding, product demos, and digital innovation.' } },
  { value: 'social', label: 'Social', template: { title: 'Campus Social Mixer', description: 'General campus social event for student engagement, networking, and community building.' } },
];

const DEPARTMENT_OPTIONS = [
  'College of Engineering and Architecture',
  'College of Management, Business and Accountancy',
  'College of Arts, Science, and Education',
  'College of Nursing and Allied Health Sciences',
  'College of Computer Studies',
  'College of Criminal Justice',
  'All Colleges',
];

const createInitialForm = () => ({
  title: '',
  description: '',
  date: '',
  time: '',
  endTime: '',
  location: '',
  category: CATEGORY_OPTIONS[0].value,
  department: DEPARTMENT_OPTIONS[0],
});

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

function getErrorMessage(err, fallback) {
  const responseData = err?.response?.data;

  if (typeof responseData === 'string' && responseData.trim()) {
    return responseData;
  }

  if (responseData && typeof responseData === 'object') {
    if (typeof responseData.message === 'string' && responseData.message.trim()) {
      return responseData.message;
    }
    if (typeof responseData.error === 'string' && responseData.error.trim()) {
      return responseData.error;
    }
  }

  if (typeof err?.message === 'string' && err.message.trim()) {
    return err.message;
  }

  return fallback;
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
  const [form, setForm] = useState(createInitialForm);
  const [editingEventId, setEditingEventId] = useState(null);
  const [existingImageUrl, setExistingImageUrl] = useState('');
  const [selectedImageFile, setSelectedImageFile] = useState(null);
  const [imagePreview, setImagePreview] = useState('');
  const [creating, setCreating] = useState(false);

  const loadMyEvents = useCallback(async () => {
    if (!user) return;

    try {
      setLoading(true);
      const evs = await eventService.getEventsByOrganizer(user.userId);
      setEvents(evs);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    loadMyEvents();
  }, [loadMyEvents]);

  const resetForm = () => {
    setForm(createInitialForm());
    setEditingEventId(null);
    setExistingImageUrl('');
    setSelectedImageFile(null);
    setImagePreview('');
  };

  const handleSaveEvent = async (e) => {
    e.preventDefault();
    setMsg(null);

    if (form.endTime && form.time && form.endTime <= form.time) {
      setMsg({ type: 'error', text: 'End time must be later than the start time.' });
      return;
    }

    setCreating(true);
    try {
      let imageUrl = existingImageUrl;
      if (selectedImageFile) {
        imageUrl = await uploadEventImage(
          selectedImageFile,
          `${form.category}-${form.department}-${form.title}`
        );
      }

      const payload = {
        ...form,
        imageUrl,
        organizerId: user.userId,
        organizerName: form.department,
      };

      if (editingEventId) {
        await eventService.updateEvent(editingEventId, payload);
        setMsg({ type: 'success', text: 'Event updated successfully.' });
      } else {
        await eventService.createEvent(payload);
        setMsg({ type: 'success', text: 'Event created successfully.' });
      }

      resetForm();
      setShowForm(false);
      loadMyEvents();
    } catch (err) {
      setMsg({ type: 'error', text: getErrorMessage(err, 'Failed to save event.') });
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
  const applyCategoryTemplate = (category) => {
    const selected = CATEGORY_OPTIONS.find((option) => option.value === category);
    if (!selected) return;
    setForm((current) => ({
      ...current,
      category,
      title: current.title || selected.template.title,
      description: current.description || selected.template.description,
    }));
  };

  const handleImageChange = (e) => {
    const file = e.target.files?.[0];
    setSelectedImageFile(file || null);
    setImagePreview(file ? URL.createObjectURL(file) : existingImageUrl);
  };

  const handleEditEvent = (event) => {
    setEditingEventId(event.id);
    setExistingImageUrl(event.imageUrl || '');
    setSelectedImageFile(null);
    setImagePreview(event.imageUrl || '');
    setForm({
      title: event.title || '',
      description: event.description || '',
      date: event.date || '',
      time: event.time || '',
      endTime: event.endTime || '',
      location: event.location || '',
      category: event.category || CATEGORY_OPTIONS[0].value,
      department: event.department || DEPARTMENT_OPTIONS[0],
    });
    setMsg(null);
    setShowForm(true);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

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
          <h2 className="organizer-title">Admin Events</h2>
          <p className="organizer-subtitle">Manage events for all departments</p>
        </div>
        <button className="create-btn" onClick={() => { resetForm(); setShowForm(true); setMsg(null); }}>
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
            <h3 className="create-card-title">{editingEventId ? 'Edit Event' : 'Create New Event'}</h3>
            <button className="close-btn" onClick={() => { setShowForm(false); resetForm(); }}><X size={18} /></button>
          </div>
          <form onSubmit={handleSaveEvent} className="create-form">
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
                <label className="form-label">Category *</label>
                <select
                  className="form-input"
                  value={form.category}
                  onChange={(e) => applyCategoryTemplate(e.target.value)}
                  required
                  disabled={creating}
                >
                  {CATEGORY_OPTIONS.map((category) => (
                    <option key={category.value} value={category.value}>{category.label}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Event Image</label>
                <input
                  type="file"
                  accept="image/*"
                  className="form-input"
                  onChange={handleImageChange}
                  disabled={creating}
                />
              </div>
            </div>
            {imagePreview && (
              <div className="event-image-preview">
                <img src={imagePreview} alt="Event preview" />
              </div>
            )}
            <div className="form-group">
              <label className="form-label">College / Department *</label>
              <select
                className="form-input"
                value={form.department}
                onChange={setF('department')}
                required
                disabled={creating}
              >
                {DEPARTMENT_OPTIONS.map((department) => (
                  <option key={department} value={department}>{department}</option>
                ))}
              </select>
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
              <button type="button" className="cancel-btn" onClick={() => { setShowForm(false); resetForm(); }}>Cancel</button>
              <button type="submit" className="submit-btn" disabled={creating}>
                {creating ? (editingEventId ? 'Saving...' : 'Creating...') : (editingEventId ? 'Save Changes' : 'Create Event')}
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
          <button className="create-btn" onClick={() => { resetForm(); setShowForm(true); }}>
            <Plus size={15} /> Create Your First Event
          </button>
        </div>
      ) : (
        <div className="org-events-grid">
          {events.map((ev) => {
            const cat = ev.category || 'social';
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
                    {ev.department && <span>{ev.department}</span>}
                  </div>
                  <button
                    className="edit-event-btn"
                    onClick={() => handleEditEvent(ev)}
                  >
                    <Pencil size={14} /> Edit Event
                  </button>
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
