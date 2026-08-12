import API_BASE_URL from '@/constants/api';
import { GameRankingResponse } from '@/types/game';
import { apiFetch, handleResponse } from './utils/apiHelpers';

export const getGameRanking = async (): Promise<GameRankingResponse> => {
  const response = await apiFetch(`${API_BASE_URL}/api/game/ranking`);
  const data = await handleResponse<GameRankingResponse>(
    response,
    '랭킹을 불러오는데 실패했습니다.',
  );
  if (!data) throw new Error('랭킹을 불러오는데 실패했습니다.');
  return data;
};
