import { redirect } from "next/navigation";
import { requireSession } from "@/lib/session";
import { apiFetch } from "@/lib/api";
import { isAdmin } from "@/lib/roles";
import { ROLE_STAFF } from "@/lib/constants";
import { setStaffRole } from "./actions";

type User = {
  id: number;
  fullName: string;
  email: string;
  roles: string[];
  enabled: boolean;
  createdAt: string;
};

type Page<T> = {
  content: T[];
};

export default async function UsersPage() {
  const session = await requireSession();
  if (!isAdmin(session.user.roles)) {
    redirect("/dashboard");
  }

  const data = await apiFetch<Page<User>>("/api/admin/users?size=50", session);

  return (
    <div className="mx-auto w-full max-w-5xl">
      <div className="mb-8">
        <h1 className="text-xl font-semibold text-slate-900">Users</h1>
        <p className="text-sm text-slate-500">{data.content.length} shown</p>
      </div>

      <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-slate-200 text-slate-500">
            <tr>
              <th className="px-4 py-3 font-medium">Name</th>
              <th className="px-4 py-3 font-medium">Email</th>
              <th className="px-4 py-3 font-medium">Roles</th>
              <th className="px-4 py-3 font-medium">Enabled</th>
              <th className="px-4 py-3 font-medium">Staff access</th>
            </tr>
          </thead>
          <tbody>
            {data.content.map((user) => {
              const hasStaff = user.roles.includes(ROLE_STAFF);
              return (
                <tr key={user.id} className="border-b border-slate-100 last:border-0">
                  <td className="px-4 py-3 text-slate-900">{user.fullName}</td>
                  <td className="px-4 py-3 text-slate-600">{user.email}</td>
                  <td className="px-4 py-3 text-slate-600">{user.roles.join(", ")}</td>
                  <td className="px-4 py-3 text-slate-600">{user.enabled ? "Yes" : "No"}</td>
                  <td className="px-4 py-3">
                    <form action={setStaffRole.bind(null, user.id, !hasStaff)}>
                      <button
                        type="submit"
                        className="rounded-md border border-slate-300 px-3 py-1.5 text-xs text-slate-700 hover:bg-slate-100"
                      >
                        {hasStaff ? "Revoke staff" : "Grant staff"}
                      </button>
                    </form>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
