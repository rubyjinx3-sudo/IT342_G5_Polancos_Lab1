import { PROFILE_PREFS_KEY } from '../config/appConfig';

const readPreferences = () => {
  try {
    return JSON.parse(localStorage.getItem(PROFILE_PREFS_KEY) || '{}');
  } catch {
    return {};
  }
};

const writePreferences = (preferences) => {
  localStorage.setItem(PROFILE_PREFS_KEY, JSON.stringify(preferences));
};

export const getProfilePreferences = (email) => {
  if (!email) return {};
  const preferences = readPreferences();
  return preferences[email] || {};
};

export const saveProfilePreferences = (email, values) => {
  if (!email) return {};
  const preferences = readPreferences();
  preferences[email] = { ...(preferences[email] || {}), ...values };
  writePreferences(preferences);
  return preferences[email];
};
