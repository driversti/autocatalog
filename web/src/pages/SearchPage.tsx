import { useMemo } from "react";
import { useSearchParams } from "react-router";
import { useQueries } from "@tanstack/react-query";
import Fuse from "fuse.js";
import { useMakes } from "../hooks/queries";
import { fetchModels } from "../api/client";
import FilterSidebar from "../components/ui/FilterSidebar";
import SearchResults from "../components/domain/SearchResults";
import Skeleton from "../components/ui/Skeleton";
import type { MakeResponse, CarModelResponse } from "../types/api";

interface SearchResult {
  type: "make" | "model";
  make: MakeResponse;
  model?: CarModelResponse;
}

const FILTER_GROUPS = [
  {
    label: "Fuel Type",
    paramKey: "fuelType",
    options: ["PETROL", "DIESEL", "HYBRID", "PLUG_IN_HYBRID", "ELECTRIC", "HYDROGEN", "LPG"],
  },
  {
    label: "Body Style",
    paramKey: "bodyStyle",
    options: ["SEDAN", "HATCHBACK", "LIFTBACK", "WAGON", "COUPE", "CONVERTIBLE", "SUV", "CROSSOVER", "MINIVAN", "PICKUP", "VAN"],
  },
  {
    label: "Drivetrain",
    paramKey: "drivetrain",
    options: ["FWD", "RWD", "AWD", "FOUR_WD"],
  },
];

export default function SearchPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const query = searchParams.get("q") ?? "";

  const { data: makes, isLoading: makesLoading } = useMakes();

  const modelQueries = useQueries({
    queries: (makes ?? []).map((make) => ({
      queryKey: ["models", { makeId: make.id }],
      queryFn: () => fetchModels(make.id),
      staleTime: 2 * 60 * 1000,
    })),
  });
  const allModels = modelQueries.flatMap((q) => q.data ?? []);
  const modelsLoading = modelQueries.some((q) => q.isLoading);

  // Build search index
  const searchItems = useMemo(() => {
    if (!makes) return [];
    const items: SearchResult[] = [];

    makes.forEach((make) => {
      items.push({ type: "make", make });
      const models = allModels.filter((m) => m.makeId === make.id);
      models.forEach((model) => {
        items.push({ type: "model", make, model });
      });
    });

    return items;
  }, [makes, allModels]);

  const fuse = useMemo(
    () =>
      new Fuse(searchItems, {
        keys: [
          { name: "make.name", weight: 1 },
          { name: "make.country", weight: 0.5 },
          { name: "model.name", weight: 1 },
        ],
        threshold: 0.3,
      }),
    [searchItems],
  );

  const results = query ? fuse.search(query).map((r) => r.item) : searchItems;

  // Parse selected filters from URL
  const selected: Record<string, Set<string>> = {};
  for (const group of FILTER_GROUPS) {
    const values = searchParams.getAll(group.paramKey);
    if (values.length > 0) {
      selected[group.paramKey] = new Set(values);
    }
  }

  function handleFilterChange(paramKey: string, value: string, checked: boolean) {
    const next = new URLSearchParams(searchParams);
    if (checked) {
      next.append(paramKey, value);
    } else {
      const values = next.getAll(paramKey).filter((v) => v !== value);
      next.delete(paramKey);
      values.forEach((v) => next.append(paramKey, v));
    }
    setSearchParams(next);
  }

  const isLoading = makesLoading || modelsLoading;

  return (
    <div>
      <h1 className="text-[28px] font-semibold mb-2" style={{ color: "var(--color-text-primary)" }}>
        Search
      </h1>
      {query && (
        <p className="text-[14px] mb-6" style={{ color: "var(--color-text-secondary)" }}>
          {results.length} result{results.length !== 1 ? "s" : ""} for "{query}"
        </p>
      )}

      <div className="flex gap-8">
        <FilterSidebar groups={FILTER_GROUPS} selected={selected} onChange={handleFilterChange} />
        <div className="flex-1">
          {isLoading ? (
            <div className="flex flex-col gap-2">
              {Array.from({ length: 5 }).map((_, i) => (
                <Skeleton key={i} height="56px" />
              ))}
            </div>
          ) : (
            <SearchResults results={results} />
          )}
        </div>
      </div>
    </div>
  );
}
