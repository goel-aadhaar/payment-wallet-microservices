"use client";

import { useState, useCallback, type SyntheticEvent } from "react";
import Link from "next/link";
import api from "@/lib/api";
import { useAuth } from "@/context/AuthContext";
import { useResource } from "@/lib/useResource";
import type { Wallet, Transaction } from "@/lib/types";
import { formatMoney, formatDateTime, currencySymbol } from "@/lib/format";
import {
  Plus,
  ArrowUpRight,
  ArrowDownLeft,
  Send,
  Loader2,
  Receipt,
} from "lucide-react";

interface DashboardData {
  wallet: Wallet;
  transactions: Transaction[];
}

export default function DashboardPage() {
  const { user } = useAuth();
  const [modalOpen, setModalOpen] = useState(false);
  const [amount, setAmount] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState("");

  const fetchDashboard = useCallback(async (): Promise<DashboardData> => {
    const userId = user!.id;

    let wallet: Wallet;
    try {
      const { data } = await api.get<Wallet>(`/api/v1/wallets/user/${userId}`);
      wallet = data;
    } catch (err) {
      if ((err as { response?: { status?: number } })?.response?.status === 404) {
        const { data } = await api.post<Wallet>("/api/v1/wallets", {
          userId,
          currency: "INR",
        });
        wallet = data;
      } else {
        throw err;
      }
    }

    const { data: tx } = await api.get<Transaction[]>("/api/v1/transactions");
    const transactions = tx
      .filter((t) => t.senderId === userId || t.receiverId === userId)
      .sort(
        (a, b) =>
          new Date(b.timestamp as string).getTime() -
          new Date(a.timestamp as string).getTime()
      )
      .slice(0, 6);

    return { wallet, transactions };
  }, [user]);

  const { data, loading, refetch } = useResource(fetchDashboard, !!user);
  const wallet = data?.wallet ?? null;
  const transactions = data?.transactions ?? [];
  const currency = wallet?.currency ?? "INR";
  const held = (wallet?.balance ?? 0) - (wallet?.availableBalance ?? 0);

  const handleAddFunds = async (e: SyntheticEvent) => {
    e.preventDefault();
    const value = parseFloat(amount);
    if (!Number.isFinite(value) || value <= 0) {
      setFormError("Enter an amount greater than zero.");
      return;
    }
    setSubmitting(true);
    setFormError("");
    try {
      await api.post("/api/v1/wallets/credit", {
        userId: user?.id,
        amount: value,
        currency,
      });
      setModalOpen(false);
      setAmount("");
      await refetch();
    } catch {
      setFormError("Couldn't add funds. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="mx-auto max-w-4xl space-y-6">
        <div className="skeleton h-44 rounded-2xl" />
        <div className="skeleton h-72 rounded-2xl" />
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-4xl">
      <header className="reveal mb-10">
        <p className="eyebrow">Overview</p>
        <h1 className="mt-2 text-3xl">Good to see you, {user?.firstName}.</h1>
      </header>

      {/* Balance statement */}
      <section
        className="reveal panel relative overflow-hidden p-8 md:p-10"
        style={{ animationDelay: "80ms" }}
      >
        <div
          className="engraved-grid absolute inset-0 -z-10 opacity-60"
          aria-hidden
        />
        <div className="flex flex-wrap items-start justify-between gap-6">
          <div>
            <p className="eyebrow">Available balance</p>
            <p className="mt-4 flex items-baseline gap-1 font-display text-6xl leading-none md:text-7xl">
              <span className="text-2xl text-muted md:text-3xl">
                {currencySymbol(currency)}
              </span>
              <span className="tabular-nums">
                {(wallet?.availableBalance ?? 0).toLocaleString("en-IN")}
              </span>
            </p>
            <div className="mt-5 flex flex-wrap gap-x-8 gap-y-2 text-sm">
              <span className="text-muted">
                Total{" "}
                <span className="tnum text-paper">
                  {formatMoney(wallet?.balance, currency)}
                </span>
              </span>
              <span className="text-muted">
                On hold{" "}
                <span className="tnum text-warn">
                  {formatMoney(held, currency)}
                </span>
              </span>
            </div>
          </div>

          <button onClick={() => setModalOpen(true)} className="btn btn-primary">
            <Plus className="h-4 w-4" strokeWidth={2.5} />
            Add funds
          </button>
        </div>

        <div className="mt-8 flex flex-wrap gap-3 border-t border-line pt-6">
          <Link href="/transactions" className="btn btn-ghost">
            <Send className="h-4 w-4" strokeWidth={1.8} />
            Send money
          </Link>
          <Link href="/rewards" className="btn btn-ghost">
            View rewards
          </Link>
        </div>
      </section>

      {/* Recent activity */}
      <section
        className="reveal panel mt-6 p-6 md:p-8"
        style={{ animationDelay: "160ms" }}
      >
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-xl">Recent activity</h2>
          <Link
            href="/transactions"
            className="text-sm text-accent hover:underline"
          >
            View all
          </Link>
        </div>

        {transactions.length === 0 ? (
          <div className="flex flex-col items-center gap-3 py-14 text-center">
            <Receipt className="h-9 w-9 text-faint" strokeWidth={1.4} />
            <p className="text-muted">No transactions yet.</p>
          </div>
        ) : (
          <ul className="divide-y divide-line">
            {transactions.map((tx) => {
              const sent = tx.senderId === user?.id;
              return (
                <li
                  key={tx.id}
                  className="flex items-center justify-between gap-4 py-4"
                >
                  <div className="flex min-w-0 items-center gap-4">
                    <span
                      className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-full border ${
                        sent
                          ? "border-negative/40 text-negative"
                          : "border-positive/40 text-positive"
                      }`}
                    >
                      {sent ? (
                        <ArrowUpRight className="h-4.5 w-4.5" />
                      ) : (
                        <ArrowDownLeft className="h-4.5 w-4.5" />
                      )}
                    </span>
                    <div className="min-w-0">
                      <p className="truncate text-sm">
                        {sent
                          ? `Sent to account #${tx.receiverId}`
                          : `Received from account #${tx.senderId}`}
                      </p>
                      <p className="text-xs text-faint">
                        {formatDateTime(tx.timestamp)}
                      </p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p
                      className={`tnum text-sm ${
                        sent ? "text-paper" : "text-positive"
                      }`}
                    >
                      {sent ? "−" : "+"}
                      {formatMoney(tx.amount, currency)}
                    </p>
                    <p className="font-mono text-[0.65rem] uppercase tracking-wider text-faint">
                      {tx.status}
                    </p>
                  </div>
                </li>
              );
            })}
          </ul>
        )}
      </section>

      {/* Add funds modal */}
      {modalOpen && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-ink/70 p-4 backdrop-blur-sm"
          onClick={() => setModalOpen(false)}
        >
          <div
            className="modal-in panel w-full max-w-sm p-7"
            onClick={(e) => e.stopPropagation()}
          >
            <p className="eyebrow">Top up</p>
            <h3 className="mt-2 text-2xl">Add funds</h3>

            {formError && (
              <p className="mt-4 text-sm text-negative">{formError}</p>
            )}

            <form onSubmit={handleAddFunds} className="mt-6 space-y-5">
              <div>
                <label htmlFor="amount" className="eyebrow">
                  Amount ({currency})
                </label>
                <div className="relative mt-2">
                  <span className="pointer-events-none absolute inset-y-0 left-3 flex items-center text-muted">
                    {currencySymbol(currency)}
                  </span>
                  <input
                    id="amount"
                    type="number"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    className="field pl-8"
                    placeholder="1000"
                    min="1"
                    step="any"
                    autoFocus
                    required
                  />
                </div>
              </div>
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => setModalOpen(false)}
                  className="btn btn-ghost flex-1 py-3"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="btn btn-primary flex-1 py-3"
                >
                  {submitting ? (
                    <Loader2 className="h-4.5 w-4.5 animate-spin" />
                  ) : (
                    "Confirm"
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
