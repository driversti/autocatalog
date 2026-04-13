package live.yurii.autocatalog.api.transmission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import live.yurii.autocatalog.application.transmission.TransmissionService;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.transmission.TransmissionType;
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

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transmissions")
@Tag(name = "Transmissions", description = "Gearboxes")
public class TransmissionController {

  private final TransmissionService transmissionService;

  public TransmissionController(TransmissionService transmissionService) {
    this.transmissionService = transmissionService;
  }

  @GetMapping
  @Operation(summary = "Get all transmissions")
  public List<TransmissionResponse> getAll(@RequestParam(required = false) TransmissionType type) {
    var list = type != null
      ? transmissionService.getByType(type)
      : transmissionService.getAll();
    return list.stream().map(TransmissionResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get transmission by ID")
  public TransmissionResponse getById(@PathVariable UUID id) {
    return TransmissionResponse.from(transmissionService.getById(new TransmissionId(id)));
  }

  @PostMapping
  @Operation(summary = "Create a new transmission")
  public ResponseEntity<TransmissionResponse> create(@Valid @RequestBody TransmissionRequest request) {
    var transmission = transmissionService.create(request.type(), request.gearCount());
    var uri = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}").buildAndExpand(transmission.id().value()).toUri();
    return ResponseEntity.created(uri).body(TransmissionResponse.from(transmission));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  @Operation(summary = "Delete a transmission")
  public void deleteById(@PathVariable UUID id) {
    transmissionService.deleteById(new TransmissionId(id));
  }
}
