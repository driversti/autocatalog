import { useState, type ReactNode } from "react";

interface ExpandableProps {
  header: ReactNode;
  children: ReactNode;
  defaultOpen?: boolean;
}

export default function Expandable({ header, children, defaultOpen = false }: ExpandableProps) {
  const [open, setOpen] = useState(defaultOpen);

  return (
    <div
      className="overflow-hidden"
      style={{
        borderRadius: "var(--radius-md)",
        border: "0.5px solid var(--color-border)",
      }}
    >
      <button
        onClick={() => setOpen(!open)}
        className="w-full flex items-center justify-between p-4 cursor-pointer bg-transparent border-none text-left"
        style={{ backgroundColor: "var(--color-bg-secondary)" }}
      >
        <div>{header}</div>
        <span
          className="text-lg transition-transform duration-300"
          style={{
            color: "var(--color-text-tertiary)",
            transform: open ? "rotate(180deg)" : "rotate(0deg)",
          }}
        >
          ﹀
        </span>
      </button>
      <div
        className="transition-all duration-300 overflow-hidden"
        style={{
          maxHeight: open ? "2000px" : "0",
          opacity: open ? 1 : 0,
        }}
      >
        {children}
      </div>
    </div>
  );
}
