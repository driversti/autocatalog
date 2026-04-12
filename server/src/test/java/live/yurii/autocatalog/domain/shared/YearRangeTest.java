package live.yurii.autocatalog.domain.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class YearRangeTest {

  @Test
  void rejects_year_before_first_car() {
    assertThatThrownBy(() -> new YearRange(1885, 1900))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("1886");
  }

  @Test
  void rejects_to_before_from() {
    assertThatThrownBy(() -> new YearRange(2000, 1999))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("'to' must be >= 'from'");
  }

  @Test
  void accepts_equal_from_and_to() {
    var range = new YearRange(2020, 2020);

    assertThat(range.from()).isEqualTo(2020);
    assertThat(range.to()).isEqualTo(2020);
  }

  @Test
  void accepts_null_to_as_open_ended() {
    var range = new YearRange(2020, null);

    assertThat(range.isCurrent()).isTrue();
  }

  @Test
  void isCurrent_false_when_to_is_set() {
    assertThat(new YearRange(2000, 2005).isCurrent()).isFalse();
  }

  @Test
  void overlaps_when_ranges_share_years() {
    var a = new YearRange(2000, 2010);
    var b = new YearRange(2005, 2015);

    assertThat(a.overlaps(b)).isTrue();
    assertThat(b.overlaps(a)).isTrue();
  }

  @Test
  void overlaps_when_ranges_touch_at_boundary() {
    var a = new YearRange(2000, 2005);
    var b = new YearRange(2005, 2010);

    assertThat(a.overlaps(b)).isTrue();
  }

  @Test
  void does_not_overlap_when_disjoint() {
    var a = new YearRange(1990, 1995);
    var b = new YearRange(2000, 2005);

    assertThat(a.overlaps(b)).isFalse();
    assertThat(b.overlaps(a)).isFalse();
  }

  @Test
  void open_ended_range_overlaps_any_later_range() {
    var current = new YearRange(2015, null);
    var past = new YearRange(2018, 2020);

    assertThat(current.overlaps(past)).isTrue();
  }

  @Test
  void two_open_ended_ranges_always_overlap() {
    var a = new YearRange(2000, null);
    var b = new YearRange(2020, null);

    assertThat(a.overlaps(b)).isTrue();
  }
}
