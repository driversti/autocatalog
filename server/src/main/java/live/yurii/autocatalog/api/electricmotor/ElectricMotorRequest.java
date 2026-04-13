package live.yurii.autocatalog.api.electricmotor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import live.yurii.autocatalog.domain.electricmotor.MotorType;

public record ElectricMotorRequest(
  @NotBlank @Size(max = 100) String label,
  @Min(1) Integer powerKw,
  @Min(1) Integer torqueNm,
  MotorType motorType
) {
}
