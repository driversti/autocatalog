import { Link } from "react-router";

export default function NotFoundPage() {
  return (
    <div className="flex flex-col items-center justify-center py-32 gap-4">
      <h1 className="text-[40px] font-semibold" style={{ color: "var(--color-text-primary)" }}>
        404
      </h1>
      <p className="text-[16px]" style={{ color: "var(--color-text-secondary)" }}>
        Page not found.
      </p>
      <Link
        to="/"
        className="text-[14px] font-medium"
        style={{ color: "var(--color-accent)" }}
      >
        Back to Home
      </Link>
    </div>
  );
}
