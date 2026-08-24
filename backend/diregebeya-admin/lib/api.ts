import type { Session } from "@/lib/session";

const API_URL = process.env.BACKEND_API_URL ?? "http://localhost:8080";

export async function apiFetch<T>(
  path: string,
  session: Session,
  init?: RequestInit,
): Promise<T> {
  const res = await fetch(`${API_URL}${path}`, {
    ...init,
    headers: {
      ...init?.headers,
      Authorization: `Bearer ${session.accessToken}`,
    },
    cache: "no-store",
  });

  if (!res.ok) {
    throw new Error(`Request to ${path} failed: ${res.status}`);
  }

  return res.json() as Promise<T>;
}
