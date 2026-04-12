import { useParams } from "react-router";
import { useMakeBySlug, useModels, useGenerations } from "../hooks/queries";
import Breadcrumb from "../components/layout/Breadcrumb";
import GenerationSection from "../components/domain/GenerationSection";
import Skeleton from "../components/ui/Skeleton";
import ErrorMessage from "../components/ui/ErrorMessage";

export default function ModelPage() {
  const { makeSlug, modelSlug } = useParams<{ makeSlug: string; modelSlug: string }>();
  const { data: make, isLoading: makeLoading } = useMakeBySlug(makeSlug!);
  const { data: models, isLoading: modelsLoading } = useModels(make?.id);

  const model = models?.find((m) => m.slug === modelSlug);
  const { data: generations, isLoading: gensLoading, error: gensError, refetch } = useGenerations(model?.id);

  const isLoading = makeLoading || modelsLoading;

  if (isLoading) {
    return (
      <div className="flex flex-col gap-4">
        <Skeleton width="200px" height="16px" />
        <Skeleton width="300px" height="32px" />
        <Skeleton width="150px" height="16px" />
        <Skeleton height="200px" />
      </div>
    );
  }

  if (!make || !model) {
    return <ErrorMessage message="Model not found." />;
  }

  // Sort generations: current (no yearTo) first, then by yearFrom descending
  const sorted = generations
    ? [...generations].sort((a, b) => {
        if (a.yearTo == null && b.yearTo != null) return -1;
        if (a.yearTo != null && b.yearTo == null) return 1;
        return b.yearFrom - a.yearFrom;
      })
    : [];

  return (
    <div>
      <Breadcrumb
        items={[
          { label: "Makes", to: "/" },
          { label: make.name, to: `/${makeSlug}` },
          { label: model.name },
        ]}
      />

      <div className="mb-8">
        <h1 className="text-[28px] font-semibold" style={{ color: "var(--color-text-primary)" }}>
          {make.name} {model.name}
        </h1>
      </div>

      {gensLoading && (
        <div className="flex flex-col gap-4">
          <Skeleton height="200px" />
          <Skeleton height="60px" />
        </div>
      )}

      {gensError && (
        <ErrorMessage
          message="Unable to load generations. Please try again."
          onRetry={() => refetch()}
        />
      )}

      {sorted.length === 0 && !gensLoading && !gensError && (
        <p className="text-[14px] py-8 text-center" style={{ color: "var(--color-text-secondary)" }}>
          No generations yet.
        </p>
      )}

      <div className="flex flex-col gap-4">
        {sorted.map((gen, i) => (
          <GenerationSection key={gen.id} generation={gen} defaultOpen={i === 0} />
        ))}
      </div>
    </div>
  );
}
