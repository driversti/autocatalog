import { useNavigate } from "react-router";
import { useState, type FormEvent } from "react";
import { useMakes } from "../hooks/queries";
import MakeGrid from "../components/domain/MakeGrid";
import { CardSkeleton } from "../components/ui/Skeleton";
import ErrorMessage from "../components/ui/ErrorMessage";

export default function HomePage() {
  const navigate = useNavigate();
  const [heroQuery, setHeroQuery] = useState("");
  const { data: makes, isLoading, error, refetch } = useMakes();

  function handleHeroSearch(e: FormEvent) {
    e.preventDefault();
    const q = heroQuery.trim();
    if (q) {
      navigate(`/search?q=${encodeURIComponent(q)}`);
    }
  }

  return (
    <div>
      {/* Hero */}
      <section className="text-center py-16">
        <h1
          className="text-[40px] font-semibold leading-tight mb-2"
          style={{ color: "var(--color-text-primary)" }}
        >
          Every car. Every spec.
        </h1>
        <p
          className="text-[16px] mb-8"
          style={{ color: "var(--color-text-secondary)" }}
        >
          Browse the automotive encyclopedia
        </p>
        <form onSubmit={handleHeroSearch} className="max-w-md mx-auto">
          <input
            type="search"
            placeholder="Search by make, model, engine code..."
            value={heroQuery}
            onChange={(e) => setHeroQuery(e.target.value)}
            className="w-full px-5 py-3 text-[14px] outline-none"
            style={{
              borderRadius: "var(--radius-md)",
              border: "0.5px solid var(--color-border)",
              backgroundColor: "var(--color-surface)",
              color: "var(--color-text-primary)",
              boxShadow: "var(--shadow-card)",
            }}
          />
        </form>
      </section>

      {/* Makes grid */}
      <section>
        <h2
          className="text-[11px] uppercase tracking-wider font-medium mb-4"
          style={{ color: "var(--color-text-secondary)" }}
        >
          All Makes
        </h2>

        {isLoading && (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
            {Array.from({ length: 8 }).map((_, i) => (
              <CardSkeleton key={i} />
            ))}
          </div>
        )}

        {error && (
          <ErrorMessage
            message="Unable to load makes. Please try again."
            onRetry={() => refetch()}
          />
        )}

        {makes && <MakeGrid makes={makes} />}
      </section>
    </div>
  );
}
