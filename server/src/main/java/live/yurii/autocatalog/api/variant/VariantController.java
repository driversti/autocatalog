package live.yurii.autocatalog.api.variant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import live.yurii.autocatalog.application.variant.VariantService;
import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.variant.VariantId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/variants")
@Tag(name = "Variants", description = "Car variants (technical configurations)")
public class VariantController {

  private final VariantService variantService;

  public VariantController(VariantService variantService) {
    this.variantService = variantService;
  }

  @GetMapping
  @Operation(summary = "Get variants by body")
  public List<VariantResponse> getByBody(@RequestParam UUID bodyId) {
    return variantService.getByBody(new BodyId(bodyId)).stream()
      .map(VariantResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get variant by ID")
  public VariantResponse getById(@PathVariable UUID id) {
    return VariantResponse.from(variantService.getById(new VariantId(id)));
  }

  @PostMapping
  @Operation(summary = "Create a new variant")
  public ResponseEntity<VariantResponse> create(@Valid @RequestBody VariantRequest request) {
    var variant = variantService.create(
      new BodyId(request.bodyId()),
      new TransmissionId(request.transmissionId()),
      request.drivetrain(),
      request.curbWeightKg(),
      request.groundClearanceMm(),
      request.systemPowerKw()
    );
    var uri = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}").buildAndExpand(variant.id().value()).toUri();
    return ResponseEntity.created(uri).body(VariantResponse.from(variant));
  }

  @PostMapping("/{id}/engines/{engineId}")
  @Operation(summary = "Add engine to variant")
  public VariantResponse addEngine(@PathVariable UUID id, @PathVariable UUID engineId) {
    return VariantResponse.from(
      variantService.addEngine(new VariantId(id), new EngineId(engineId))
    );
  }

  @PostMapping("/{id}/markets/{market}")
  @Operation(summary = "Add market to variant")
  public VariantResponse addMarket(@PathVariable UUID id, @PathVariable String market) {
    return VariantResponse.from(
      variantService.addMarket(new VariantId(id), market)
    );
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  @Operation(summary = "Delete a variant")
  public void deleteById(@PathVariable UUID id) {
    variantService.deleteById(new VariantId(id));
  }
}
