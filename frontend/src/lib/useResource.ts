"use client";

import { useCallback, useEffect, useState } from "react";

/**
 * Loads an async resource once `enabled` is true, and exposes a `refetch`
 * for use after mutations.
 *
 * `fetcher` must be stable (wrap it in `useCallback` in the caller). State is
 * only ever set inside promise callbacks (`.then/.catch/.finally`), never
 * synchronously in the effect body — this keeps it correct under React 19's
 * `react-hooks/set-state-in-effect` analysis while still doing real
 * client-side data fetching.
 */
export function useResource<T>(fetcher: () => Promise<T>, enabled: boolean) {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);

  const refetch = useCallback(
    () =>
      fetcher()
        .then((result) => {
          setData(result);
          return result;
        })
        .finally(() => setLoading(false)),
    [fetcher]
  );

  useEffect(() => {
    if (!enabled) return;
    let active = true;
    fetcher()
      .then((result) => {
        if (active) setData(result);
      })
      .catch((error) => {
        if (active) console.error("Failed to load resource", error);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [enabled, fetcher]);

  return { data, loading, refetch };
}
