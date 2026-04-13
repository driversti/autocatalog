package live.yurii.autocatalog.api.powertrain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import live.yurii.autocatalog.application.powertrain.PowertrainService;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.powerunit.PowerUnitId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/powertrains")
@Tag(name = "Powertrains", description = "Powertrain configurations (1..N power units working together)")
public class PowertrainController {

  private final PowertrainService powertrainService;

  public PowertrainController(PowertrainService powertrainService) {
    this.powertrainService = powertrainService;
  }

  @GetMapping
  @Operation(summary = "Get all powertrains")
  public List<PowertrainResponse> getAll() {
    return powertrainService.getAll().stream().map(PowertrainResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get powertrain by ID")
  public PowertrainResponse getById(@PathVariable UUID id) {
    return PowertrainResponse.from(powertrainService.getById(new PowertrainId(id)));
  }

  @PostMapping
  @Operation(summary = "Create a new powertrain")
  public ResponseEntity<PowertrainResponse> create(@Valid @RequestBody PowertrainRequest request) {
    var unitEntries = request.units().stream()
      .map(e -> new PowertrainService.UnitEntry(new PowerUnitId(e.powerUnitId()), e.role()))
      .toList();

    var powertrain = powertrainService.create(
      request.name(),
      request.drivetrainType(),
      request.combinedPowerHp(),
      request.combinedTorqueNm(),
      request.batteryCapacityKwh(),
      request.electricRangeKm(),
      unitEntries
    );

    var uri = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}").buildAndExpand(powertrain.id().value()).toUri();
    return ResponseEntity.created(uri).body(PowertrainResponse.from(powertrain));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  @Operation(summary = "Delete a powertrain")
  public void deleteById(@PathVariable UUID id) {
    powertrainService.deleteById(new PowertrainId(id));
  }

  @PatchMapping("/{id}/name")
  @Operation(summary = "Rename a powertrain")
  public PowertrainResponse rename(@PathVariable UUID id, @Valid @RequestBody PowertrainRequest request) {
    return PowertrainResponse.from(powertrainService.rename(new PowertrainId(id), request.name()));
  }
}
