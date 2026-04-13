package live.yurii.autocatalog.api.body;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import live.yurii.autocatalog.application.body.BodyService;
import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.generation.GenerationId;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/v1/bodies")
@Tag(name = "Bodies", description = "Car body configurations")
public class BodyController {

  private final BodyService bodyService;

  public BodyController(BodyService bodyService) {
    this.bodyService = bodyService;
  }

  @GetMapping
  @Operation(summary = "Get bodies by generation")
  public List<BodyResponse> getByGeneration(@RequestParam UUID generationId) {
    return bodyService.getByGeneration(new GenerationId(generationId)).stream()
      .map(BodyResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get body by ID")
  public BodyResponse getById(@PathVariable UUID id) {
    return BodyResponse.from(bodyService.getById(new BodyId(id)));
  }

  @PostMapping
  @Operation(summary = "Create a new body configuration")
  public ResponseEntity<BodyResponse> create(@Valid @RequestBody BodyRequest request) {
    var body = bodyService.create(
      new GenerationId(request.generationId()),
      request.bodyStyle(),
      request.lengthMm(),
      request.widthMm(),
      request.heightMm(),
      request.wheelbaseMm(),
      request.trunkVolumeLitres(),
      request.groundClearanceMm()
    );
    var uri = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}").buildAndExpand(body.id().value()).toUri();
    return ResponseEntity.created(uri).body(BodyResponse.from(body));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  @Operation(summary = "Delete a body configuration")
  public void deleteById(@PathVariable UUID id) {
    bodyService.deleteById(new BodyId(id));
  }

  @PatchMapping("/{id}/dimensions")
  @Operation(summary = "Update body dimensions")
  public BodyResponse updateDimensions(@PathVariable UUID id, @Valid @RequestBody BodyRequest request) {
    return BodyResponse.from(bodyService.updateDimensions(
      new BodyId(id),
      request.lengthMm(), request.widthMm(), request.heightMm(), request.wheelbaseMm(),
      request.trunkVolumeLitres(), request.groundClearanceMm()
    ));
  }
}
