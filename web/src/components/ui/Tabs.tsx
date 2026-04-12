interface TabsProps {
  items: string[];
  active: string;
  onChange: (item: string) => void;
}

export default function Tabs({ items, active, onChange }: TabsProps) {
  return (
    <div
      className="flex gap-0"
      style={{ borderBottom: "0.5px solid var(--color-border)" }}
    >
      {items.map((item) => (
        <button
          key={item}
          onClick={() => onChange(item)}
          className="px-4 py-2 text-[13px] font-medium cursor-pointer bg-transparent border-none"
          style={{
            color: item === active ? "var(--color-accent)" : "var(--color-text-secondary)",
            borderBottom: item === active ? "2px solid var(--color-accent)" : "2px solid transparent",
            transition: "color 200ms ease, border-color 200ms ease",
          }}
        >
          {item}
        </button>
      ))}
    </div>
  );
}
