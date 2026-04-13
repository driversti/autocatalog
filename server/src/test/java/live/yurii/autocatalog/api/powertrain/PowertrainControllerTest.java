package live.yurii.autocatalog.api.powertrain;

import live.yurii.autocatalog.api.shared.GlobalExceptionHandler;
import live.yurii.autocatalog.application.powertrain.PowertrainService;
import live.yurii.autocatalog.domain.powertrain.DrivetrainType;
import live.yurii.autocatalog.domain.powertrain.Powertrain;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.powertrain.PowertrainUnit;
import live.yurii.autocatalog.domain.powertrain.UnitRole;
import live.yurii.autocatalog.domain.powerunit.PowerUnitId;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PowertrainController.class)
@Import(GlobalExceptionHandler.class)
class PowertrainControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PowertrainService powertrainService;

  private Powertrain samplePowertrain() {
    return Powertrain.create("2.0 TDI 150", DrivetrainType.ICE, 150, 340, null, null,
      List.of(new PowertrainUnit(new PowerUnitId(1L), UnitRole.PRIMARY)));
  }

  @Test
  void getAll_returns_powertrains() throws Exception {
    var pt1 = samplePowertrain();
    var pt2 = Powertrain.create("EQA 250", DrivetrainType.BEV, 140, null, null, null,
      List.of(new PowertrainUnit(new PowerUnitId(10L), UnitRole.PRIMARY)));
    when(powertrainService.getAll()).thenReturn(List.of(pt1, pt2));

    mockMvc.perform(get("/api/v1/powertrains"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].name").value("2.0 TDI 150"))
      .andExpect(jsonPath("$[1].name").value("EQA 250"));
  }

  @Test
  void getById_returns_powertrain() throws Exception {
    var pt = samplePowertrain();
    when(powertrainService.getById(any(PowertrainId.class))).thenReturn(pt);

    mockMvc.perform(get("/api/v1/powertrains/{id}", pt.id().value()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("2.0 TDI 150"))
      .andExpect(jsonPath("$.drivetrainType").value("ICE"))
      .andExpect(jsonPath("$.combinedPowerHp").value(150))
      .andExpect(jsonPath("$.combinedTorqueNm").value(340))
      .andExpect(jsonPath("$.units.length()").value(1))
      .andExpect(jsonPath("$.units[0].role").value("PRIMARY"));
  }

  @Test
  void getById_returns_404_when_not_found() throws Exception {
    when(powertrainService.getById(any(PowertrainId.class)))
      .thenThrow(new EntityNotFoundException("Powertrain not found"));

    mockMvc.perform(get("/api/v1/powertrains/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_returns_201_with_powertrain() throws Exception {
    var pt = samplePowertrain();
    when(powertrainService.create(eq("2.0 TDI 150"), eq(DrivetrainType.ICE),
      eq(150), eq(340), any(), any(), anyList())).thenReturn(pt);

    var requestBody = """
      {
        "name": "2.0 TDI 150",
        "drivetrainType": "ICE",
        "combinedPowerHp": 150,
        "combinedTorqueNm": 340,
        "units": [{"powerUnitId": 1, "role": "PRIMARY"}]
      }
      """;

    mockMvc.perform(post("/api/v1/powertrains")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location",
        "http://localhost/api/v1/powertrains/" + pt.id().value()))
      .andExpect(jsonPath("$.name").value("2.0 TDI 150"))
      .andExpect(jsonPath("$.drivetrainType").value("ICE"))
      .andExpect(jsonPath("$.combinedPowerHp").value(150));
  }

  @Test
  void create_returns_409_when_name_already_exists() throws Exception {
    when(powertrainService.create(any(), any(), any(), any(), any(), any(), anyList()))
      .thenThrow(new IllegalStateException("Powertrain already exists with name: 2.0 TDI 150"));

    var requestBody = """
      {
        "name": "2.0 TDI 150",
        "drivetrainType": "ICE",
        "combinedPowerHp": 150,
        "units": [{"powerUnitId": 1, "role": "PRIMARY"}]
      }
      """;

    mockMvc.perform(post("/api/v1/powertrains")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isConflict());
  }

  @Test
  void create_returns_400_when_units_empty() throws Exception {
    var requestBody = """
      {
        "name": "2.0 TDI 150",
        "drivetrainType": "ICE",
        "combinedPowerHp": 150,
        "units": []
      }
      """;

    mockMvc.perform(post("/api/v1/powertrains")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isBadRequest());
  }

  @Test
  void delete_returns_204() throws Exception {
    var id = UUID.randomUUID();

    mockMvc.perform(delete("/api/v1/powertrains/{id}", id))
      .andExpect(status().isNoContent());

    verify(powertrainService).deleteById(new PowertrainId(id));
  }

  @Test
  void delete_returns_409_when_referenced_by_variants() throws Exception {
    var id = UUID.randomUUID();
    doThrow(new IllegalStateException("Cannot delete powertrain: referenced by variant(s)"))
      .when(powertrainService).deleteById(new PowertrainId(id));

    mockMvc.perform(delete("/api/v1/powertrains/{id}", id))
      .andExpect(status().isConflict());
  }

  @Test
  void delete_returns_404_when_not_found() throws Exception {
    var id = UUID.randomUUID();
    doThrow(new EntityNotFoundException("Powertrain not found"))
      .when(powertrainService).deleteById(new PowertrainId(id));

    mockMvc.perform(delete("/api/v1/powertrains/{id}", id))
      .andExpect(status().isNotFound());
  }

  @Test
  void rename_returns_200() throws Exception {
    var pt = samplePowertrain();
    when(powertrainService.rename(any(PowertrainId.class), eq("2.0 TDI 200"))).thenReturn(pt);

    var requestBody = """
      {
        "name": "2.0 TDI 200",
        "drivetrainType": "ICE",
        "combinedPowerHp": 147,
        "units": [{"powerUnitId": 1, "role": "PRIMARY"}]
      }
      """;

    mockMvc.perform(patch("/api/v1/powertrains/{id}/name", pt.id().value())
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("2.0 TDI 150"));
  }
}
