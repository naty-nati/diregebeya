import { requireSession } from "@/lib/session";
import { apiFetch } from "@/lib/api";

type Category = {
  id: number;
  name: string;
  description: string;
};

export default async function CategoriesPage() {
  const session = await requireSession();
  const categories = await apiFetch<Category[]>("/api/categories", session);

  return (
    <div className="mx-auto w-full max-w-5xl">
      <div className="mb-8">
        <h1 className="text-xl font-semibold text-slate-900">Categories</h1>
        <p className="text-sm text-slate-500">{categories.length} total</p>
      </div>

      <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-slate-200 text-slate-500">
            <tr>
              <th className="px-4 py-3 font-medium">Name</th>
              <th className="px-4 py-3 font-medium">Description</th>
            </tr>
          </thead>
          <tbody>
            {categories.map((category) => (
              <tr key={category.id} className="border-b border-slate-100 last:border-0">
                <td className="px-4 py-3 text-slate-900">{category.name}</td>
                <td className="px-4 py-3 text-slate-600">{category.description}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
