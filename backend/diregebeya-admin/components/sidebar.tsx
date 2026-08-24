"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { NAV_ITEMS } from "@/lib/nav";
import { logout } from "@/app/login/actions";

type SidebarProps = {
  roles: string[];
  userName: string;
  userEmail: string;
};

export function Sidebar({ roles, userName, userEmail }: SidebarProps) {
  const pathname = usePathname();
  const items = NAV_ITEMS.filter((item) =>
    item.allowedRoles.some((role) => roles.includes(role)),
  );

  return (
    <aside className="flex w-56 flex-col bg-slate-900 text-slate-300">
      <div className="px-4 py-5 text-sm font-semibold text-white">
        Diregebeya Admin
      </div>

      <nav className="flex-1 space-y-1 px-2">
        {items.map((item) => {
          const active =
            pathname === item.href ||
            (item.href !== "/dashboard" && pathname.startsWith(`${item.href}/`));

          return (
            <Link
              key={item.href}
              href={item.href}
              className={`block rounded-md px-3 py-2 text-sm ${
                active
                  ? "bg-white font-medium text-slate-900"
                  : "text-slate-300 hover:bg-slate-800 hover:text-white"
              }`}
            >
              {item.label}
            </Link>
          );
        })}
      </nav>

      <div className="border-t border-slate-800 px-4 py-4">
        <p className="truncate text-sm font-medium text-white">{userName}</p>
        <p className="truncate text-xs text-slate-400">{userEmail}</p>
        <form action={logout} className="mt-3">
          <button
            type="submit"
            className="w-full rounded-md border border-slate-700 px-3 py-1.5 text-sm text-slate-300 hover:bg-slate-800 hover:text-white"
          >
            Sign out
          </button>
        </form>
      </div>
    </aside>
  );
}
