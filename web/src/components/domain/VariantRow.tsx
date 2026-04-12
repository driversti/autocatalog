import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { fetchEngineById, fetchTransmissionById } from "../../api/client";
import SpecsTable from "../ui/SpecsTable";
import type { VariantResponse } from "../../types/api";

function formatDrivetrain(d: string): string {
  return d === "FOUR_WD" ? "4WD" : d;
}

export default function VariantRow({ variant }: { variant: VariantResponse }) {
  const [open, setOpen] = useState(false);

  const { data: engines } = useQuery({
    queryKey: ["engines", "batch", variant.engineIds],
    queryFn: () => Promise.all(variant.engineIds.map(fetchEngineById)),
    enabled: open && variant.engineIds.length > 0,
    staleTime: 5 * 60 * 1000,
  });

  const { data: transmission } = useQuery({
    queryKey: ["transmissions", variant.transmissionId],
    queryFn: () => fetchTransmissionById(variant.transmissionId),
    enabled: open,
    staleTime: 5 * 60 * 1000,
  });

  const primaryEngine = engines?.[0];
  const summaryParts: string[] = [];
  if (primaryEngine) {
    summaryParts.push(primaryEngine.code || primaryEngine.name);
    summaryParts.push(`${primaryEngine.powerKw} kW`);
  }
  if (transmission) {
    const typeLabel =
      transmission.type === "MANUAL" ? "MT" :
      transmission.type === "AUTOMATIC" ? "AT" :
      transmission.type === "DCT" ? "DCT" :
      transmission.type === "CVT" ? "CVT" :
      "1-speed";
    summaryParts.push(`${transmission.gearCount}-speed ${typeLabel}`);
  }
  summaryParts.push(formatDrivetrain(variant.drivetrain));

  return (
    <div>
      <button
        onClick={() => setOpen(!open)}
        className="w-full flex items-center justify-between p-3 cursor-pointer bg-transparent border-none text-left"
        style={{
          borderRadius: "var(--radius-sm)",
          backgroundColor: "var(--color-bg-secondary)",
          transition: "background-color 200ms ease",
        }}
        onMouseEnter={(e) => {
          e.currentTarget.style.backgroundColor = "var(--color-bg-tertiary)";
        }}
        onMouseLeave={(e) => {
          e.currentTarget.style.backgroundColor = "var(--color-bg-secondary)";
        }}
      >
        <div>
          <div className="text-[13px] font-medium" style={{ color: "var(--color-text-primary)" }}>
            {primaryEngine
              ? `${primaryEngine.displacementCc ? (primaryEngine.displacementCc / 1000).toFixed(1) + "L " : ""}${primaryEngine.fuelType === "ELECTRIC" ? "Electric" : primaryEngine.name} ${formatDrivetrain(variant.drivetrain)}`
              : `Variant · ${formatDrivetrain(variant.drivetrain)}`}
          </div>
          <div className="text-[11px]" style={{ color: "var(--color-text-secondary)" }}>
            {summaryParts.join(" · ")}
          </div>
        </div>
        <span
          className="text-lg transition-transform duration-200"
          style={{
            color: "var(--color-text-tertiary)",
            transform: open ? "rotate(90deg)" : "rotate(0deg)",
          }}
        >
          ›
        </span>
      </button>

      {open && (
        <div className="px-3 pb-3 mt-2">
          {engines?.map((engine) => (
            <div key={engine.id} className="mb-3">
              <h4 className="text-[12px] font-medium mb-1" style={{ color: "var(--color-text-secondary)" }}>
                {engine.fuelType === "ELECTRIC" ? "Electric Motor" : "Engine"}: {engine.code || engine.name}
              </h4>
              <SpecsTable
                rows={[
                  { label: "Fuel type", value: engine.fuelType.replace(/_/g, " ") },
                  { label: "Displacement", value: engine.displacementCc ? `${engine.displacementCc} cc` : null },
                  { label: "Power", value: `${engine.powerKw} kW (${engine.powerHp} hp)` },
                  { label: "Torque", value: engine.torqueNm ? `${engine.torqueNm} Nm` : null },
                  { label: "Cylinders", value: engine.cylinderCount },
                  { label: "Configuration", value: engine.configuration },
                ]}
              />
            </div>
          ))}

          {transmission && (
            <div className="mb-3">
              <h4 className="text-[12px] font-medium mb-1" style={{ color: "var(--color-text-secondary)" }}>
                Transmission
              </h4>
              <SpecsTable
                rows={[
                  { label: "Type", value: transmission.type.replace(/_/g, " ") },
                  { label: "Gears", value: transmission.gearCount },
                ]}
              />
            </div>
          )}

          <SpecsTable
            rows={[
              { label: "Drivetrain", value: formatDrivetrain(variant.drivetrain) },
              { label: "Curb weight", value: `${variant.curbWeightKg} kg` },
              { label: "Ground clearance", value: variant.groundClearanceMm ? `${variant.groundClearanceMm} mm` : null },
              { label: "System power", value: variant.systemPowerKw ? `${variant.systemPowerKw} kW` : null },
              { label: "Markets", value: variant.markets.length > 0 ? variant.markets.join(", ") : null },
            ]}
          />
        </div>
      )}
    </div>
  );
}
