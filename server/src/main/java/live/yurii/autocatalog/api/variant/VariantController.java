package live.yurii.autocatalog.api.variant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import live.yurii.autocatalog.application.variant.VariantBatchService;
import live.yurii.autocatalog.application.variant.VariantService;
import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.variant.VariantId;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

@Validated
@RestController
@RequestMapping("/api/v1/variants")
@Tag(name = "Variants", description = "Car variants (technical configurations)")
public class VariantController {

  private final VariantService variantService;
  private final VariantBatchService variantBatchService;

  public VariantController(VariantService variantService, VariantBatchService variantBatchService) {
    this.variantService = variantService;
    this.variantBatchService = variantBatchService;
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
      new PowertrainId(request.powertrainId()),
      new TransmissionId(request.transmissionId()),
      request.drivetrain(),
      request.curbWeightKg(),
      request.groundClearanceMm()
    );
    var uri = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}").buildAndExpand(variant.id().value()).toUri();
    return ResponseEntity.created(uri).body(VariantResponse.from(variant));
  }

  @PostMapping("/batch")
  @Operation(summary = "Create multiple variants in a single request",
    description = "Each item is processed independently. A failure in one item does not affect others. "
      + "Returns one result per input item, each with status SUCCESS or FAILURE.")
  public List<BatchVariantResultResponse> createBatch(
    @NotEmpty @RequestBody List<@Valid VariantRequest> requests
  ) {
    var commands = requests.stream()
      .map(r -> new VariantBatchService.CreateVariantCommand(
        new BodyId(r.bodyId()),
        new PowertrainId(r.powertrainId()),
        new TransmissionId(r.transmissionId()),
        r.drivetrain(),
        r.curbWeightKg(),
        r.groundClearanceMm()
      ))
      .toList();

    return variantBatchService.createAll(commands).stream()
      .map(result -> result.succeeded()
        ? BatchVariantResultResponse.success(result.index(), VariantResponse.from(result.variant()))
        : BatchVariantResultResponse.failure(result.index(), result.error()))
      .toList();
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

  @DeleteMapping("/{id}/markets/{market}")
  @ResponseStatus(NO_CONTENT)
  @Operation(summary = "Remove a market from a variant")
  public void removeMarket(@PathVariable UUID id, @PathVariable String market) {
    variantService.removeMarket(new VariantId(id), market);
  }

  @PatchMapping("/{id}/weight")
  @Operation(summary = "Update curb weight")
  public VariantResponse updateCurbWeight(@PathVariable UUID id, @Valid @RequestBody VariantRequest request) {
    return VariantResponse.from(variantService.updateCurbWeightKg(new VariantId(id), request.curbWeightKg()));
  }

  @PatchMapping("/{id}/ground-clearance")
  @Operation(summary = "Update ground clearance")
  public VariantResponse updateGroundClearance(@PathVariable UUID id, @Valid @RequestBody VariantRequest request) {
    return VariantResponse.from(variantService.updateGroundClearanceMm(new VariantId(id), request.groundClearanceMm()));
  }
}
