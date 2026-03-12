import axios from 'axios';

const API = 'http://localhost:8080/api';

const getUser = () => JSON.parse(localStorage.getItem('ced_user') || 'null');

const api = axios.create({ baseURL: API });

// ── Helpers ───────────────────────────────────────────────────────────────────

const EVENT_IMAGES = [
  'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800&q=80',
  'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800&q=80',
  'https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=800&q=80',
  'https://images.unsplash.com/photo-1531058020387-3be344556be6?w=800&q=80',
  'https://images.unsplash.com/photo-1505373877841-8d25f7d46678?w=800&q=80',
  'https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=800&q=80',
];

const inferCategory = (title = '') => {
  if (/tech|hack|academic|science|code|program/i.test(title)) return 'academic';
  if (/cultur|music|art|festival|dance|perform/i.test(title)) return 'cultural';
  if (/career|fair|job|summit|entrepreneur|business/i.test(title)) return 'career';
  if (/sport|game|tournament|athletic/i.test(title)) return 'sports';
  return 'social';
};

const formatTime = (t) => {
  if (!t) return '';
  const [h, m] = t.split(':').map(Number);
  const ampm = h >= 12 ? 'PM' : 'AM';
  return `${h % 12 || 12}:${String(m).padStart(2, '0')} ${ampm}`;
};

// Enrich a backend event with UI-friendly fields
const enrich = (event) => ({
  ...event,
  category: inferCategory(event.title),
  image: EVENT_IMAGES[(Number(event.id) - 1) % EVENT_IMAGES.length],
  timeFormatted: formatTime(event.time),
  endTimeFormatted: event.endTime ? formatTime(event.endTime) : null,
});

// ── Service ───────────────────────────────────────────────────────────────────

const eventService = {
  // Get all events
  getAllEvents: async () => {
    const res = await api.get('/events');
    return res.data.map(enrich);
  },

  // Get single event by ID
  getEventById: async (id) => {
    const res = await api.get(`/events/${id}`);
    return enrich(res.data);
  },

  // Get events created by organizer
  getEventsByOrganizer: async (userId) => {
    const res = await api.get(`/events/organizer/${userId}`);
    return res.data.map(enrich);
  },

  // Create a new event (organizer)
  createEvent: async (eventData) => {
    const res = await api.post('/events', eventData);
    return enrich(res.data);
  },

  // Register current user for an event
  registerForEvent: async (eventId) => {
    const user = getUser();
    const res = await api.post('/registrations', {
      userId: user.userId,
      eventId,
    });
    return res.data;
  },

  // Get registrations for current user (returns Registration[])
  getMyRegistrations: async () => {
    const user = getUser();
    const res = await api.get(`/registrations/user/${user.userId}`);
    return res.data;
  },

  // Get registered events with full event details for current user
  getMyRegisteredEvents: async () => {
    const user = getUser();
    // Fetch registrations + all events in parallel
    const [regsRes, eventsRes] = await Promise.all([
      api.get(`/registrations/user/${user.userId}`),
      api.get('/events'),
    ]);
    const regs = regsRes.data;
    const allEvents = eventsRes.data;

    return regs
      .map((reg) => {
        const event = allEvents.find((e) => e.id === reg.eventId);
        return event ? { ...enrich(event), registrationId: reg.id, registeredAt: reg.registeredAt } : null;
      })
      .filter(Boolean);
  },

  // Get registered students for an event (organizer use)
  getEventRegistrations: async (eventId) => {
    const res = await api.get(`/registrations/event/${eventId}`);
    return res.data;
  },

  // Check if current user is registered for a specific event
  isRegistered: async (eventId) => {
    const user = getUser();
    const res = await api.get(`/registrations/check?userId=${user.userId}&eventId=${eventId}`);
    return res.data; // boolean
  },


    // Cancel registration for current user   ← ADD THIS
  cancelRegistration: async (eventId) => {
    const user = getUser();
    const res = await api.delete(`/registrations?userId=${user.userId}&eventId=${eventId}`);
    return res.data;
  },
  // ── Client-side filter & sort helpers ─────────────────

  filterEvents: (events, { search = '', category = 'all', dateFrom, dateTo } = {}) => {
    return events.filter((e) => {
      const matchSearch =
        !search ||
        e.title?.toLowerCase().includes(search.toLowerCase()) ||
        e.location?.toLowerCase().includes(search.toLowerCase()) ||
        e.organizerName?.toLowerCase().includes(search.toLowerCase());

      const matchCategory = category === 'all' || e.category === category;
      const matchFrom = !dateFrom || new Date(e.date) >= new Date(dateFrom);
      const matchTo = !dateTo || new Date(e.date) <= new Date(dateTo);

      return matchSearch && matchCategory && matchFrom && matchTo;
    });
  },

  sortEvents: (events, sortBy = 'date-asc') => {
    const sorted = [...events];
    if (sortBy === 'date-asc') return sorted.sort((a, b) => new Date(a.date) - new Date(b.date));
    if (sortBy === 'date-desc') return sorted.sort((a, b) => new Date(b.date) - new Date(a.date));
    if (sortBy === 'title') return sorted.sort((a, b) => a.title.localeCompare(b.title));
    return sorted;
  },

  getUpcomingEvents: (events, limit = 6) => {
    const today = new Date();
    return events
      .filter((e) => new Date(e.date) >= today)
      .sort((a, b) => new Date(a.date) - new Date(b.date))
      .slice(0, limit);
  },
};

export default eventService;