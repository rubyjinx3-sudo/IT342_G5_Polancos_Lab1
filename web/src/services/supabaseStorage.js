import { SUPABASE_ANON_KEY, SUPABASE_PROFILE_BUCKET, SUPABASE_URL } from '../config/appConfig';

const sanitizeSegment = (value) => value.replace(/[^a-zA-Z0-9._-]/g, '_');

const ensureConfigured = () => {
  if (!SUPABASE_URL || !SUPABASE_ANON_KEY) {
    throw new Error('Supabase storage is not configured. Set REACT_APP_SUPABASE_URL and REACT_APP_SUPABASE_ANON_KEY.');
  }
};

export const uploadProfilePhoto = async (file, email) => {
  ensureConfigured();

  if (!file) throw new Error('No image file selected.');
  if (!email) throw new Error('User email is required to upload a profile photo.');

  const extension = file.name.includes('.') ? file.name.split('.').pop()?.toLowerCase() : 'jpg';
  const objectPath = `profiles/${sanitizeSegment(email)}-${Date.now()}.${extension || 'jpg'}`;
  const uploadUrl = `${SUPABASE_URL}/storage/v1/object/${SUPABASE_PROFILE_BUCKET}/${objectPath}`;

  const response = await fetch(uploadUrl, {
    method: 'POST',
    headers: {
      apikey: SUPABASE_ANON_KEY,
      Authorization: `Bearer ${SUPABASE_ANON_KEY}`,
      'Content-Type': file.type || 'application/octet-stream',
      'x-upsert': 'true',
    },
    body: file,
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || 'Failed to upload profile photo.');
  }

  return `${SUPABASE_URL}/storage/v1/object/public/${SUPABASE_PROFILE_BUCKET}/${objectPath}`;
};
