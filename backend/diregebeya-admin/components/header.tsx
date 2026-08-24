import { logout } from "@/app/login/actions";

type HeaderProps = {
  userName: string;
  userEmail: string;
};

export function Header({ userName, userEmail }: HeaderProps) {
  const initial = userName.trim().charAt(0).toUpperCase() || "?";

  return (
    <header className="flex items-center justify-between border-b border-slate-200 bg-white px-6 py-3">
      <div className="flex items-center gap-3">
        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-slate-900 text-sm font-medium text-white">
          {initial}
        </div>
        <div className="leading-tight">
          <p className="truncate text-sm font-medium text-slate-900">
            {userName}
          </p>
          <p className="truncate text-xs text-slate-500">{userEmail}</p>
        </div>
      </div>

      <form action={logout}>
        <button
          type="submit"
          className="rounded-md border border-slate-300 px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-100"
        >
          Sign out
        </button>
      </form>
    </header>
  );
}
