import { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import eventService from '../services/eventService';
import { Calendar, Clock, MapPin, ExternalLink, CheckCircle } from 'lucide-react';
import './DashboardPage.css';

const CATEGORY_COLORS = {
  academic: { bg: '#EEF2FF', color: '#4F46E5' },
  cultural: { bg: '#FDF2F8', color: '#BE185D' },
  career: { bg: '#ECFDF5', color: '#059669' },
  technology: { bg: '#ECFEFF', color: '#0F766E' },
  social: { bg: '#FFF7ED', color: '#EA580C' },
  sports: { bg: '#F0FDF4', color: '#16A34A' },
};

const Tag = ({ label }) => {
  const s = CATEGORY_COLORS[label] || CATEGORY_COLORS.social;
  return (
    <span className="event-tag" style={{ background: s.bg, color: s.color }}>
      {label.charAt(0).toUpperCase() + label.slice(1)}
    </span>
  );
};

const EventCard = ({ event }) => (
  <div className="event-card">
    <div className="event-card-img">
      <img src={event.image} alt={event.title} />
    </div>
    <div className="event-card-body">
      <div className="event-card-top">
        <span className="event-card-title">{event.title}</span>
        <Tag label={event.category} />
      </div>
      <div className="event-meta">
        <span><Calendar size={13} /> {formatDate(event.date)}</span>
        <span><Clock size={13} /> {event.timeFormatted}{event.endTimeFormatted ? ` - ${event.endTimeFormatted}` : ''}</span>
        <span><MapPin size={13} /> {event.location}</span>
        {event.department && <span>{event.department}</span>}
      </div>
      <Link to={`/dashboard/event/${event.id}`} className="view-btn">
        View Details <ExternalLink size={13} />
      </Link>
    </div>
  </div>
);

function formatDate(d) {
  if (!d) return '';
  return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

function startOfDay(value) {
  const date = new Date(value);
  date.setHours(0, 0, 0, 0);
  return date;
}

const CATEGORY_OPTIONS = [
  { value: 'all', label: 'All Categories' },
  { value: 'academic', label: 'Academic' },
  { value: 'cultural', label: 'Cultural' },
  { value: 'career', label: 'Career' },
  { value: 'sports', label: 'Sports' },
  { value: 'technology', label: 'Technology' },
  { value: 'social', label: 'Social' },
];

const DEPARTMENT_OPTIONS = [
  { value: 'all', label: 'All Colleges' },
  { value: 'College of Engineering and Architecture', label: 'Engineering and Architecture' },
  { value: 'College of Management, Business and Accountancy', label: 'Management, Business and Accountancy' },
  { value: 'College of Arts, Science, and Education', label: 'Arts, Science, and Education' },
  { value: 'College of Nursing and Allied Health Sciences', label: 'Nursing and Allied Health Sciences' },
  { value: 'College of Computer Studies', label: 'Computer Studies' },
  { value: 'College of Criminal Justice', label: 'Criminal Justice' },
  { value: 'All Colleges', label: 'All Colleges Events' },
];

const DashboardPage = () => {
  const { user } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [allUpcoming, setAllUpcoming] = useState([]);
  const [allRegistered, setAllRegistered] = useState([]);
  const [upcoming, setUpcoming] = useState([]);
  const [myEvents, setMyEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('all');
  const [department, setDepartment] = useState('all');
  const [dateFilter, setDateFilter] = useState('');
  const [successMessage, setSuccessMessage] = useState(location.state?.successMessage || '');

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    if (!location.state?.successMessage) return;
    setSuccessMessage(location.state.successMessage);
    navigate(location.pathname, { replace: true, state: {} });
  }, [location.pathname, location.state, navigate]);

  useEffect(() => {
    const filters = {
      search,
      category,
      department,
      dateFrom: dateFilter || undefined,
      dateTo: dateFilter || undefined,
    };

    setUpcoming(eventService.filterEvents(allUpcoming, filters));
    setMyEvents(eventService.filterEvents(allRegistered, filters));
  }, [allUpcoming, allRegistered, search, category, department, dateFilter]);

  const loadData = async () => {
    try {
      setLoading(true);
      const allEvents = await eventService.getAllEvents();
      const today = startOfDay(new Date());
      const upcomingEvents = eventService.sortEvents(
        allEvents.filter((event) => startOfDay(event.date) >= today),
        'date-asc'
      );
      setAllUpcoming(upcomingEvents);

      const registered = await eventService.getMyRegisteredEvents();
      setAllRegistered(registered);
    } catch (err) {
      console.error('Dashboard load error:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="page-loading">
        <div className="spinner" />
        <p>Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div className="dashboard-page">
      {successMessage && (
        <div className="dashboard-success-banner">
          <div className="dashboard-success-copy">
            <CheckCircle size={18} />
            <span>{successMessage}</span>
          </div>
          <button type="button" className="dashboard-success-dismiss" onClick={() => setSuccessMessage('')}>
            Dismiss
          </button>
        </div>
      )}
      <section className="dashboard-hero">
        <div>
          <div className="dashboard-eyebrow">Overview</div>
          <h1 className="dashboard-hero-title">Welcome back, {user?.fullName?.split(' ')[0] || 'Student'}.</h1>
          <p className="dashboard-hero-copy">
            Track upcoming events, keep your registrations organized, and stay ready for what is next on campus.
          </p>
        </div>
        <div className="dashboard-stats">
          <div className="dashboard-stat-card">
            <span className="dashboard-stat-label">Upcoming</span>
            <strong>{upcoming.length}</strong>
          </div>
          <div className="dashboard-stat-card">
            <span className="dashboard-stat-label">Registered</span>
            <strong>{myEvents.length}</strong>
          </div>
        </div>
      </section>

      <section className="dash-section">
        <div className="section-head">
          <h2 className="section-title">Upcoming Events</h2>
          <Link to="/dashboard/events" className="view-all-link">View All</Link>
        </div>
        <div className="dashboard-filters">
          <div className="filter-field filter-search">
            <label htmlFor="dashboard-search">Search</label>
            <input
              id="dashboard-search"
              type="search"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by event, location, or organizer"
            />
          </div>
          <div className="filter-field">
            <label htmlFor="dashboard-category">Category</label>
            <select
              id="dashboard-category"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
            >
              {CATEGORY_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>
          <div className="filter-field">
            <label htmlFor="dashboard-date">Date</label>
            <input
              id="dashboard-date"
              type="date"
              value={dateFilter}
              onChange={(e) => setDateFilter(e.target.value)}
            />
          </div>
          <div className="filter-field">
            <label htmlFor="dashboard-department">College</label>
            <select
              id="dashboard-department"
              value={department}
              onChange={(e) => setDepartment(e.target.value)}
            >
              {DEPARTMENT_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>
          <button
            type="button"
            className="filter-reset-btn"
            onClick={() => {
              setSearch('');
              setCategory('all');
              setDepartment('all');
              setDateFilter('');
            }}
          >
            Reset
          </button>
        </div>
        {upcoming.length === 0 ? (
          <div className="empty-state">
            <Calendar size={40} className="empty-icon" />
            <p>No upcoming events match the current filters.</p>
          </div>
        ) : (
          <div className="events-grid">
            {upcoming.map((ev) => <EventCard key={ev.id} event={ev} />)}
          </div>
        )}
      </section>

      <section className="dash-section">
        <div className="section-head">
          <h2 className="section-title">My Registered Events</h2>
          <Link to="/dashboard/my-events" className="view-all-link">View All</Link>
        </div>
        {myEvents.length === 0 ? (
          <div className="empty-state card-empty">
            <Calendar size={40} className="empty-icon" />
            <p>No registered events match the current filters.</p>
            <Link to="/dashboard/events" className="browse-btn">Browse Events</Link>
          </div>
        ) : (
          <div className="reg-table-wrap">
            <table className="reg-table">
              <thead>
                <tr><th>Event</th><th>Date</th><th>Status</th><th /></tr>
              </thead>
              <tbody>
                {myEvents.slice(0, 5).map((ev) => (
                  <tr key={ev.id}>
                    <td data-label="Event">
                      <div className="tbl-event-name">{ev.title}</div>
                      <div className="tbl-event-loc">{ev.location}</div>
                    </td>
                    <td data-label="Date" className="tbl-date">{formatDate(ev.date)}</td>
                    <td data-label="Status"><span className="status-badge">Registered</span></td>
                    <td data-label="Action">
                      <Link to={`/dashboard/event/${ev.id}`} className="tbl-view-btn">View</Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
};

export default DashboardPage;
