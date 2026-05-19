// Shared API types — mirrors the backend DTOs/entities so the UI is fully typed.

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
}

export interface Wallet {
  id: number;
  userId: number;
  currency: string;
  /** Total balance including funds on hold. */
  balance: number;
  /** Spendable balance (total minus active holds). */
  availableBalance: number;
}

export type TransactionStatus = "SUCCESS" | "PENDING" | "FAILED" | string;

export interface Transaction {
  id: number;
  senderId: number;
  receiverId: number;
  amount: number;
  /** ISO string, or a Jackson LocalDateTime tuple — parse via lib/format. */
  timestamp: string | number[];
  status: TransactionStatus;
}

export interface Reward {
  id: number;
  userId: number;
  points: number;
  sentAt: string | number[];
  transactionId: number;
}

export interface JwtResponse {
  token: string;
}
