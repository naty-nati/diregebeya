import { ADMIN_ONLY_PATHS, ROLE_ADMIN, ROLE_STAFF } from "@/lib/constants";

export function isAdmin(roles: string[]): boolean {
  return roles.includes(ROLE_ADMIN);
}

export function hasAdminAccess(roles: string[]): boolean {
  return roles.includes(ROLE_ADMIN) || roles.includes(ROLE_STAFF);
}

export function isAdminOnlyPath(pathname: string): boolean {
  return ADMIN_ONLY_PATHS.some(
    (path) => pathname === path || pathname.startsWith(`${path}/`),
  );
}
