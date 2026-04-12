import { useNavigate } from "react-router";
import type { MakeResponse } from "../../types/api";

export default function MakeGrid({ makes }: { makes: MakeResponse[] }) {
  const navigate = useNavigate();

  return (
    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
      {makes.map((make) => (
        <div
          key={make.id}
          onClick={() => navigate(`/${make.slug}`)}
          className="flex flex-col items-center gap-2 p-5 cursor-pointer"
          style={{
            borderRadius: "var(--radius-md)",
            backgroundColor: "var(--color-bg-secondary)",
            transition: "background-color 200ms ease, transform 200ms ease",
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.backgroundColor = "var(--color-bg-tertiary)";
            e.currentTarget.style.transform = "translateY(-2px)";
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.backgroundColor = "var(--color-bg-secondary)";
            e.currentTarget.style.transform = "translateY(0)";
          }}
        >
          <div
            className="w-12 h-12 rounded-full flex items-center justify-center text-[18px] font-semibold"
            style={{
              backgroundColor: "var(--color-bg-tertiary)",
              color: "var(--color-text-secondary)",
            }}
          >
            {make.name.charAt(0)}
          </div>
          <div className="text-center">
            <div className="text-[14px] font-medium" style={{ color: "var(--color-text-primary)" }}>
              {make.name}
            </div>
            <div className="text-[12px]" style={{ color: "var(--color-text-secondary)" }}>
              {make.country}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
