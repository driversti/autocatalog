package live.yurii.autocatalog.api.make;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import live.yurii.autocatalog.application.make.MakeService;
import live.yurii.autocatalog.domain.make.MakeId;
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
@RequestMapping("/api/v1/makes")
@Tag(name = "Makes", description = "Car manufacturers")
public class MakeController {

  private final MakeService makeService;

  public MakeController(MakeService makeService) {
    this.makeService = makeService;
  }

  @GetMapping
  @Operation(summary = "Get all makes")
  public List<MakeResponse> getAll() {
    return makeService.getAll().stream().map(MakeResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get make by ID")
  public MakeResponse getById(@PathVariable UUID id) {
    return MakeResponse.from(makeService.getById(MakeId.of(id.toString())));
  }

  @GetMapping("/slug/{slug}")
  @Operation(summary = "Get make by slug")
  public MakeResponse getBySlug(@PathVariable String slug) {
    return MakeResponse.from(makeService.getBySlug(slug));
  }

  @PostMapping
  @Operation(summary = "Create a new make")
  public ResponseEntity<MakeResponse> create(@Valid @RequestBody MakeRequest request) {
    var make = makeService.create(request.name(), request.country());
    var uri = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}").buildAndExpand(make.id().value()).toUri();
    return ResponseEntity.created(uri).body(MakeResponse.from(make));
  }

  @PatchMapping("/{id}/name")
  @Operation(summary = "Rename a make")
  public MakeResponse rename(@PathVariable UUID id, @Valid @RequestBody MakeRequest request) {
    return MakeResponse.from(makeService.rename(MakeId.of(id.toString()), request.name()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  @Operation(summary = "Delete a make")
  public void deleteById(@PathVariable UUID id) {
    makeService.deleteById(MakeId.of(id.toString()));
  }

  @PatchMapping("/{id}/country")
  @Operation(summary = "Update the country of a make")
  public MakeResponse updateCountry(@PathVariable UUID id, @Valid @RequestBody MakeRequest request) {
    return MakeResponse.from(makeService.updateCountry(MakeId.of(id.toString()), request.country()));
  }
}
