import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { SESSION_COOKIE } from "@/lib/constants";
import { isAdmin, isAdminOnlyPath } from "@/lib/roles";

export function proxy(request: NextRequest) {
  const raw = request.cookies.get(SESSION_COOKIE)?.value;
  if (!raw) {
    return NextResponse.redirect(new URL("/login", request.url));
  }

  // Optimistic check: reads roles straight out of the cookie (no DB/network
  // call, since proxy runs on every request including prefetches). This is
  // the UI-layer guard only - the backend's @PreAuthorize checks are the
  // real security boundary, since a hand-edited cookie can't forge the JWT.
  let roles: string[] = [];
  try {
    roles = JSON.parse(raw)?.user?.roles ?? [];
  } catch {
    return NextResponse.redirect(new URL("/login", request.url));
  }

  if (isAdminOnlyPath(request.nextUrl.pathname) && !isAdmin(roles)) {
    return NextResponse.redirect(new URL("/dashboard", request.url));
  }
}

export const config = {
  matcher: ["/dashboard/:path*"],
};
