import { Routes, Route } from "react-router";
import Layout from "./components/layout/Layout";
import HomePage from "./pages/HomePage";
import MakePage from "./pages/MakePage";

function Placeholder({ name }: { name: string }) {
  return (
    <div className="flex items-center justify-center py-32">
      <h1 className="text-[28px] font-semibold" style={{ color: "var(--color-text-primary)" }}>
        {name}
      </h1>
    </div>
  );
}

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/search" element={<Placeholder name="Search" />} />
        <Route path="/:makeSlug" element={<MakePage />} />
        <Route path="/:makeSlug/:modelSlug" element={<Placeholder name="Model" />} />
        <Route path="*" element={<Placeholder name="404 — Not Found" />} />
      </Route>
    </Routes>
  );
}
