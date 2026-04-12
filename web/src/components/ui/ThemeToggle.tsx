import { useTheme } from "../../hooks/useTheme";

const icons: Record<string, string> = {
  system: "◐",
  light: "☀",
  dark: "☾",
};

const labels: Record<string, string> = {
  system: "System theme",
  light: "Light theme",
  dark: "Dark theme",
};

export default function ThemeToggle() {
  const { mode, toggle } = useTheme();

  return (
    <button
      onClick={toggle}
      aria-label={labels[mode]}
      title={labels[mode]}
      className="flex items-center justify-center w-[44px] h-[44px] rounded-full cursor-pointer text-lg"
      style={{
        color: "var(--color-text-secondary)",
        background: "transparent",
        border: "none",
        transition: "color 200ms ease",
      }}
    >
      {icons[mode]}
    </button>
  );
}
