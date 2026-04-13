package live.yurii.autocatalog.api.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import live.yurii.autocatalog.application.model.CarModelService;
import live.yurii.autocatalog.domain.make.MakeId;
import live.yurii.autocatalog.domain.model.ModelId;
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
@RequestMapping("/api/v1/models")
@Tag(name = "Models", description = "Car models")
public class CarModelController {

  private final CarModelService carModelService;

  public CarModelController(CarModelService carModelService) {
    this.carModelService = carModelService;
  }

  @GetMapping
  @Operation(summary = "Get models by make")
  public List<CarModelResponse> getByMake(@RequestParam UUID makeId) {
    return carModelService.getByMake(new MakeId(makeId)).stream()
      .map(CarModelResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get model by ID")
  public CarModelResponse getById(@PathVariable UUID id) {
    return CarModelResponse.from(carModelService.getById(new ModelId(id)));
  }

  @PostMapping
  @Operation(summary = "Create a new model")
  public ResponseEntity<CarModelResponse> create(@Valid @RequestBody CarModelRequest request) {
    var model = carModelService.create(new MakeId(request.makeId()), request.name());
    var uri = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}").buildAndExpand(model.id().value()).toUri();
    return ResponseEntity.created(uri).body(CarModelResponse.from(model));
  }

  @PostMapping("/{id}/relations")
  @Operation(summary = "Link related model")
  public CarModelResponse addRelation(@PathVariable UUID id,
                                      @Valid @RequestBody ModelRelationRequest request) {
    return CarModelResponse.from(carModelService.linkRelation(
      new ModelId(id), new ModelId(request.targetModelId()), request.type(), request.note()
    ));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  @Operation(summary = "Delete a car model")
  public void deleteById(@PathVariable UUID id) {
    carModelService.deleteById(new ModelId(id));
  }

  @PatchMapping("/{id}/name")
  @Operation(summary = "Rename a car model")
  public CarModelResponse rename(@PathVariable UUID id, @Valid @RequestBody CarModelRequest request) {
    return CarModelResponse.from(carModelService.rename(new ModelId(id), request.name()));
  }
}
