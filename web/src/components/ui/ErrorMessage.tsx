interface ErrorMessageProps {
  message: string;
  onRetry?: () => void;
}

export default function ErrorMessage({ message, onRetry }: ErrorMessageProps) {
  return (
    <div className="flex flex-col items-center justify-center py-16 gap-4">
      <p className="text-[16px]" style={{ color: "var(--color-text-secondary)" }}>
        {message}
      </p>
      {onRetry && (
        <button
          onClick={onRetry}
          className="px-4 py-2 rounded-[var(--radius-sm)] text-[14px] font-medium cursor-pointer border-none"
          style={{
            backgroundColor: "var(--color-accent)",
            color: "#ffffff",
            transition: "background-color 200ms ease",
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.backgroundColor = "var(--color-accent-hover)";
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.backgroundColor = "var(--color-accent)";
          }}
        >
          Try again
        </button>
      )}
    </div>
  );
}
