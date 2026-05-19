"use client";

import { useState, type SyntheticEvent } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import api from "@/lib/api";
import { ArrowRight, Loader2, Check } from "lucide-react";

export default function SignupPage() {
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const update = (key: keyof typeof form) => (e: SyntheticEvent) =>
    setForm((f) => ({
      ...f,
      [key]: (e.target as HTMLInputElement).value,
    }));

  const handleSubmit = async (e: SyntheticEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await api.post("/auth/signup", form);
      router.push("/login");
    } catch (err) {
      const message =
        (err as { response?: { data?: { message?: string } } })?.response?.data
          ?.message ?? "Couldn't create your account. Try again.";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="grid min-h-dvh lg:grid-cols-2">
      {/* Form column */}
      <section className="order-2 flex items-center justify-center px-6 py-16 lg:order-1">
        <div className="reveal w-full max-w-sm" style={{ animationDelay: "80ms" }}>
          <div className="mb-10 flex items-baseline gap-2 lg:hidden">
            <span className="font-display text-2xl tracking-tight">Vault</span>
            <span className="h-1.5 w-1.5 rounded-full bg-accent" />
          </div>

          <p className="eyebrow">Open an account</p>
          <h1 className="mt-3 text-4xl">Create your wallet</h1>
          <p className="mt-3 text-sm text-muted">
            Takes a minute. No card required.
          </p>

          {error && (
            <div className="mt-6 flex items-center gap-2.5 rounded-lg border border-negative/40 bg-negative/10 px-4 py-3 text-sm text-negative">
              <span className="h-1.5 w-1.5 shrink-0 rounded-full bg-negative" />
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="mt-8 space-y-5">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label htmlFor="firstName" className="eyebrow">
                  First name
                </label>
                <input
                  id="firstName"
                  value={form.firstName}
                  onChange={update("firstName")}
                  className="field mt-2"
                  placeholder="Ada"
                  required
                />
              </div>
              <div>
                <label htmlFor="lastName" className="eyebrow">
                  Last name
                </label>
                <input
                  id="lastName"
                  value={form.lastName}
                  onChange={update("lastName")}
                  className="field mt-2"
                  placeholder="Lovelace"
                  required
                />
              </div>
            </div>

            <div>
              <label htmlFor="email" className="eyebrow">
                Email
              </label>
              <input
                id="email"
                type="email"
                autoComplete="email"
                value={form.email}
                onChange={update("email")}
                className="field mt-2"
                placeholder="you@example.com"
                required
              />
            </div>

            <div>
              <label htmlFor="password" className="eyebrow">
                Password
              </label>
              <input
                id="password"
                type="password"
                autoComplete="new-password"
                value={form.password}
                onChange={update("password")}
                className="field mt-2"
                placeholder="At least 6 characters"
                minLength={6}
                required
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="btn btn-primary mt-2 w-full py-3.5"
            >
              {loading ? (
                <Loader2 className="h-5 w-5 animate-spin" />
              ) : (
                <>
                  Create account
                  <ArrowRight className="h-4.5 w-4.5" />
                </>
              )}
            </button>
          </form>

          <p className="mt-8 text-sm text-muted">
            Already have an account?{" "}
            <Link href="/login" className="text-accent hover:underline">
              Sign in
            </Link>
          </p>
        </div>
      </section>

      {/* Brand column */}
      <aside className="relative order-1 hidden flex-col justify-between overflow-hidden border-l border-line p-12 lg:order-2 lg:flex">
        <div className="engraved-grid absolute inset-0 -z-10" aria-hidden />
        <div className="flex items-baseline gap-2 self-end">
          <span className="font-display text-2xl tracking-tight">Vault</span>
          <span className="h-1.5 w-1.5 rounded-full bg-accent" />
        </div>
        <ul className="reveal space-y-5" style={{ animationDelay: "140ms" }}>
          {[
            "A live balance you control",
            "Instant transfers between accounts",
            "Rewards on every settled transfer",
          ].map((line) => (
            <li key={line} className="flex items-center gap-3 text-lg">
              <span className="flex h-7 w-7 items-center justify-center rounded-full border border-accent text-accent">
                <Check className="h-4 w-4" strokeWidth={2.5} />
              </span>
              {line}
            </li>
          ))}
        </ul>
        <p className="font-mono text-xs tracking-wide text-faint">
          ROLE_USER · ENCRYPTED AT REST
        </p>
      </aside>
    </main>
  );
}
