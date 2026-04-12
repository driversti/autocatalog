interface SkeletonProps {
  className?: string;
  width?: string;
  height?: string;
}

export default function Skeleton({ className = "", width, height }: SkeletonProps) {
  return (
    <div
      className={`animate-pulse rounded-[var(--radius-sm)] ${className}`}
      style={{
        backgroundColor: "var(--color-bg-tertiary)",
        width,
        height,
        minHeight: height ?? "16px",
      }}
    />
  );
}

export function CardSkeleton() {
  return (
    <div
      className="overflow-hidden"
      style={{
        borderRadius: "var(--radius-md)",
        border: "0.5px solid var(--color-border)",
      }}
    >
      <Skeleton height="160px" className="rounded-none" />
      <div className="p-4 flex flex-col gap-2">
        <Skeleton width="60%" height="16px" />
        <Skeleton width="40%" height="12px" />
      </div>
    </div>
  );
}

export function VariantSkeleton() {
  return (
    <div className="flex flex-col gap-2">
      <Skeleton height="48px" />
      <Skeleton height="48px" />
      <Skeleton height="48px" />
    </div>
  );
}
