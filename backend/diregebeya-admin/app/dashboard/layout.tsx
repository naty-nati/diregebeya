import { requireSession } from "@/lib/session";
import { Sidebar } from "@/components/sidebar";
import { Header } from "@/components/header";

export default async function DashboardLayout({
  children,
}: LayoutProps<"/dashboard">) {
  const session = await requireSession();

  return (
    <div className="flex flex-1">
      <Sidebar
        roles={session.user.roles}
        userName={session.user.fullName}
        userEmail={session.user.email}
      />
      <div className="flex flex-1 flex-col overflow-hidden">
        <Header
          userName={session.user.fullName}
          userEmail={session.user.email}
        />
        <main className="flex-1 overflow-y-auto bg-slate-50 px-6 py-8">
          {children}
        </main>
      </div>
    </div>
  );
}
