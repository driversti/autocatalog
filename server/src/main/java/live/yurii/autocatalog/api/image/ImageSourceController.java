package live.yurii.autocatalog.api.image;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import live.yurii.autocatalog.infrastructure.image.ImageSourceService;
import live.yurii.autocatalog.infrastructure.persistence.image.ImageSourceJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    return imageSourceService.findAll(PageRequest.of(page, size))
      .map(ImageSourceResponse::from);
  }

  @GetMapping("/by-url")
  @Operation(summary = "Get image source by image URL")
  public ImageSourceResponse getByUrl(@RequestParam String imageUrl) {
    return ImageSourceResponse.from(imageSourceService.getByImageUrl(imageUrl));
  }

  // TODO: restrict to ADMIN role when Spring Security is added
  @PostMapping
  @Operation(summary = "Save image source attribution")
  public ResponseEntity<ImageSourceResponse> create(@Valid @RequestBody ImageSourceRequest request) {
    var entity = new ImageSourceJpaEntity(
      request.imageUrl(), request.sourceUrl(), request.author(),
      request.license(), request.licenseUrl(), request.copyright(),
      request.usageTerms()
    );
    var saved = imageSourceService.save(entity);
    return ResponseEntity
      .created(URI.create("/api/v1/image-sources/" + saved.id()))
      .body(ImageSourceResponse.from(saved));
  }
}
