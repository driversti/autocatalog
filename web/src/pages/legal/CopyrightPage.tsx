export default function CopyrightPage() {
  return (
    <div className="max-w-3xl mx-auto">
      <h1
        className="text-[28px] font-semibold mb-8"
        style={{ color: "var(--color-text-primary)" }}
      >
        Copyright Policy
      </h1>

      <section className="mb-8">
        <h2
          className="text-[18px] font-semibold mb-3"
          style={{ color: "var(--color-text-primary)" }}
        >
          Trademarks
        </h2>
        <p className="text-[14px] leading-relaxed" style={{ color: "var(--color-text-secondary)" }}>
          All brand names, trademarks, logos, and trade dress mentioned on this
          site are the property of their respective owners. Their use here is
          for identification and informational purposes only and does not imply
          endorsement or affiliation.
        </p>
      </section>

      <section className="mb-8">
        <h2
          className="text-[18px] font-semibold mb-3"
          style={{ color: "var(--color-text-primary)" }}
        >
          Images
        </h2>
        <p className="text-[14px] leading-relaxed" style={{ color: "var(--color-text-secondary)" }}>
          Images displayed on AutoCatalog are sourced from Wikimedia Commons,
          manufacturer press kits, and other licensed sources. Each image
          includes attribution information identifying the author, license, and
          source where available.
        </p>
      </section>

      <section className="mb-8">
        <h2
          className="text-[18px] font-semibold mb-3"
          style={{ color: "var(--color-text-primary)" }}
        >
          DMCA / Takedown Requests
        </h2>
        <p className="text-[14px] leading-relaxed" style={{ color: "var(--color-text-secondary)" }}>
          If you believe that content on this site infringes your copyright,
          please contact us at{" "}
          <a
            href="mailto:copyright@autocatalog.com"
            className="underline"
            style={{ color: "var(--color-accent)" }}
          >
            copyright@autocatalog.com
          </a>
          . We will respond within 48 hours and remove any infringing content
          promptly.
        </p>
      </section>

      <section className="mb-8">
        <h2
          className="text-[18px] font-semibold mb-3"
          style={{ color: "var(--color-text-primary)" }}
        >
          Licenses Used
        </h2>
        <ul
          className="text-[14px] leading-relaxed list-disc pl-5 space-y-1"
          style={{ color: "var(--color-text-secondary)" }}
        >
          <li>CC0 (Public Domain)</li>
          <li>CC BY 4.0 (Creative Commons Attribution)</li>
          <li>CC BY-SA 4.0 (Creative Commons Attribution-ShareAlike)</li>
          <li>Press Kit (manufacturer-provided media)</li>
        </ul>
      </section>
    </div>
  );
}
