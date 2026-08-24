import Link from "next/link";
import { requireSession } from "@/lib/session";
import { apiFetch } from "@/lib/api";

const STATUSES = ["PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"];

type Order = {
  id: number;
  status: string;
  totalAmount: number;
  paymentStatus: string;
  createdAt: string;
};

type Page<T> = {
  content: T[];
  number: number;
  totalPages: number;
};

export default async function OrdersPage({
  searchParams,
}: PageProps<"/dashboard/orders">) {
  const session = await requireSession();
  const params = await searchParams;
  const page = Number(params.page ?? "0");
  const status = typeof params.status === "string" ? params.status : "";

  const query = new URLSearchParams({ page: String(page), size: "20" });
  if (status) query.set("status", status);

  const data = await apiFetch<Page<Order>>(
    `/api/admin/orders?${query.toString()}`,
    session,
  );

  return (
    <div className="mx-auto w-full max-w-5xl">
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-slate-900">Orders</h1>
          <p className="text-sm text-slate-500">{data.content.length} shown</p>
        </div>

        <form className="flex items-center gap-2">
          <select
            name="status"
            defaultValue={status}
            className="rounded-md border border-slate-300 px-3 py-1.5 text-sm text-slate-700"
          >
            <option value="">All statuses</option>
            {STATUSES.map((s) => (
              <option key={s} value={s}>
                {s}
              </option>
            ))}
          </select>
          <button
            type="submit"
            className="rounded-md border border-slate-300 px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-100"
          >
            Filter
          </button>
        </form>
      </div>

      <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-slate-200 text-slate-500">
            <tr>
              <th className="px-4 py-3 font-medium">Order #</th>
              <th className="px-4 py-3 font-medium">Status</th>
              <th className="px-4 py-3 font-medium">Total</th>
              <th className="px-4 py-3 font-medium">Payment</th>
              <th className="px-4 py-3 font-medium">Created</th>
            </tr>
          </thead>
          <tbody>
            {data.content.map((order) => (
              <tr key={order.id} className="border-b border-slate-100 last:border-0">
                <td className="px-4 py-3 text-slate-900">#{order.id}</td>
                <td className="px-4 py-3 text-slate-600">{order.status}</td>
                <td className="px-4 py-3 text-slate-600">{order.totalAmount}</td>
                <td className="px-4 py-3 text-slate-600">{order.paymentStatus}</td>
                <td className="px-4 py-3 text-slate-600">
                  {new Date(order.createdAt).toLocaleString()}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="mt-4 flex justify-between text-sm">
        {page > 0 ? (
          <Link
            href={`/dashboard/orders?page=${page - 1}${status ? `&status=${status}` : ""}`}
            className="rounded-md border border-slate-300 px-3 py-1.5 text-slate-700 hover:bg-slate-100"
          >
            Previous
          </Link>
        ) : (
          <span />
        )}
        {page + 1 < data.totalPages && (
          <Link
            href={`/dashboard/orders?page=${page + 1}${status ? `&status=${status}` : ""}`}
            className="rounded-md border border-slate-300 px-3 py-1.5 text-slate-700 hover:bg-slate-100"
          >
            Next
          </Link>
        )}
      </div>
    </div>
  );
}
