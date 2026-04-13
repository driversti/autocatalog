package live.yurii.autocatalog.api.fuelcellstack;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import live.yurii.autocatalog.application.fuelcellstack.FuelCellStackService;
import live.yurii.autocatalog.domain.fuelcellstack.FuelCellStackId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/v1/fuel-cell-stacks")
@Tag(name = "Fuel Cell Stacks", description = "Fuel cell stack components for FCEV powertrains")
public class FuelCellStackController {

  private final FuelCellStackService stackService;

  public FuelCellStackController(FuelCellStackService stackService) {
    this.stackService = stackService;
  }

  @GetMapping
  @Operation(summary = "Get all fuel cell stacks")
  public List<FuelCellStackResponse> getAll() {
    return stackService.getAll().stream().map(FuelCellStackResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get fuel cell stack by ID")
  public FuelCellStackResponse getById(@PathVariable Long id) {
    return FuelCellStackResponse.from(stackService.getById(new FuelCellStackId(id)));
  }

  @PostMapping
  @Operation(summary = "Create a new fuel cell stack")
  public ResponseEntity<FuelCellStackResponse> create(@Valid @RequestBody FuelCellStackRequest request) {
    var stack = stackService.create(request.label(), request.peakPowerKw(), request.hydrogenTankKg());
    var uri = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}").buildAndExpand(stack.id().value()).toUri();
    return ResponseEntity.created(uri).body(FuelCellStackResponse.from(stack));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  @Operation(summary = "Delete a fuel cell stack (409 if referenced by a powertrain)")
  public void deleteById(@PathVariable Long id) {
    stackService.deleteById(new FuelCellStackId(id));
  }
}
