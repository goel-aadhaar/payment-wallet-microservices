"use client";

import { useCallback, useMemo } from "react";
import Link from "next/link";
import api from "@/lib/api";
import { useAuth } from "@/context/AuthContext";
import { useResource } from "@/lib/useResource";
import type { Reward } from "@/lib/types";
import { formatNumber, formatDate } from "@/lib/format";
import { Sparkles, ArrowRight, Star } from "lucide-react";

export default function RewardsPage() {
  const { user } = useAuth();

  const fetchRewards = useCallback(async (): Promise<Reward[]> => {
    const { data } = await api.get<Reward[]>(
      `/api/v1/rewards/user/${user!.id}`
    );
    return [...data].sort(
      (a, b) =>
        new Date(b.sentAt as string).getTime() -
        new Date(a.sentAt as string).getTime()
    );
  }, [user]);

  const { data, loading } = useResource(fetchRewards, !!user);
  const rewards = useMemo(() => data ?? [], [data]);
  const total = rewards.reduce((sum, r) => sum + (r.points || 0), 0);

  return (
    <div className="mx-auto max-w-4xl">
      <header className="reveal mb-8">
        <p className="eyebrow">Loyalty</p>
        <h1 className="mt-2 text-3xl">Rewards</h1>
      </header>

      {/* Points statement */}
      <section
        className="reveal panel relative overflow-hidden p-8 md:p-10"
        style={{ animationDelay: "80ms" }}
      >
        <div
          className="engraved-grid absolute inset-0 -z-10 opacity-60"
          aria-hidden
        />
        <div className="flex flex-wrap items-center justify-between gap-6">
          <div>
            <p className="eyebrow">Points earned</p>
            <p className="mt-4 font-display text-7xl leading-none tabular-nums md:text-8xl">
              {formatNumber(total)}
            </p>
            <p className="mt-4 text-sm text-muted">
              Earned across {rewards.length}{" "}
              {rewards.length === 1 ? "reward" : "rewards"}. Every settled
              transfer adds more.
            </p>
          </div>
          <span className="flex h-20 w-20 items-center justify-center rounded-full border border-accent text-accent">
            <Sparkles className="h-9 w-9" strokeWidth={1.4} />
          </span>
        </div>
      </section>

      {/* History */}
      <section className="reveal panel mt-6" style={{ animationDelay: "160ms" }}>
        <div className="border-b border-line p-6 md:px-8">
          <h2 className="text-xl">Reward history</h2>
        </div>

        {loading ? (
          <div className="space-y-px">
            {[0, 1, 2].map((i) => (
              <div key={i} className="skeleton h-20" />
            ))}
          </div>
        ) : rewards.length === 0 ? (
          <div className="flex flex-col items-center gap-3 px-6 py-20 text-center">
            <Star className="h-10 w-10 text-faint" strokeWidth={1.4} />
            <p className="text-lg">No rewards yet</p>
            <p className="text-sm text-muted">
              Make your first transfer to start earning.
            </p>
            <Link
              href="/transactions"
              className="mt-2 inline-flex items-center gap-1.5 text-sm text-accent hover:underline"
            >
              Go to transactions
              <ArrowRight className="h-4 w-4" />
            </Link>
          </div>
        ) : (
          <ul className="divide-y divide-line">
            {rewards.map((reward) => (
              <li
                key={reward.id}
                className="flex items-center justify-between gap-4 p-6 md:px-8"
              >
                <div className="flex items-center gap-4">
                  <span className="flex h-10 w-10 items-center justify-center rounded-full border border-accent/40 text-accent">
                    <Star className="h-4.5 w-4.5" strokeWidth={1.8} />
                  </span>
                  <div>
                    <p className="text-sm">Cashback reward</p>
                    <p className="font-mono text-xs text-faint">
                      TX-{reward.transactionId} · {formatDate(reward.sentAt)}
                    </p>
                  </div>
                </div>
                <span className="tnum text-accent">
                  +{formatNumber(reward.points)}
                  <span className="ml-1 font-mono text-[0.65rem] uppercase tracking-wider text-faint">
                    pts
                  </span>
                </span>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}
