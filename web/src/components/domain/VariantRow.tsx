import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { fetchEngineById, fetchPowertrainById, fetchTransmissionById } from "../../api/client";
import SpecsTable from "../ui/SpecsTable";
import type { VariantResponse, PowertrainResponse, EngineResponse } from "../../types/api";

function formatDrivetrain(d: string): string {
  return d === "FOUR_WD" ? "4WD" : d;
}

function formatTransmissionType(type: string): string {
  switch (type) {
    case "MANUAL": return "MT";
    case "AUTOMATIC": return "AT";
    case "DCT": return "DCT";
    case "CVT": return "CVT";
    default: return "1-speed";
  }
}

function buildHeaderLabel(
  powertrain: PowertrainResponse | undefined,
  primaryEngine: EngineResponse | undefined,
  drivetrain: string,
): string {
  if (!powertrain) return `Variant · ${formatDrivetrain(drivetrain)}`;

  const isHybrid = powertrain.type === "HYBRID" || powertrain.type === "PHEV" || powertrain.type === "MHEV";
  const isElectric = powertrain.type === "ELECTRIC";

  if (isElectric) {
    const power = powertrain.totalPowerKw ? ` · ${powertrain.totalPowerKw}kW` : "";
    return `${powertrain.name} ${formatDrivetrain(drivetrain)}${power}`;
  }

  if (isHybrid) {
    const power = powertrain.totalPowerKw ? ` · ${powertrain.totalPowerKw}kW` : "";
    const hybridSuffix = powertrain.name.toLowerCase().includes("hybrid") ? "" : " Hybrid";
    return `${powertrain.name}${hybridSuffix} · ${formatDrivetrain(drivetrain)}${power}`;
  }

  // ICE — show displacement from primary engine
  if (primaryEngine) {
    const displacement = primaryEngine.displacementCc
      ? (primaryEngine.displacementCc / 1000).toFixed(1) + "L "
      : "";
    return `${displacement}${powertrain.name} ${formatDrivetrain(drivetrain)}`;
  }

  return `${powertrain.name} ${formatDrivetrain(drivetrain)}`;
}

export default function VariantRow({ variant }: { variant: VariantResponse }) {
  const [open, setOpen] = useState(false);

  const { data: powertrain } = useQuery({
    queryKey: ["powertrains", variant.powertrainId],
    queryFn: () => fetchPowertrainById(variant.powertrainId),
    staleTime: 5 * 60 * 1000,
  });

  const engineIds = powertrain?.engines.map((e) => e.engineId) ?? [];

  const { data: engines } = useQuery({
    queryKey: ["engines", "batch", engineIds],
    queryFn: () => Promise.all(engineIds.map(fetchEngineById)),
    enabled: engineIds.length > 0,
    staleTime: 5 * 60 * 1000,
  });

  const { data: transmission } = useQuery({
    queryKey: ["transmissions", variant.transmissionId],
    queryFn: () => fetchTransmissionById(variant.transmissionId),
    enabled: open,
    staleTime: 5 * 60 * 1000,
  });

  const primaryEngine = engines?.[0];

  const headerLabel = buildHeaderLabel(powertrain, primaryEngine, variant.drivetrain);

  // Build subtitle parts
  const summaryParts: string[] = [];
  if (powertrain) {
    summaryParts.push(powertrain.name);
    if (powertrain.totalPowerKw) summaryParts.push(`${powertrain.totalPowerKw}kW`);
  }
  if (transmission) {
    summaryParts.push(`${transmission.gearCount}-speed ${formatTransmissionType(transmission.type)}`);
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
            {headerLabel}
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
          {powertrain && (
            <div className="mb-3">
              <h4 className="text-[12px] font-medium mb-1" style={{ color: "var(--color-text-secondary)" }}>
                Powertrain: {powertrain.name}
              </h4>
              <SpecsTable
                rows={[
                  { label: "Type", value: powertrain.type.replace(/_/g, " ") },
                  { label: "Total power", value: powertrain.totalPowerKw ? `${powertrain.totalPowerKw} kW` : null },
                  { label: "Total torque", value: powertrain.totalTorqueNm ? `${powertrain.totalTorqueNm} Nm` : null },
                ]}
              />
            </div>
          )}

          {engines?.map((engine) => {
            const role = powertrain?.engines.find((e) => e.engineId === engine.id)?.role;
            return (
              <div key={engine.id} className="mb-3">
                <h4 className="text-[12px] font-medium mb-1" style={{ color: "var(--color-text-secondary)" }}>
                  {engine.fuelType === "ELECTRIC" ? "Electric Motor" : "Engine"}: {engine.code || engine.name}
                  {role === "SECONDARY_ELECTRIC" && " (secondary)"}
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
            );
          })}

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
              { label: "Markets", value: variant.markets.length > 0 ? variant.markets.join(", ") : null },
            ]}
          />
        </div>
      )}
    </div>
  );
}
