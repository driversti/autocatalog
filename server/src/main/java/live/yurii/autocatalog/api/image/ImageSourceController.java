package live.yurii.autocatalog.api.image;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import live.yurii.autocatalog.application.image.ImageSourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/image-sources")
@Tag(name = "Image Sources", description = "Image copyright and attribution metadata")
public class ImageSourceController {

  private final ImageSourceService imageSourceService;

  public ImageSourceController(ImageSourceService imageSourceService) {
    this.imageSourceService = imageSourceService;
  }

  @GetMapping
  @Operation(summary = "Get all image sources (paginated)")
  public Page<ImageSourceResponse> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return imageSourceService.findAll(PageRequest.of(page, size));
  }

  @GetMapping("/by-url")
  @Operation(summary = "Get image source by image URL")
  public ImageSourceResponse getByUrl(@RequestParam String imageUrl) {
    return imageSourceService.getByImageUrl(imageUrl);
  }

  // TODO: restrict to ADMIN role when Spring Security is added
  @PostMapping
  @Operation(summary = "Save image source attribution")
  public ResponseEntity<ImageSourceResponse> create(@Valid @RequestBody ImageSourceRequest request) {
    var saved = imageSourceService.save(request);
    return ResponseEntity
      .created(URI.create("/api/v1/image-sources/" + saved.id()))
      .body(saved);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get image source by ID")
  public ImageSourceResponse getById(@PathVariable Long id) {
    return imageSourceService.getById(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  @Operation(summary = "Delete an image source")
  public void deleteById(@PathVariable Long id) {
    imageSourceService.deleteById(id);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Update image source metadata")
  public ImageSourceResponse update(@PathVariable Long id, @Valid @RequestBody ImageSourceRequest request) {
    return imageSourceService.update(id, request);
  }
}
