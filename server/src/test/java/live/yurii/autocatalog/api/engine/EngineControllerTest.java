package live.yurii.autocatalog.api.engine;

import live.yurii.autocatalog.api.shared.GlobalExceptionHandler;
import live.yurii.autocatalog.application.engine.EngineService;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.engine.Engine;
import live.yurii.autocatalog.domain.engine.EngineId;
import live.yurii.autocatalog.domain.engine.FuelType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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

@WebMvcTest(EngineController.class)
@Import(GlobalExceptionHandler.class)
class EngineControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private EngineService engineService;

  // Helper: create an engine with a fake persisted ID (simulate DB assignment)
  private Engine engineWithId(long id) {
    return Engine.reconstitute(new EngineId(id), "B58", "3.0L", FuelType.PETROL,
      2998, 250, 500, null, null, null, null, false);
  }

  @Test
  void getAll_without_filter_returns_engines() throws Exception {
    var e1 = Engine.create("B58", "3.0L", FuelType.PETROL, 2998, 250, 500);
    var e2 = Engine.create("M57", "3.0L D", FuelType.DIESEL, 2993, 210, 600);
    when(engineService.getAll()).thenReturn(List.of(e1, e2));

    mockMvc.perform(get("/api/v1/engines"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].code").value("B58"))
      .andExpect(jsonPath("$[1].code").value("M57"));

    verify(engineService).getAll();
  }

  @Test
  void getAll_with_fuel_filter_delegates() throws Exception {
    when(engineService.getByFuelType(FuelType.ELECTRIC)).thenReturn(List.of(
      Engine.create("E1", "Electric", FuelType.ELECTRIC, 1, 150, 300)
    ));

    mockMvc.perform(get("/api/v1/engines").param("fuelType", "ELECTRIC"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].fuelType").value("ELECTRIC"));

    verify(engineService).getByFuelType(FuelType.ELECTRIC);
  }

  @Test
  void getById_returns_engine_with_computed_hp() throws Exception {
    var engine = engineWithId(1L);
    when(engineService.getById(any(EngineId.class))).thenReturn(engine);

    mockMvc.perform(get("/api/v1/engines/{id}", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.code").value("B58"))
      .andExpect(jsonPath("$.powerKw").value(250))
      .andExpect(jsonPath("$.powerHp").value(340));
  }

  @Test
  void getById_returns_404_when_not_found() throws Exception {
    when(engineService.getById(any(EngineId.class)))
      .thenThrow(new EntityNotFoundException("Engine not found"));

    mockMvc.perform(get("/api/v1/engines/{id}", 999L))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_returns_201_with_full_body() throws Exception {
    var engine = engineWithId(1L);
    when(engineService.create(eq("B58"), eq("3.0L"), eq(FuelType.PETROL),
      eq(2998), eq(250), eq(500), eq(6), eq("I6"), eq(null)))
      .thenReturn(engine);

    var body = """
      {"code":"B58","name":"3.0L","fuelType":"PETROL",
       "displacementCc":2998,"powerKw":250,"torqueNm":500,
       "cylinderCount":6,"configuration":"I6"}""";

    mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location",
        "http://localhost/api/v1/engines/1"))
      .andExpect(jsonPath("$.code").value("B58"));
  }

  @Test
  void create_without_optional_fields_still_works() throws Exception {
    var engine = engineWithId(2L);
    when(engineService.create(any(), any(), any(), any(), any(int.class), any(),
      any(), any(), any()))
      .thenReturn(engine);

    var body = """
      {"code":"E1","name":"Electric","fuelType":"ELECTRIC",
       "displacementCc":1,"powerKw":150,"torqueNm":300}""";

    mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.cylinderCount").isEmpty())
      .andExpect(jsonPath("$.configuration").isEmpty())
      .andExpect(jsonPath("$.systemPowerKw").isEmpty());
  }

  @Test
  void create_returns_409_when_code_already_exists() throws Exception {
    when(engineService.create(any(), any(), any(), any(), any(int.class), any(),
      any(), any(), any()))
      .thenThrow(new IllegalStateException("Engine already exists: B58"));

    var body = """
      {"code":"B58","name":"3.0L","fuelType":"PETROL",
       "displacementCc":2998,"powerKw":250,"torqueNm":500}""";

    mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isConflict());
  }

  @Test
  void create_returns_400_when_code_blank() throws Exception {
    var body = """
      {"code":"","name":"3.0L","fuelType":"PETROL",
       "displacementCc":2998,"powerKw":250,"torqueNm":500}""";

    mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_returns_400_when_displacement_not_positive() throws Exception {
    var body = """
      {"code":"B58","name":"3.0L","fuelType":"PETROL",
       "displacementCc":0,"powerKw":250,"torqueNm":500}""";

    mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_returns_400_when_cylinder_count_exceeds_max() throws Exception {
    var body = """
      {"code":"W16","name":"Quad-Turbo","fuelType":"PETROL",
       "displacementCc":7993,"powerKw":900,"torqueNm":1600,
       "cylinderCount":17,"configuration":"W16"}""";

    mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void delete_returns_204() throws Exception {
    doNothing().when(engineService).deleteById(any(EngineId.class));

    mockMvc.perform(delete("/api/v1/engines/{id}", 1L))
      .andExpect(status().isNoContent());

    verify(engineService).deleteById(any(EngineId.class));
  }

  @Test
  void delete_returns_409_when_referenced_by_powertrain() throws Exception {
    doThrow(new IllegalStateException("Cannot delete engine: referenced by powertrain(s)"))
      .when(engineService).deleteById(any(EngineId.class));

    mockMvc.perform(delete("/api/v1/engines/{id}", 1L))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.detail").value("Cannot delete engine: referenced by powertrain(s)"));
  }

  @Test
  void delete_returns_404_when_not_found() throws Exception {
    doThrow(new EntityNotFoundException("Engine not found"))
      .when(engineService).deleteById(any(EngineId.class));

    mockMvc.perform(delete("/api/v1/engines/{id}", 999L))
      .andExpect(status().isNotFound());
  }

  @Test
  void rename_returns_200_with_updated_name() throws Exception {
    var engine = engineWithId(1L);
    when(engineService.rename(any(EngineId.class), eq("3.0L I6"))).thenAnswer(invocation -> {
      var e = Engine.reconstitute(new EngineId(1L), "B58", "3.0L I6", FuelType.PETROL,
        2998, 250, 500, null, null, null, null, false);
      return e;
    });
    var body = """
      {"code":"B58","name":"3.0L I6","fuelType":"PETROL",
       "displacementCc":2998,"powerKw":250,"torqueNm":500}""";

    mockMvc.perform(patch("/api/v1/engines/{id}/name", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("3.0L I6"));

    verify(engineService).rename(any(EngineId.class), eq("3.0L I6"));
  }

  @Test
  void rename_returns_404_when_not_found() throws Exception {
    when(engineService.rename(any(EngineId.class), any()))
      .thenThrow(new EntityNotFoundException("Engine not found"));
    var body = """
      {"code":"B58","name":"NewName","fuelType":"PETROL",
       "displacementCc":2998,"powerKw":250,"torqueNm":500}""";

    mockMvc.perform(patch("/api/v1/engines/{id}/name", 999L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isNotFound());
  }
}
