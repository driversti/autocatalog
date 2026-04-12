import { Routes, Route } from "react-router";

function Placeholder({ name }: { name: string }) {
  return (
    <div className="flex items-center justify-center min-h-screen">
      <h1 className="text-[28px] font-semibold" style={{ color: "var(--color-text-primary)" }}>
        {name}
      </h1>
    </div>
  );
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Placeholder name="Home" />} />
      <Route path="/:makeSlug" element={<Placeholder name="Make" />} />
      <Route path="/:makeSlug/:modelSlug" element={<Placeholder name="Model" />} />
      <Route path="/search" element={<Placeholder name="Search" />} />
      <Route path="*" element={<Placeholder name="404 — Not Found" />} />
    </Routes>
  );
}
