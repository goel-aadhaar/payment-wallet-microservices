"use client";

import { useState, type SyntheticEvent } from "react";
import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import api from "@/lib/api";
import { decodeJwt } from "@/lib/jwt";
import type { JwtResponse, User } from "@/lib/types";
import { ArrowRight, Loader2 } from "lucide-react";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();

  const resolveUser = async (token: string): Promise<User> => {
    const claims = decodeJwt(token);
    const userId = typeof claims?.userId === "number" ? claims.userId : undefined;

    // Prefer the full profile; the JWT carries id/email/role as a fallback.
    if (userId !== undefined) {
      try {
        const { data } = await api.get<User>(`/api/v1/users/${userId}`);
        if (data?.id != null) return data;
      } catch {
        /* fall through to claims */
      }
    }
    return {
      id: userId ?? 0,
      firstName: claims?.sub?.split("@")[0] ?? "User",
      lastName: "",
      email: claims?.sub ?? email,
      role: typeof claims?.role === "string" ? claims.role : "ROLE_USER",
    };
  };

  const handleSubmit = async (e: SyntheticEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const { data } = await api.post<JwtResponse>("/auth/login", {
        email,
        password,
      });
      const token = data.token;
      login(token, await resolveUser(token));
    } catch (err) {
      const message =
        (err as { response?: { data?: { message?: string } } })?.response?.data
          ?.message ?? "Couldn't sign you in. Check your credentials.";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="grid min-h-dvh lg:grid-cols-2">
      {/* Brand column */}
      <aside className="relative hidden flex-col justify-between overflow-hidden border-r border-line p-12 lg:flex">
        <div className="engraved-grid absolute inset-0 -z-10" aria-hidden />
        <div className="flex items-baseline gap-2">
          <span className="font-display text-2xl tracking-tight">Vault</span>
          <span className="h-1.5 w-1.5 rounded-full bg-accent" />
        </div>
        <div className="reveal max-w-md" style={{ animationDelay: "120ms" }}>
          <p className="eyebrow">The ledger</p>
          <p className="mt-5 font-display text-4xl leading-tight">
            Every transfer, held and captured —{" "}
            <span className="italic text-accent">accounted for.</span>
          </p>
        </div>
        <p className="font-mono text-xs tracking-wide text-faint">
          SECURE SESSION · JWT BEARER
        </p>
      </aside>

      {/* Form column */}
      <section className="flex items-center justify-center px-6 py-16">
        <div className="reveal w-full max-w-sm" style={{ animationDelay: "80ms" }}>
          <div className="mb-10 flex items-baseline gap-2 lg:hidden">
            <span className="font-display text-2xl tracking-tight">Vault</span>
            <span className="h-1.5 w-1.5 rounded-full bg-accent" />
          </div>

          <p className="eyebrow">Welcome back</p>
          <h1 className="mt-3 text-4xl">Sign in</h1>
          <p className="mt-3 text-sm text-muted">
            Access your wallet, transfers and rewards.
          </p>

          {error && (
            <div className="mt-6 flex items-center gap-2.5 rounded-lg border border-negative/40 bg-negative/10 px-4 py-3 text-sm text-negative">
              <span className="h-1.5 w-1.5 shrink-0 rounded-full bg-negative" />
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="mt-8 space-y-5">
            <div>
              <label htmlFor="email" className="eyebrow">
                Email
              </label>
              <input
                id="email"
                type="email"
                autoComplete="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="field mt-2"
                placeholder="you@example.com"
                required
              />
            </div>

            <div>
              <div className="flex items-baseline justify-between">
                <label htmlFor="password" className="eyebrow">
                  Password
                </label>
                <span className="text-xs text-faint">Forgot?</span>
              </div>
              <input
                id="password"
                type="password"
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="field mt-2"
                placeholder="••••••••"
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
                  Sign in
                  <ArrowRight className="h-4.5 w-4.5" />
                </>
              )}
            </button>
          </form>

          <p className="mt-8 text-sm text-muted">
            New to Vault?{" "}
            <Link href="/signup" className="text-accent hover:underline">
              Create an account
            </Link>
          </p>
        </div>
      </section>
    </main>
  );
}
