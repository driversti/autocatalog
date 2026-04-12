import { Link } from "react-router";

export interface BreadcrumbItem {
  label: string;
  to?: string;
}

export default function Breadcrumb({ items }: { items: BreadcrumbItem[] }) {
  return (
    <nav
      className="flex items-center gap-1.5 text-[13px] mb-4"
      style={{ color: "var(--color-text-secondary)" }}
    >
      {items.map((item, i) => (
        <span key={i} className="flex items-center gap-1.5">
          {i > 0 && <span>›</span>}
          {item.to ? (
            <Link to={item.to} className="hover:underline" style={{ color: "var(--color-text-secondary)" }}>
              {item.label}
            </Link>
          ) : (
            <span style={{ color: "var(--color-text-primary)" }}>{item.label}</span>
          )}
        </span>
      ))}
    </nav>
  );
}
