// Formatting helpers shared across the app. Centralised so currency, number,
// and date rendering stay consistent everywhere.

const CURRENCY_SYMBOLS: Record<string, string> = {
  INR: "₹",
  USD: "$",
  EUR: "€",
  GBP: "£",
};

export function currencySymbol(currency?: string): string {
  return CURRENCY_SYMBOLS[currency ?? "INR"] ?? "₹";
}

/**
 * Formats a monetary amount with grouping. Whole numbers stay clean
 * (₹5,000); fractional amounts show two decimals (₹150.50).
 */
export function formatMoney(amount?: number | null, currency = "INR"): string {
  const value = Number(amount ?? 0);
  const hasFraction = !Number.isInteger(value);
  const formatted = value.toLocaleString("en-IN", {
    minimumFractionDigits: hasFraction ? 2 : 0,
    maximumFractionDigits: 2,
  });
  return `${currencySymbol(currency)}${formatted}`;
}

export function formatNumber(value?: number | null): string {
  return Number(value ?? 0).toLocaleString("en-IN");
}

/**
 * Backend `LocalDateTime` fields may arrive as an ISO string or as a Jackson
 * tuple `[year, month, day, hour, minute, second, nanos]`. Parse both safely.
 */
export function parseDate(value?: string | number[] | null): Date | null {
  if (value == null) return null;
  if (Array.isArray(value)) {
    const [y, mo = 1, d = 1, h = 0, mi = 0, s = 0] = value;
    if (y == null) return null;
    return new Date(y, mo - 1, d, h, mi, s);
  }
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
}

export function formatDate(value?: string | number[] | null): string {
  const date = parseDate(value);
  if (!date) return "—";
  return date.toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

export function formatDateTime(value?: string | number[] | null): string {
  const date = parseDate(value);
  if (!date) return "—";
  return `${date.toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
  })} · ${date.toLocaleTimeString("en-IN", {
    hour: "2-digit",
    minute: "2-digit",
  })}`;
}

export function initials(firstName?: string, lastName?: string): string {
  const a = firstName?.trim()?.[0] ?? "";
  const b = lastName?.trim()?.[0] ?? "";
  return (a + b).toUpperCase() || "U";
}
