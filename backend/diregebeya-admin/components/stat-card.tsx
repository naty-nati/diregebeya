type StatCardProps = {
  label: string;
  value: string;
  caption?: string;
};

export function StatCard({ label, value, caption }: StatCardProps) {
  return (
    <div className="rounded-xl border border-slate-200 bg-white p-6">
      <p className="text-xs font-medium tracking-wide text-slate-500 uppercase">
        {label}
      </p>
      <p className="mt-2 text-2xl font-semibold text-slate-900">{value}</p>
      {caption && <p className="mt-1 text-xs text-slate-500">{caption}</p>}
    </div>
  );
}
