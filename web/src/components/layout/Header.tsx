import { Link, useNavigate } from "react-router";
import { useState, type FormEvent } from "react";
import ThemeToggle from "../ui/ThemeToggle";

export default function Header() {
  const navigate = useNavigate();
  const [query, setQuery] = useState("");

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    const q = query.trim();
    if (q) {
      navigate(`/search?q=${encodeURIComponent(q)}`);
    }
  }

  return (
    <header
      className="sticky top-0 z-50 backdrop-blur-lg"
      style={{
        backgroundColor: "color-mix(in srgb, var(--color-bg) 80%, transparent)",
        borderBottom: "0.5px solid var(--color-border)",
      }}
    >
      <div className="mx-auto flex items-center justify-between px-6 py-3 max-w-6xl">
        <Link
          to="/"
          className="text-[16px] font-semibold no-underline"
          style={{ color: "var(--color-text-primary)" }}
        >
          AutoCatalog
        </Link>

        <div className="flex items-center gap-2">
          <form onSubmit={handleSubmit}>
            <input
              type="search"
              placeholder="Search makes, models..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="rounded-[var(--radius-sm)] px-3 py-1.5 text-[13px] w-52 outline-none"
              style={{
                backgroundColor: "var(--color-bg-secondary)",
                color: "var(--color-text-primary)",
                border: "0.5px solid var(--color-border)",
              }}
            />
          </form>
          <ThemeToggle />
        </div>
      </div>
    </header>
  );
}
