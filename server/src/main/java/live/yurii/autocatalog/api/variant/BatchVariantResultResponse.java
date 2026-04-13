package live.yurii.autocatalog.api.variant;

/**
 * Represents the outcome of a single item in a batch variant creation request.
 *
 * <p>On success, {@code variant} is populated and {@code error} is null.
 * On failure, {@code error} is populated and {@code variant} is null.
 * The {@code index} field matches the zero-based position of the request in the
 * original list, allowing callers to correlate results with their input.
 */
public record BatchVariantResultResponse(
  int index,
  String status,
  VariantResponse variant,
  String error
) {

  public static BatchVariantResultResponse success(int index, VariantResponse variant) {
    return new BatchVariantResultResponse(index, "SUCCESS", variant, null);
  }

  public static BatchVariantResultResponse failure(int index, String error) {
    return new BatchVariantResultResponse(index, "FAILURE", null, error);
  }
}
