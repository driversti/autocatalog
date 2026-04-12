import { Component, type ReactNode } from "react";
import { Routes, Route } from "react-router";
import Layout from "./components/layout/Layout";
import HomePage from "./pages/HomePage";
import MakePage from "./pages/MakePage";
import ModelPage from "./pages/ModelPage";
import SearchPage from "./pages/SearchPage";
import CopyrightPage from "./pages/legal/CopyrightPage";
import NotFoundPage from "./pages/NotFoundPage";

class ErrorBoundary extends Component<
  { children: ReactNode },
  { hasError: boolean }
> {
  constructor(props: { children: ReactNode }) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  render() {
    if (this.state.hasError) {
      return (
        <div
          className="flex flex-col items-center justify-center min-h-screen gap-4"
          style={{ backgroundColor: "var(--color-bg)" }}
        >
          <h1
            className="text-[28px] font-semibold"
            style={{ color: "var(--color-text-primary)" }}
          >
            Something went wrong
          </h1>
          <button
            onClick={() => {
              this.setState({ hasError: false });
              window.location.href = "/";
            }}
            className="px-4 py-2 rounded-[var(--radius-sm)] text-[14px] font-medium cursor-pointer border-none"
            style={{
              backgroundColor: "var(--color-accent)",
              color: "#ffffff",
            }}
          >
            Back to Home
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}

export default function App() {
  return (
    <ErrorBoundary>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/search" element={<SearchPage />} />
          <Route path="/legal/copyright" element={<CopyrightPage />} />
          <Route path="/:makeSlug" element={<MakePage />} />
          <Route path="/:makeSlug/:modelSlug" element={<ModelPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </ErrorBoundary>
  );
}
