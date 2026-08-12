import API_BASE_URL from '@/constants/api';
import { apiFetch } from '@/apis/utils/apiHelpers';

export const refreshAccessToken = async (): Promise<string> => {
  const res = await apiFetch(`${API_BASE_URL}/auth/user/refresh`, {
    method: 'POST',
    credentials: 'include',
  });

  if (res.status === 200) {
    const { data } = await res.json();
    return data.accessToken;
  }

  throw new Error('REFRESH_FAILED');
};
