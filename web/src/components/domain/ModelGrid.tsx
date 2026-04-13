import { useNavigate } from "react-router";
import Card from "../ui/Card";
import type { CarModelResponse } from "../../types/api";

interface ModelGridProps {
  models: CarModelResponse[];
  makeSlug: string;
}

export default function ModelGrid({ models, makeSlug }: ModelGridProps) {
  const navigate = useNavigate();

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      {models.map((model) => (
        <Card
          key={model.id}
          onClick={() => navigate(`/${makeSlug}/${model.slug}`)}
          image={
            <div
              className="w-full h-full flex items-center justify-center text-[13px]"
              style={{ color: "var(--color-text-tertiary)" }}
            >
              {model.name}
            </div>
          }
        >
          <h3 className="text-[14px] font-medium" style={{ color: "var(--color-text-primary)" }}>
            {model.name}
          </h3>
        </Card>
      ))}
    </div>
  );
}
