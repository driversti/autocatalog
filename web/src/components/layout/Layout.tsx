import { Outlet } from "react-router";
import Header from "./Header";
import Footer from "./Footer";

export default function Layout() {
  return (
    <div className="flex flex-col min-h-screen" style={{ backgroundColor: "var(--color-bg)" }}>
      <Header />
      <main className="mx-auto max-w-6xl px-6 py-8 flex-1 w-full">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
