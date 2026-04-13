package live.yurii.autocatalog.api.generation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import live.yurii.autocatalog.application.generation.GenerationService;
import live.yurii.autocatalog.domain.generation.GenerationId;
import live.yurii.autocatalog.domain.model.ModelId;
import live.yurii.autocatalog.domain.shared.YearRange;
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

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/generations")
@Tag(name = "Generations", description = "Car generations")
public class GenerationController {

  private final GenerationService generationService;

  public GenerationController(GenerationService generationService) {
    this.generationService = generationService;
  }

  @GetMapping
  @Operation(summary = "Get generations by model")
  public List<GenerationResponse> getByModel(@RequestParam UUID modelId) {
    return generationService.getByModel(new ModelId(modelId)).stream()
      .map(GenerationResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get generation by ID")
  public GenerationResponse getById(@PathVariable UUID id) {
    return GenerationResponse.from(generationService.getById(new GenerationId(id)));
  }

  @PostMapping
  @Operation(summary = "Create a new generation")
  public ResponseEntity<GenerationResponse> create(@Valid @RequestBody GenerationRequest request) {
    var generation = generationService.create(
      new ModelId(request.modelId()),
      request.name(),
      new YearRange(request.yearFrom(), request.yearTo())
    );
    var uri = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}").buildAndExpand(generation.id().value()).toUri();
    return ResponseEntity.created(uri).body(GenerationResponse.from(generation));
  }

  @PatchMapping("/{id}/close")
  @Operation(summary = "Close generation (set end year)")
  public GenerationResponse close(@PathVariable UUID id, @RequestParam int year) {
    return GenerationResponse.from(generationService.close(new GenerationId(id), year));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  @Operation(summary = "Delete a generation")
  public void deleteById(@PathVariable UUID id) {
    generationService.deleteById(new GenerationId(id));
  }

  @PatchMapping("/{id}/name")
  @Operation(summary = "Rename a generation")
  public GenerationResponse rename(@PathVariable UUID id, @Valid @RequestBody GenerationRequest request) {
    return GenerationResponse.from(generationService.rename(new GenerationId(id), request.name()));
  }
}
