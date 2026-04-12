import { useNavigate } from "react-router";
import type { MakeResponse, CarModelResponse } from "../../types/api";

interface SearchResult {
  type: "make" | "model";
  make: MakeResponse;
  model?: CarModelResponse;
}

export default function SearchResults({ results }: { results: SearchResult[] }) {
  const navigate = useNavigate();

  if (results.length === 0) {
    return (
      <p className="text-[14px] py-8 text-center" style={{ color: "var(--color-text-secondary)" }}>
        No results found. Try a different search.
      </p>
    );
  }

  return (
    <div className="flex flex-col gap-2">
      {results.map((result, i) => (
        <div
          key={i}
          onClick={() => {
            if (result.type === "model" && result.model) {
              navigate(`/${result.make.slug}/${result.model.slug}`);
            } else {
              navigate(`/${result.make.slug}`);
            }
          }}
          className="flex items-center gap-3 p-3 cursor-pointer"
          style={{
            borderRadius: "var(--radius-sm)",
            border: "0.5px solid var(--color-border)",
            transition: "background-color 200ms ease",
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.backgroundColor = "var(--color-bg-secondary)";
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.backgroundColor = "transparent";
          }}
        >
          <div
            className="w-10 h-10 rounded-[var(--radius-sm)] flex items-center justify-center text-[12px] font-medium shrink-0"
            style={{
              backgroundColor: "var(--color-bg-secondary)",
              color: "var(--color-text-secondary)",
            }}
          >
            {result.make.name.charAt(0)}
          </div>
          <div>
            <div className="text-[13px] font-medium" style={{ color: "var(--color-text-primary)" }}>
              {result.type === "model" && result.model
                ? `${result.make.name} ${result.model.name}`
                : result.make.name}
            </div>
            <div className="text-[11px]" style={{ color: "var(--color-text-secondary)" }}>
              {result.type === "model" ? "Model" : `${result.make.country} · Make`}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
