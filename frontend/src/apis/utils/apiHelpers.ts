import { ApiError } from '@/errors';

/**
 * localtunnel bypass 헤더를 자동으로 포함하는 fetch 래퍼.
 * 공개(인증 불필요) API 요청에 사용한다.
 */
export const apiFetch = (
  input: RequestInfo | URL,
  init?: RequestInit,
): Promise<Response> => {
  return fetch(input, {
    ...init,
    headers: {
      'bypass-tunnel-reminder': 'true',
      ...(init?.headers || {}),
    },
  });
};

export const handleResponse = async <T = unknown>(
  response: Response,
  customErrorMessage?: string,
): Promise<T | undefined> => {
  if (!response.ok) {
    let message = customErrorMessage ?? response.statusText;
    let errorCode: string | undefined;
    let errorData: unknown;

    try {
      const body = await response.json();
      errorData = body;
      if (body?.message) {
        message = customErrorMessage ?? body.message;
      }
      if (body?.errorCode) {
        errorCode = body.errorCode;
      }
    } catch {
      // JSON 파싱 실패시 statusText 사용
    }

    throw new ApiError(
      response.status,
      response.statusText,
      errorCode,
      errorData,
      message,
    );
  }

  const contentType = response.headers.get('content-type');
  const contentLength = response.headers.get('content-length');

  if (contentLength === '0' || !contentType?.includes('application/json')) {
    return undefined;
  }
  const text = await response.text();
  if (!text) {
    return undefined;
  }

  try {
    const result = JSON.parse(text);
    // wrapped 형식({ data: {...} })인 경우 data를 unwrap
    // 단, data가 null/undefined가 아닌 유효한 값일 때만
    if (
      result &&
      typeof result === 'object' &&
      'data' in result &&
      result.data !== null &&
      result.data !== undefined
    ) {
      return result.data as T;
    }
    return result as T;
  } catch {
    return undefined;
  }
};
