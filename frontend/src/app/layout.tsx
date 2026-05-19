import type { Metadata, Viewport } from "next";
import { Fraunces, Hanken_Grotesk, JetBrains_Mono } from "next/font/google";
import "./globals.css";
import { AuthProvider } from "@/context/AuthContext";

// Display: an expressive, high-contrast serif with optical sizing.
const fraunces = Fraunces({
  variable: "--font-fraunces",
  subsets: ["latin"],
  axes: ["opsz", "SOFT"],
});

// Body/UI: a warm humanist grotesque — not a generic system sans.
const hanken = Hanken_Grotesk({
  variable: "--font-hanken",
  subsets: ["latin"],
});

// Figures/data: tabular mono for balances and the transaction ledger.
const jetbrains = JetBrains_Mono({
  variable: "--font-jetbrains",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: {
    default: "Vault — Payment Wallet",
    template: "%s · Vault",
  },
  description:
    "Vault is a precision payment wallet: move money, track every transaction, and earn rewards with a ledger you can trust.",
};

export const viewport: Viewport = {
  themeColor: "#0a0b0a",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="en"
      className={`${fraunces.variable} ${hanken.variable} ${jetbrains.variable}`}
    >
      <body className="grain antialiased">
        <AuthProvider>{children}</AuthProvider>
      </body>
    </html>
  );
}
