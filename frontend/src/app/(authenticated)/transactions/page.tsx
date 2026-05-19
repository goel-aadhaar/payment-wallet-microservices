"use client";

import { useState, useCallback, type SyntheticEvent } from "react";
import api from "@/lib/api";
import { useAuth } from "@/context/AuthContext";
import { useResource } from "@/lib/useResource";
import type { Transaction } from "@/lib/types";
import { formatMoney, formatDateTime } from "@/lib/format";
import { ArrowUpRight, ArrowDownLeft, Send, Loader2, Receipt } from "lucide-react";

type Filter = "all" | "sent" | "received";

const STATUS_TONE: Record<string, string> = {
  SUCCESS: "text-positive",
  PENDING: "text-warn",
  FAILED: "text-negative",
};

export default function TransactionsPage() {
  const { user } = useAuth();
  const [filter, setFilter] = useState<Filter>("all");
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ receiverId: "", amount: "" });
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState("");

  const fetchTransactions = useCallback(async (): Promise<Transaction[]> => {
    const userId = user!.id;
    const { data } = await api.get<Transaction[]>("/api/v1/transactions");
    return data
      .filter((t) => t.senderId === userId || t.receiverId === userId)
      .sort(
        (a, b) =>
          new Date(b.timestamp as string).getTime() -
          new Date(a.timestamp as string).getTime()
      );
  }, [user]);

  const { data, loading, refetch } = useResource(fetchTransactions, !!user);
  const transactions = data ?? [];

  // React Compiler memoizes this automatically — no manual useMemo needed.
  const visible = transactions.filter((t) => {
    if (filter === "sent") return t.senderId === user?.id;
    if (filter === "received") return t.receiverId === user?.id;
    return true;
  });

  const handleSend = async (e: SyntheticEvent) => {
    e.preventDefault();
    const receiverId = parseInt(form.receiverId, 10);
    const amount = parseFloat(form.amount);
    if (!Number.isInteger(receiverId) || receiverId <= 0) {
      setFormError("Enter a valid recipient account ID.");
      return;
    }
    if (receiverId === user?.id) {
      setFormError("You can't send money to yourself.");
      return;
    }
    if (!Number.isFinite(amount) || amount <= 0) {
      setFormError("Enter an amount greater than zero.");
      return;
    }
    setSubmitting(true);
    setFormError("");
    try {
      await api.post("/api/v1/transactions", {
        senderId: user?.id,
        receiverId,
        amount,
      });
      setModalOpen(false);
      setForm({ receiverId: "", amount: "" });
      await refetch();
    } catch (err) {
      const message =
        (err as { response?: { data?: { message?: string } } })?.response?.data
          ?.message ?? "Transaction failed. Please try again.";
      setFormError(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="mx-auto max-w-4xl">
      <header className="reveal mb-8 flex flex-wrap items-end justify-between gap-4">
        <div>
          <p className="eyebrow">Ledger</p>
          <h1 className="mt-2 text-3xl">Transactions</h1>
        </div>
        <button onClick={() => setModalOpen(true)} className="btn btn-primary">
          <Send className="h-4 w-4" strokeWidth={1.8} />
          Send money
        </button>
      </header>

      {/* Filter */}
      <div
        className="reveal mb-5 inline-flex rounded-lg border border-line p-1 text-sm"
        style={{ animationDelay: "60ms" }}
      >
        {(["all", "sent", "received"] as Filter[]).map((f) => (
          <button
            key={f}
            onClick={() => setFilter(f)}
            className={`rounded-md px-4 py-1.5 capitalize transition-colors ${
              filter === f
                ? "bg-accent-soft text-accent"
                : "text-muted hover:text-paper"
            }`}
          >
            {f}
          </button>
        ))}
      </div>

      <section
        className="reveal panel overflow-hidden"
        style={{ animationDelay: "120ms" }}
      >
        {loading ? (
          <div className="space-y-px">
            {[0, 1, 2, 3].map((i) => (
              <div key={i} className="skeleton h-[4.5rem]" />
            ))}
          </div>
        ) : visible.length === 0 ? (
          <div className="flex flex-col items-center gap-3 py-20 text-center">
            <Receipt className="h-10 w-10 text-faint" strokeWidth={1.4} />
            <p className="text-muted">No transactions to show.</p>
          </div>
        ) : (
          <>
            {/* Desktop table */}
            <table className="hidden w-full md:table">
              <thead>
                <tr className="border-b border-line text-left">
                  <th className="eyebrow p-5 font-normal">Counterparty</th>
                  <th className="eyebrow p-5 font-normal">Date</th>
                  <th className="eyebrow p-5 font-normal">Status</th>
                  <th className="eyebrow p-5 text-right font-normal">Amount</th>
                </tr>
              </thead>
              <tbody>
                {visible.map((tx) => {
                  const sent = tx.senderId === user?.id;
                  return (
                    <tr
                      key={tx.id}
                      className="border-b border-line transition-colors last:border-0 hover:bg-surface-2"
                    >
                      <td className="p-5">
                        <div className="flex items-center gap-3">
                          <span
                            className={`flex h-9 w-9 items-center justify-center rounded-full border ${
                              sent
                                ? "border-negative/40 text-negative"
                                : "border-positive/40 text-positive"
                            }`}
                          >
                            {sent ? (
                              <ArrowUpRight className="h-4 w-4" />
                            ) : (
                              <ArrowDownLeft className="h-4 w-4" />
                            )}
                          </span>
                          <div>
                            <p className="text-sm">
                              {sent
                                ? `Account #${tx.receiverId}`
                                : `Account #${tx.senderId}`}
                            </p>
                            <p className="font-mono text-xs text-faint">
                              TX-{tx.id}
                            </p>
                          </div>
                        </div>
                      </td>
                      <td className="p-5 text-sm text-muted">
                        {formatDateTime(tx.timestamp)}
                      </td>
                      <td className="p-5">
                        <span
                          className={`font-mono text-xs uppercase tracking-wider ${
                            STATUS_TONE[tx.status] ?? "text-muted"
                          }`}
                        >
                          {tx.status}
                        </span>
                      </td>
                      <td
                        className={`tnum p-5 text-right ${
                          sent ? "text-paper" : "text-positive"
                        }`}
                      >
                        {sent ? "−" : "+"}
                        {formatMoney(tx.amount)}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>

            {/* Mobile cards */}
            <ul className="divide-y divide-line md:hidden">
              {visible.map((tx) => {
                const sent = tx.senderId === user?.id;
                return (
                  <li
                    key={tx.id}
                    className="flex items-center justify-between gap-4 p-5"
                  >
                    <div className="flex min-w-0 items-center gap-3">
                      <span
                        className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-full border ${
                          sent
                            ? "border-negative/40 text-negative"
                            : "border-positive/40 text-positive"
                        }`}
                      >
                        {sent ? (
                          <ArrowUpRight className="h-4 w-4" />
                        ) : (
                          <ArrowDownLeft className="h-4 w-4" />
                        )}
                      </span>
                      <div className="min-w-0">
                        <p className="truncate text-sm">
                          {sent
                            ? `Account #${tx.receiverId}`
                            : `Account #${tx.senderId}`}
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
                        {formatMoney(tx.amount)}
                      </p>
                      <p
                        className={`font-mono text-[0.65rem] uppercase tracking-wider ${
                          STATUS_TONE[tx.status] ?? "text-faint"
                        }`}
                      >
                        {tx.status}
                      </p>
                    </div>
                  </li>
                );
              })}
            </ul>
          </>
        )}
      </section>

      {/* Send modal */}
      {modalOpen && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-ink/70 p-4 backdrop-blur-sm"
          onClick={() => setModalOpen(false)}
        >
          <div
            className="modal-in panel w-full max-w-md p-7"
            onClick={(e) => e.stopPropagation()}
          >
            <p className="eyebrow">Transfer</p>
            <h3 className="mt-2 text-2xl">Send money</h3>

            {formError && (
              <p className="mt-4 text-sm text-negative">{formError}</p>
            )}

            <form onSubmit={handleSend} className="mt-6 space-y-5">
              <div>
                <label htmlFor="receiver" className="eyebrow">
                  Recipient account ID
                </label>
                <input
                  id="receiver"
                  type="number"
                  value={form.receiverId}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, receiverId: e.target.value }))
                  }
                  className="field mt-2"
                  placeholder="e.g. 2"
                  min="1"
                  required
                />
              </div>
              <div>
                <label htmlFor="sendAmount" className="eyebrow">
                  Amount (INR)
                </label>
                <div className="relative mt-2">
                  <span className="pointer-events-none absolute inset-y-0 left-3 flex items-center text-muted">
                    ₹
                  </span>
                  <input
                    id="sendAmount"
                    type="number"
                    value={form.amount}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, amount: e.target.value }))
                    }
                    className="field pl-8"
                    placeholder="150"
                    min="1"
                    step="any"
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
                    "Send"
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
