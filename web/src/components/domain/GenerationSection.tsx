import { useState } from "react";
import { useBodies, useVariants } from "../../hooks/queries";
import Tabs from "../ui/Tabs";
import Expandable from "../ui/Expandable";
import VariantRow from "./VariantRow";
import { VariantSkeleton } from "../ui/Skeleton";
import type { GenerationResponse } from "../../types/api";

function formatBodyStyle(style: string): string {
  return style.charAt(0) + style.slice(1).toLowerCase().replace(/_/g, " ");
}

export default function GenerationSection({
  generation,
  defaultOpen,
}: {
  generation: GenerationResponse;
  defaultOpen: boolean;
}) {
  const { data: bodies, isLoading: bodiesLoading } = useBodies(generation.id);
  const [activeBody, setActiveBody] = useState<string | null>(null);

  const selectedBody = bodies?.find((b) => b.id === activeBody) ?? bodies?.[0];
  const { data: variants, isLoading: variantsLoading } = useVariants(selectedBody?.id);

  // Set active body to first one when bodies load
  if (bodies && bodies.length > 0 && !activeBody) {
    setActiveBody(bodies[0].id);
  }

  const isCurrent = generation.yearTo == null;

  const header = (
    <div className="flex items-center gap-3">
      <div>
        <div className="text-[16px] font-semibold" style={{ color: isCurrent ? "var(--color-text-primary)" : "var(--color-text-secondary)" }}>
          {generation.name}
        </div>
        <div className="text-[12px]" style={{ color: "var(--color-text-secondary)" }}>
          {generation.yearFrom} – {generation.yearTo ?? "present"}
        </div>
      </div>
      {isCurrent && (
        <span
          className="text-[10px] font-medium px-2 py-0.5 rounded"
          style={{ backgroundColor: "var(--color-accent)", color: "#ffffff" }}
        >
          CURRENT
        </span>
      )}
    </div>
  );

  return (
    <Expandable header={header} defaultOpen={defaultOpen}>
      {bodiesLoading && (
        <div className="p-4">
          <VariantSkeleton />
        </div>
      )}

      {bodies && bodies.length === 0 && (
        <p className="p-4 text-[13px]" style={{ color: "var(--color-text-secondary)" }}>
          No body configurations yet.
        </p>
      )}

      {bodies && bodies.length > 0 && (
        <>
          <Tabs
            items={bodies.map((b) => formatBodyStyle(b.bodyStyle))}
            active={formatBodyStyle(selectedBody?.bodyStyle ?? "")}
            onChange={(label) => {
              const body = bodies.find((b) => formatBodyStyle(b.bodyStyle) === label);
              if (body) setActiveBody(body.id);
            }}
          />

          {selectedBody && (
            <div
              className="flex gap-6 px-4 py-2 text-[11px] flex-wrap"
              style={{ color: "var(--color-text-secondary)", borderBottom: "0.5px solid var(--color-border)" }}
            >
              <span>{selectedBody.lengthMm} × {selectedBody.widthMm} × {selectedBody.heightMm} mm</span>
              <span>Wheelbase: {selectedBody.wheelbaseMm} mm</span>
              {selectedBody.trunkVolumeLitres && <span>Trunk: {selectedBody.trunkVolumeLitres} L</span>}
              {selectedBody.groundClearanceMm && <span>Clearance: {selectedBody.groundClearanceMm} mm</span>}
            </div>
          )}

          <div className="p-4">
            <h4
              className="text-[11px] uppercase tracking-wider font-medium mb-3"
              style={{ color: "var(--color-text-secondary)" }}
            >
              Variants
            </h4>

            {variantsLoading && <VariantSkeleton />}

            {variants && variants.length === 0 && (
              <p className="text-[13px]" style={{ color: "var(--color-text-secondary)" }}>
                No variants yet.
              </p>
            )}

            {variants && variants.length > 0 && (
              <div className="flex flex-col gap-2">
                {variants.map((v) => (
                  <VariantRow key={v.id} variant={v} />
                ))}
              </div>
            )}
          </div>
        </>
      )}
    </Expandable>
  );
}
