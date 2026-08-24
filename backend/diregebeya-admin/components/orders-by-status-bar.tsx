const STATUS_ORDER = ["PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"];

type OrdersByStatusBarProps = {
  ordersByStatus: Record<string, number>;
};

export function OrdersByStatusBar({ ordersByStatus }: OrdersByStatusBarProps) {
  const max = Math.max(1, ...Object.values(ordersByStatus));

  return (
    <div className="rounded-xl border border-slate-200 bg-white p-6">
      <h2 className="mb-4 text-sm font-semibold text-slate-900">
        Orders by status
      </h2>
      <div className="space-y-3">
        {STATUS_ORDER.map((status) => {
          const count = ordersByStatus[status] ?? 0;
          const width = count === 0 ? 0 : (count / max) * 100;

          return (
            <div key={status} className="flex items-center gap-3">
              <span className="w-24 shrink-0 text-sm text-slate-700">
                {status.charAt(0) + status.slice(1).toLowerCase()}
              </span>
              <div className="h-2 flex-1 rounded-full bg-slate-100">
                <div
                  className="h-2 rounded-full bg-slate-900"
                  style={{ width: `${width}%` }}
                />
              </div>
              <span className="w-8 shrink-0 text-right text-sm font-medium text-slate-900">
                {count}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}
