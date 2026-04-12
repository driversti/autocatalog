import { useParams } from "react-router";
import { useMakeBySlug, useModels } from "../hooks/queries";
import Breadcrumb from "../components/layout/Breadcrumb";
import ModelGrid from "../components/domain/ModelGrid";
import { CardSkeleton } from "../components/ui/Skeleton";
import ErrorMessage from "../components/ui/ErrorMessage";

export default function MakePage() {
  const { makeSlug } = useParams<{ makeSlug: string }>();
  const { data: make, isLoading: makeLoading, error: makeError } = useMakeBySlug(makeSlug!);
  const { data: models, isLoading: modelsLoading, error: modelsError, refetch } = useModels(make?.id);

  if (makeLoading) {
    return (
      <div className="flex flex-col gap-4">
        <div className="animate-pulse h-8 w-48 rounded" style={{ backgroundColor: "var(--color-bg-tertiary)" }} />
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
          {Array.from({ length: 6 }).map((_, i) => (
            <CardSkeleton key={i} />
          ))}
        </div>
      </div>
    );
  }

  if (makeError || !make) {
    return <ErrorMessage message="Make not found." />;
  }

  return (
    <div>
      <Breadcrumb items={[{ label: "Makes", to: "/" }, { label: make.name }]} />

      <div className="mb-8">
        <h1 className="text-[28px] font-semibold" style={{ color: "var(--color-text-primary)" }}>
          {make.name}
        </h1>
        <p className="text-[14px]" style={{ color: "var(--color-text-secondary)" }}>
          {make.country}
        </p>
      </div>

      {modelsLoading && (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
          {Array.from({ length: 6 }).map((_, i) => (
            <CardSkeleton key={i} />
          ))}
        </div>
      )}

      {modelsError && (
        <ErrorMessage
          message="Unable to load models. Please try again."
          onRetry={() => refetch()}
        />
      )}

      {models && models.length === 0 && (
        <p className="text-[14px] py-8 text-center" style={{ color: "var(--color-text-secondary)" }}>
          No models yet.
        </p>
      )}

      {models && models.length > 0 && (
        <ModelGrid models={models} makeSlug={makeSlug!} />
      )}
    </div>
  );
}
