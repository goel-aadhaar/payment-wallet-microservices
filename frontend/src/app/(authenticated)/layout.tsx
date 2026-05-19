"use client";

import { useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import { initials } from "@/lib/format";
import { LayoutDashboard, ArrowLeftRight, Gift, LogOut } from "lucide-react";

const NAV = [
  { name: "Overview", href: "/dashboard", icon: LayoutDashboard },
  { name: "Transactions", href: "/transactions", icon: ArrowLeftRight },
  { name: "Rewards", href: "/rewards", icon: Gift },
];

export default function AuthenticatedLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const { user, isAuthenticated, isLoading, logout } = useAuth();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.replace("/login");
    }
  }, [isAuthenticated, isLoading, router]);

  if (isLoading || !isAuthenticated) {
    return (
      <div className="flex min-h-dvh items-center justify-center">
        <div className="h-7 w-7 animate-spin rounded-full border-2 border-line border-t-accent" />
      </div>
    );
  }

  return (
    <div className="min-h-dvh md:flex">
      {/* Desktop sidebar */}
      <aside className="sticky top-0 hidden h-dvh w-64 shrink-0 flex-col border-r border-line md:flex">
        <div className="flex h-16 items-center gap-2 border-b border-line px-6">
          <span className="font-display text-xl tracking-tight">Vault</span>
          <span className="h-1.5 w-1.5 rounded-full bg-accent" />
        </div>

        <nav className="flex-1 space-y-1 p-4">
          {NAV.map((item) => {
            const active = pathname === item.href;
            return (
              <Link
                key={item.href}
                href={item.href}
                className={`flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm transition-colors ${
                  active
                    ? "bg-accent-soft text-accent"
                    : "text-muted hover:bg-surface hover:text-paper"
                }`}
              >
                <item.icon className="h-4.5 w-4.5" strokeWidth={1.8} />
                {item.name}
                {active && (
                  <span className="ml-auto h-1.5 w-1.5 rounded-full bg-accent" />
                )}
              </Link>
            );
          })}
        </nav>

        <div className="border-t border-line p-4">
          <div className="mb-3 flex items-center gap-3 px-1">
            <div className="flex h-9 w-9 items-center justify-center rounded-full border border-line-strong font-mono text-xs">
              {initials(user?.firstName, user?.lastName)}
            </div>
            <div className="min-w-0">
              <p className="truncate text-sm">
                {user?.firstName} {user?.lastName}
              </p>
              <p className="truncate text-xs text-faint">{user?.email}</p>
            </div>
          </div>
          <button
            onClick={logout}
            className="flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-sm text-muted transition-colors hover:bg-surface hover:text-negative"
          >
            <LogOut className="h-4.5 w-4.5" strokeWidth={1.8} />
            Sign out
          </button>
        </div>
      </aside>

      {/* Mobile top bar */}
      <header className="flex h-14 items-center justify-between border-b border-line px-5 md:hidden">
        <div className="flex items-baseline gap-2">
          <span className="font-display text-lg tracking-tight">Vault</span>
          <span className="h-1.5 w-1.5 rounded-full bg-accent" />
        </div>
        <button
          onClick={logout}
          className="text-muted transition-colors hover:text-negative"
          aria-label="Sign out"
        >
          <LogOut className="h-5 w-5" strokeWidth={1.8} />
        </button>
      </header>

      {/* Content */}
      <main className="relative flex-1 px-5 pb-28 pt-8 md:px-10 md:pb-12 md:pt-12">
        {children}
      </main>

      {/* Mobile bottom tab bar */}
      <nav className="fixed inset-x-0 bottom-0 z-40 grid grid-cols-3 border-t border-line bg-ink/95 backdrop-blur md:hidden">
        {NAV.map((item) => {
          const active = pathname === item.href;
          return (
            <Link
              key={item.href}
              href={item.href}
              className={`flex flex-col items-center gap-1 py-3 text-xs transition-colors ${
                active ? "text-accent" : "text-faint"
              }`}
            >
              <item.icon className="h-5 w-5" strokeWidth={1.8} />
              {item.name}
            </Link>
          );
        })}
      </nav>
    </div>
  );
}
