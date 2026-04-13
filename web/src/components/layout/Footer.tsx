import { Link } from "react-router";

export default function Footer() {
  return (
    <footer
      className="mt-auto py-6 px-6 text-center"
      style={{
        borderTop: "0.5px solid var(--color-border)",
        color: "var(--color-text-tertiary)",
      }}
    >
      <div className="mx-auto max-w-6xl text-[12px] leading-relaxed">
        <p>
          All trademarks, logos, and brand names are the property of their
          respective owners. Images are used for informational purposes only.{" "}
          <Link
            to="/legal/copyright"
            className="underline"
            style={{ color: "var(--color-text-secondary)" }}
          >
            Copyright Policy →
          </Link>
        </p>
      </div>
    </footer>
  );
}
