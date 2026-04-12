interface SpecRow {
  label: string;
  value: string | number | null | undefined;
}

export default function SpecsTable({ rows }: { rows: SpecRow[] }) {
  const filtered = rows.filter((r) => r.value != null && r.value !== "");

  return (
    <div className="grid grid-cols-2 gap-x-8 gap-y-2 py-3">
      {filtered.map((row) => (
        <div key={row.label} className="flex justify-between py-1.5"
          style={{ borderBottom: "0.5px solid var(--color-border)" }}
        >
          <span className="text-[13px]" style={{ color: "var(--color-text-secondary)" }}>
            {row.label}
          </span>
          <span className="text-[13px] font-medium" style={{ color: "var(--color-text-primary)" }}>
            {row.value}
          </span>
        </div>
      ))}
    </div>
  );
}
