import Link from "next/link";
import { requireSession } from "@/lib/session";
import { apiFetch } from "@/lib/api";

type Product = {
  id: number;
  name: string;
  brand: string;
  price: number;
  stock: number;
  category: { id: number; name: string } | null;
};

type Page<T> = {
  content: T[];
  number: number;
  totalPages: number;
};

export default async function ProductsPage({
  searchParams,
}: PageProps<"/dashboard/products">) {
  const session = await requireSession();
  const page = Number((await searchParams).page ?? "0");
  const data = await apiFetch<Page<Product>>(
    `/api/products?page=${page}&size=20`,
    session,
  );

  return (
    <div className="mx-auto w-full max-w-5xl">
      <div className="mb-8">
        <h1 className="text-xl font-semibold text-slate-900">Products</h1>
        <p className="text-sm text-slate-500">{data.content.length} shown</p>
      </div>

      <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-slate-200 text-slate-500">
            <tr>
              <th className="px-4 py-3 font-medium">Name</th>
              <th className="px-4 py-3 font-medium">Brand</th>
              <th className="px-4 py-3 font-medium">Category</th>
              <th className="px-4 py-3 font-medium">Price</th>
              <th className="px-4 py-3 font-medium">Stock</th>
            </tr>
          </thead>
          <tbody>
            {data.content.map((product) => (
              <tr key={product.id} className="border-b border-slate-100 last:border-0">
                <td className="px-4 py-3 text-slate-900">{product.name}</td>
                <td className="px-4 py-3 text-slate-600">{product.brand}</td>
                <td className="px-4 py-3 text-slate-600">
                  {product.category?.name ?? "—"}
                </td>
                <td className="px-4 py-3 text-slate-600">{product.price}</td>
                <td className="px-4 py-3 text-slate-600">{product.stock}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="mt-4 flex justify-between text-sm">
        {page > 0 ? (
          <Link
            href={`/dashboard/products?page=${page - 1}`}
            className="rounded-md border border-slate-300 px-3 py-1.5 text-slate-700 hover:bg-slate-100"
          >
            Previous
          </Link>
        ) : (
          <span />
        )}
        {page + 1 < data.totalPages && (
          <Link
            href={`/dashboard/products?page=${page + 1}`}
            className="rounded-md border border-slate-300 px-3 py-1.5 text-slate-700 hover:bg-slate-100"
          >
            Next
          </Link>
        )}
      </div>
    </div>
  );
}
