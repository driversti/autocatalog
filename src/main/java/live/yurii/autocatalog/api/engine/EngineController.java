package live.yurii.autocatalog.api.engine;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import live.yurii.autocatalog.application.engine.EngineService;
import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.engine.FuelType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/engines")
@Tag(name = "Engines", description = "Car engines")
public class EngineController {

  private final EngineService engineService;

  public EngineController(EngineService engineService) {
    this.engineService = engineService;
  }

  @GetMapping
  @Operation(summary = "Get all engines")
  public List<EngineResponse> getAll(@RequestParam(required = false) FuelType fuelType) {
    var engines = fuelType != null
      ? engineService.getByFuelType(fuelType)
      : engineService.getAll();
    return engines.stream().map(EngineResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get engine by ID")
  public EngineResponse getById(@PathVariable UUID id) {
    return EngineResponse.from(engineService.getById(new EngineId(id)));
  }

  @PostMapping
  @Operation(summary = "Create a new engine")
  public ResponseEntity<EngineResponse> create(@Valid @RequestBody EngineRequest request) {
    var engine = engineService.create(
      request.code(), request.name(), request.fuelType(),
      request.displacementCc(), request.powerKw(), request.torqueNm(),
      request.cylinderCount(), request.configuration(), request.systemPowerKw()
    );
    var uri = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}").buildAndExpand(engine.id().value()).toUri();
    return ResponseEntity.created(uri).body(EngineResponse.from(engine));
  }
}
