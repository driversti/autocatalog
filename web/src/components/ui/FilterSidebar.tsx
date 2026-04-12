interface FilterGroup {
  label: string;
  paramKey: string;
  options: string[];
}

interface FilterSidebarProps {
  groups: FilterGroup[];
  selected: Record<string, Set<string>>;
  onChange: (paramKey: string, value: string, checked: boolean) => void;
}

function formatLabel(value: string): string {
  return value.charAt(0) + value.slice(1).toLowerCase().replace(/_/g, " ");
}

export default function FilterSidebar({ groups, selected, onChange }: FilterSidebarProps) {
  return (
    <aside className="w-52 shrink-0">
      <h3
        className="text-[11px] uppercase tracking-wider font-medium mb-4"
        style={{ color: "var(--color-text-secondary)" }}
      >
        Filters
      </h3>
      {groups.map((group) => (
        <div key={group.paramKey} className="mb-5">
          <h4
            className="text-[12px] font-medium mb-2"
            style={{ color: "var(--color-text-primary)" }}
          >
            {group.label}
          </h4>
          <div className="flex flex-col gap-1.5">
            {group.options.map((opt) => {
              const isChecked = selected[group.paramKey]?.has(opt) ?? false;
              return (
                <label
                  key={opt}
                  className="flex items-center gap-2 cursor-pointer text-[12px]"
                  style={{ color: "var(--color-text-secondary)" }}
                >
                  <input
                    type="checkbox"
                    checked={isChecked}
                    onChange={(e) => onChange(group.paramKey, opt, e.target.checked)}
                    className="w-3.5 h-3.5 rounded accent-[var(--color-accent)]"
                  />
                  {formatLabel(opt)}
                </label>
              );
            })}
          </div>
        </div>
      ))}
    </aside>
  );
}
