import { redirect } from "next/navigation";
import { requireSession } from "@/lib/session";
import { apiFetch } from "@/lib/api";
import { isAdmin } from "@/lib/roles";

type Coupon = {
  id: number;
  code: string;
  discountType: string;
  discountValue: number;
  maxUses: number | null;
  usedCount: number;
  expiresAt: string | null;
  active: boolean;
};

export default async function CouponsPage() {
  const session = await requireSession();
  if (!isAdmin(session.user.roles)) {
    redirect("/dashboard");
  }

  const coupons = await apiFetch<Coupon[]>("/api/coupons", session);

  return (
    <div className="mx-auto w-full max-w-5xl">
      <div className="mb-8">
        <h1 className="text-xl font-semibold text-slate-900">Coupons</h1>
        <p className="text-sm text-slate-500">{coupons.length} total</p>
      </div>

      <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-slate-200 text-slate-500">
            <tr>
              <th className="px-4 py-3 font-medium">Code</th>
              <th className="px-4 py-3 font-medium">Discount</th>
              <th className="px-4 py-3 font-medium">Used / Max</th>
              <th className="px-4 py-3 font-medium">Expires</th>
              <th className="px-4 py-3 font-medium">Active</th>
            </tr>
          </thead>
          <tbody>
            {coupons.map((coupon) => (
              <tr key={coupon.id} className="border-b border-slate-100 last:border-0">
                <td className="px-4 py-3 text-slate-900">{coupon.code}</td>
                <td className="px-4 py-3 text-slate-600">
                  {coupon.discountValue} ({coupon.discountType})
                </td>
                <td className="px-4 py-3 text-slate-600">
                  {coupon.usedCount} / {coupon.maxUses ?? "∞"}
                </td>
                <td className="px-4 py-3 text-slate-600">
                  {coupon.expiresAt
                    ? new Date(coupon.expiresAt).toLocaleDateString()
                    : "—"}
                </td>
                <td className="px-4 py-3 text-slate-600">
                  {coupon.active ? "Yes" : "No"}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
