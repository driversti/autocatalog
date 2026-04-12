package live.yurii.autocatalog.domain.shared;

public record YearRange(int from, Integer to) {

  public YearRange {
    if (from < 1886) throw new IllegalArgumentException("No cars before 1886");
    if (to != null && to < from) throw new IllegalArgumentException("'to' must be >= 'from'");
  }

  public boolean isCurrent() {
    return to == null;
  }

  public boolean overlaps(YearRange other) {
    int otherTo = other.to() != null ? other.to() : Integer.MAX_VALUE;
    int thisTo = this.to != null ? this.to : Integer.MAX_VALUE;
    return this.from <= otherTo && other.from() <= thisTo;
  }
}
