"use client";

import {
  createContext,
  useContext,
  useCallback,
  useSyncExternalStore,
  ReactNode,
} from "react";
import { useRouter } from "next/navigation";
import type { User } from "@/lib/types";

interface AuthState {
  token: string | null;
  user: User | null;
}

interface AuthContextType extends AuthState {
  login: (token: string, userData: User) => void;
  logout: () => void;
  isAuthenticated: boolean;
  isLoading: boolean;
}

const EMPTY: AuthState = { token: null, user: null };
const AUTH_EVENT = "vault-auth-change";

// localStorage is an external store. Reading it via useSyncExternalStore
// (instead of an effect) keeps auth correct on the first client render
// without a hydration mismatch and without synchronous setState in effects.
let snapshot: AuthState = EMPTY;
let snapshotKey = "\0";

function readSnapshot(): AuthState {
  if (typeof window === "undefined") return EMPTY;
  const token = localStorage.getItem("token");
  const userRaw = localStorage.getItem("user");
  const key = `${token ?? ""}\0${userRaw ?? ""}`;
  if (key === snapshotKey) return snapshot; // stable reference between reads
  snapshotKey = key;

  let user: User | null = null;
  if (userRaw) {
    try {
      user = JSON.parse(userRaw) as User;
    } catch {
      user = null;
    }
  }
  snapshot = { token: token ?? null, user };
  return snapshot;
}

function subscribe(callback: () => void): () => void {
  window.addEventListener("storage", callback);
  window.addEventListener(AUTH_EVENT, callback);
  return () => {
    window.removeEventListener("storage", callback);
    window.removeEventListener(AUTH_EVENT, callback);
  };
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const router = useRouter();
  const { token, user } = useSyncExternalStore(
    subscribe,
    readSnapshot,
    () => EMPTY
  );

  const login = useCallback(
    (newToken: string, userData: User) => {
      localStorage.setItem("token", newToken);
      localStorage.setItem("user", JSON.stringify(userData));
      window.dispatchEvent(new Event(AUTH_EVENT));
      router.push("/dashboard");
    },
    [router]
  );

  const logout = useCallback(() => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    window.dispatchEvent(new Event(AUTH_EVENT));
    router.push("/login");
  }, [router]);

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        login,
        logout,
        isAuthenticated: !!token,
        isLoading: false,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
