package live.yurii.autocatalog.api.electricmotor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import live.yurii.autocatalog.application.electricmotor.ElectricMotorService;
import live.yurii.autocatalog.domain.electricmotor.ElectricMotorId;
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

@RestController
@RequestMapping("/api/v1/electric-motors")
@Tag(name = "Electric Motors", description = "Electric motor components for BEV/PHEV/MHEV powertrains")
public class ElectricMotorController {

  private final ElectricMotorService motorService;

  public ElectricMotorController(ElectricMotorService motorService) {
    this.motorService = motorService;
  }

  @GetMapping
  @Operation(summary = "Get all electric motors")
  public List<ElectricMotorResponse> getAll() {
    return motorService.getAll().stream().map(ElectricMotorResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get electric motor by ID")
  public ElectricMotorResponse getById(@PathVariable Long id) {
    return ElectricMotorResponse.from(motorService.getById(new ElectricMotorId(id)));
  }

  @PostMapping
  @Operation(summary = "Create a new electric motor")
  public ResponseEntity<ElectricMotorResponse> create(@Valid @RequestBody ElectricMotorRequest request) {
    var motor = motorService.create(request.label(), request.powerKw(),
      request.torqueNm(), request.motorType());
    var uri = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}").buildAndExpand(motor.id().value()).toUri();
    return ResponseEntity.created(uri).body(ElectricMotorResponse.from(motor));
  }

  @PatchMapping("/{id}/label")
  @Operation(summary = "Update electric motor label")
  public ElectricMotorResponse updateLabel(@PathVariable Long id,
                                           @Valid @RequestBody ElectricMotorRequest request) {
    return ElectricMotorResponse.from(
      motorService.updateLabel(new ElectricMotorId(id), request.label())
    );
  }

  @PatchMapping("/{id}/specs")
  @Operation(summary = "Update electric motor specs")
  public ElectricMotorResponse updateSpecs(@PathVariable Long id,
                                           @Valid @RequestBody ElectricMotorRequest request) {
    return ElectricMotorResponse.from(
      motorService.updateSpecs(new ElectricMotorId(id), request.powerKw(),
        request.torqueNm(), request.motorType())
    );
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  @Operation(summary = "Delete an electric motor (409 if referenced by a powertrain)")
  public void deleteById(@PathVariable Long id) {
    motorService.deleteById(new ElectricMotorId(id));
  }
}
