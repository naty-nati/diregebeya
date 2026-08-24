import { requireSession } from "@/lib/session";
import { apiFetch } from "@/lib/api";
import { isAdmin } from "@/lib/roles";
import { StatCard } from "@/components/stat-card";
import { OrdersByStatusBar } from "@/components/orders-by-status-bar";

type AdminStats = {
  totalUsers: number;
  totalProducts: number;
  totalCategories: number;
  totalOrders: number;
  totalRevenue: number;
  ordersByStatus: Record<string, number>;
};

export default async function DashboardPage() {
  const session = await requireSession();
  const stats = await apiFetch<AdminStats>("/api/admin/stats", session);
  const admin = isAdmin(session.user.roles);

  return (
    <div className="mx-auto w-full max-w-5xl">
      <div className="mb-8">
        <h1 className="text-xl font-semibold text-slate-900">Dashboard</h1>
        <p className="text-sm text-slate-500">
          Signed in as {session.user.fullName} ({session.user.email})
        </p>
      </div>

      <div className="mb-8 grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-5">
        {admin && (
          <StatCard
            label="Customers"
            value={String(stats.totalUsers)}
            caption="accounts registered"
          />
        )}
        <StatCard
          label="Products"
          value={String(stats.totalProducts)}
          caption="live in catalog"
        />
        <StatCard
          label="Categories"
          value={String(stats.totalCategories)}
          caption="in the catalog"
        />
        <StatCard
          label="Orders"
          value={String(stats.totalOrders)}
          caption="placed all time"
        />
        {admin && (
          <StatCard
            label="Revenue"
            value={`ETB ${stats.totalRevenue}`}
            caption="gross, excluding cancelled"
          />
        )}
      </div>

      <OrdersByStatusBar ordersByStatus={stats.ordersByStatus} />
    </div>
  );
}
