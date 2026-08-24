import { ROLE_ADMIN, ROLE_STAFF } from "@/lib/constants";

export type NavItem = {
  label: string;
  href: string;
  allowedRoles: string[];
};

export const NAV_ITEMS: NavItem[] = [
  { label: "Dashboard", href: "/dashboard", allowedRoles: [ROLE_ADMIN, ROLE_STAFF] },
  { label: "Products", href: "/dashboard/products", allowedRoles: [ROLE_ADMIN, ROLE_STAFF] },
  { label: "Categories", href: "/dashboard/categories", allowedRoles: [ROLE_ADMIN, ROLE_STAFF] },
  { label: "Orders", href: "/dashboard/orders", allowedRoles: [ROLE_ADMIN, ROLE_STAFF] },
  { label: "Users", href: "/dashboard/users", allowedRoles: [ROLE_ADMIN] },
  { label: "Coupons", href: "/dashboard/coupons", allowedRoles: [ROLE_ADMIN] },
];
