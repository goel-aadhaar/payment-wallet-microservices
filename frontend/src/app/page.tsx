"use client";

import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import { ArrowUpRight, ShieldCheck, Zap, Gift } from "lucide-react";

export default function LandingPage() {
  const { isAuthenticated } = useAuth();
  const primaryHref = isAuthenticated ? "/dashboard" : "/signup";

  return (
    <main className="relative min-h-dvh overflow-hidden">
      <div className="engraved-grid absolute inset-0 -z-10" aria-hidden />
      <div
        className="absolute -top-40 left-1/2 -z-10 h-[36rem] w-[36rem] -translate-x-1/2 rounded-full blur-[140px]"
        style={{ background: "radial-gradient(circle, rgba(199,242,80,0.16), transparent 70%)" }}
        aria-hidden
      />

      {/* Masthead */}
      <header className="mx-auto flex max-w-6xl items-center justify-between px-6 py-7">
        <div className="reveal flex items-baseline gap-2" style={{ animationDelay: "60ms" }}>
          <span className="font-display text-2xl tracking-tight">Vault</span>
          <span className="h-1.5 w-1.5 rounded-full bg-accent" />
        </div>
        <nav className="reveal flex items-center gap-2 text-sm" style={{ animationDelay: "120ms" }}>
          <Link href="/login" className="btn btn-ghost">
            Sign in
          </Link>
          <Link href={primaryHref} className="btn btn-primary">
            Open account
            <ArrowUpRight className="h-4 w-4" />
          </Link>
        </nav>
      </header>

      {/* Hero */}
      <section className="mx-auto max-w-6xl px-6 pt-16 pb-24 md:pt-28">
        <p className="eyebrow reveal" style={{ animationDelay: "180ms" }}>
          Payment wallet · settled in real time
        </p>

        <h1
          className="reveal mt-6 max-w-4xl text-5xl leading-[1.03] sm:text-7xl md:text-[5.5rem]"
          style={{ animationDelay: "260ms" }}
        >
          Money that moves
          <br />
          <span className="italic text-accent">with precision.</span>
        </h1>

        <p
          className="reveal mt-8 max-w-xl text-lg leading-relaxed text-muted"
          style={{ animationDelay: "360ms" }}
        >
          Hold a balance, transfer instantly between accounts, and watch every
          rupee land on a ledger you can audit line by line.
        </p>

        <div
          className="reveal mt-10 flex flex-wrap items-center gap-3"
          style={{ animationDelay: "440ms" }}
        >
          <Link href={primaryHref} className="btn btn-primary px-6 py-3.5 text-base">
            {isAuthenticated ? "Go to dashboard" : "Create your wallet"}
            <ArrowUpRight className="h-4.5 w-4.5" />
          </Link>
          <Link href="/login" className="btn btn-ghost px-6 py-3.5 text-base">
            I already have one
          </Link>
        </div>

        {/* Feature ledger */}
        <div
          className="reveal mt-24 grid gap-px overflow-hidden rounded-2xl border border-line bg-line sm:grid-cols-3"
          style={{ animationDelay: "560ms" }}
        >
          {[
            {
              icon: Zap,
              label: "Instant transfers",
              body: "Send to any account ID and the balance updates the moment it clears.",
            },
            {
              icon: ShieldCheck,
              label: "Held-fund safety",
              body: "Funds are held, then captured — never debited into thin air.",
            },
            {
              icon: Gift,
              label: "Rewards that accrue",
              body: "Every settled transfer earns points, tracked on its own ledger.",
            },
          ].map(({ icon: Icon, label, body }) => (
            <div key={label} className="bg-surface p-8">
              <Icon className="h-6 w-6 text-accent" strokeWidth={1.6} />
              <h3 className="mt-5 text-xl">{label}</h3>
              <p className="mt-2 text-sm leading-relaxed text-muted">{body}</p>
            </div>
          ))}
        </div>
      </section>

      <footer className="mx-auto max-w-6xl px-6 py-10 text-sm text-faint">
        <span className="font-mono tracking-wide">VAULT</span> — a demonstration
        payment platform.
      </footer>
    </main>
  );
}
