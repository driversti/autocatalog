import type { ReactNode } from "react";

interface CardProps {
  image?: ReactNode;
  children: ReactNode;
  onClick?: () => void;
}

export default function Card({ image, children, onClick }: CardProps) {
  return (
    <div
      onClick={onClick}
      className="overflow-hidden cursor-pointer"
      style={{
        borderRadius: "var(--radius-md)",
        border: "0.5px solid var(--color-border)",
        backgroundColor: "var(--color-surface)",
        boxShadow: "var(--shadow-card)",
        transition: "box-shadow 200ms ease, transform 200ms ease",
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = "translateY(-2px)";
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = "translateY(0)";
      }}
    >
      {image && (
        <div
          className="w-full"
          style={{
            aspectRatio: "16 / 9",
            backgroundColor: "var(--color-bg-secondary)",
            overflow: "hidden",
          }}
        >
          {image}
        </div>
      )}
      <div className="p-4">{children}</div>
    </div>
  );
}
