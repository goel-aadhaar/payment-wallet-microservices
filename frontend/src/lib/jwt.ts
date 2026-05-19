// Minimal browser-side JWT payload decode (no verification — the gateway
// verifies; this only reads non-sensitive claims to bootstrap the UI).

export interface JwtClaims {
  sub?: string;
  userId?: number;
  role?: string;
  exp?: number;
  [key: string]: unknown;
}

export function decodeJwt(token: string): JwtClaims | null {
  try {
    const payload = token.split(".")[1];
    const json = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
    return JSON.parse(json) as JwtClaims;
  } catch {
    return null;
  }
}
