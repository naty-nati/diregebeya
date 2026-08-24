"use server";

import { redirect } from "next/navigation";
import { setSession, clearSession } from "@/lib/session";
import { hasAdminAccess } from "@/lib/roles";

const API_URL = process.env.BACKEND_API_URL ?? "http://localhost:8080";

export type LoginState = {
  error?: string;
};

export async function login(
  _prevState: LoginState,
  formData: FormData,
): Promise<LoginState> {
  const email = formData.get("email");
  const password = formData.get("password");

  if (
    typeof email !== "string" ||
    typeof password !== "string" ||
    !email ||
    !password
  ) {
    return { error: "Email and password are required." };
  }

  let response: Response;
  try {
    response = await fetch(`${API_URL}/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
      cache: "no-store",
    });
  } catch {
    return { error: "Could not reach the server. Is the backend running?" };
  }

  if (!response.ok) {
    if (response.status === 401) {
      return { error: "Invalid email or password." };
    }
    return { error: "Login failed. Please try again." };
  }

  const data = await response.json();

  if (!hasAdminAccess(data.user?.roles ?? [])) {
    return { error: "This account does not have admin access." };
  }

  await setSession({ accessToken: data.accessToken, user: data.user });
  redirect("/dashboard");
}

export async function logout() {
  await clearSession();
  redirect("/login");
}
