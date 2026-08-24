"use server";

import { revalidatePath } from "next/cache";
import { requireSession } from "@/lib/session";
import { apiFetch } from "@/lib/api";

export async function setStaffRole(userId: number, staff: boolean) {
  const session = await requireSession();
  await apiFetch(`/api/admin/users/${userId}/staff-role`, session, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ staff }),
  });
  revalidatePath("/dashboard/users");
}
