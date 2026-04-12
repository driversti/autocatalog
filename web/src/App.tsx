import { Routes, Route } from "react-router";
import Layout from "./components/layout/Layout";
import HomePage from "./pages/HomePage";
import MakePage from "./pages/MakePage";
import ModelPage from "./pages/ModelPage";
import NotFoundPage from "./pages/NotFoundPage";

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
        <Route path="/:makeSlug/:modelSlug" element={<ModelPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
