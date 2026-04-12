import { Outlet } from "react-router";
import Header from "./Header";

export default function Layout() {
  return (
    <div className="min-h-screen" style={{ backgroundColor: "var(--color-bg)" }}>
      <Header />
      <main className="mx-auto max-w-6xl px-6 py-8">
        <Outlet />
      </main>
    </div>
  );
}
