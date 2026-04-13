package live.yurii.autocatalog.api.fuelcellstack;

import live.yurii.autocatalog.api.shared.GlobalExceptionHandler;
import live.yurii.autocatalog.application.fuelcellstack.FuelCellStackService;
import live.yurii.autocatalog.domain.fuelcellstack.FuelCellStack;
import live.yurii.autocatalog.domain.fuelcellstack.FuelCellStackId;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FuelCellStackController.class)
@Import(GlobalExceptionHandler.class)
class FuelCellStackControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FuelCellStackService stackService;

  private FuelCellStack stackWithId(long id) {
    return FuelCellStack.reconstitute(new FuelCellStackId(id),
      "Toyota FC Stack", 128, new BigDecimal("5.6"));
  }

  @Test
  void getAll_returns_stacks() throws Exception {
    when(stackService.getAll()).thenReturn(List.of(stackWithId(1L)));

    mockMvc.perform(get("/api/v1/fuel-cell-stacks"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].label").value("Toyota FC Stack"));
  }

  @Test
  void getById_returns_stack() throws Exception {
    when(stackService.getById(new FuelCellStackId(1L))).thenReturn(stackWithId(1L));

    mockMvc.perform(get("/api/v1/fuel-cell-stacks/{id}", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.label").value("Toyota FC Stack"))
      .andExpect(jsonPath("$.peakPowerKw").value(128));
  }

  @Test
  void getById_returns_404_when_not_found() throws Exception {
    when(stackService.getById(any())).thenThrow(new EntityNotFoundException("not found"));

    mockMvc.perform(get("/api/v1/fuel-cell-stacks/{id}", 999L))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_returns_201() throws Exception {
    when(stackService.create(any(), any(), any())).thenReturn(stackWithId(1L));

    mockMvc.perform(post("/api/v1/fuel-cell-stacks")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"label":"Toyota FC Stack","peakPowerKw":128,"hydrogenTankKg":5.6}"""))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", "http://localhost/api/v1/fuel-cell-stacks/1"))
      .andExpect(jsonPath("$.label").value("Toyota FC Stack"));
  }

  @Test
  void create_returns_400_when_label_blank() throws Exception {
    mockMvc.perform(post("/api/v1/fuel-cell-stacks")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"label":""}"""))
      .andExpect(status().isBadRequest());
  }

  @Test
  void delete_returns_204() throws Exception {
    mockMvc.perform(delete("/api/v1/fuel-cell-stacks/{id}", 1L))
      .andExpect(status().isNoContent());
  }

  @Test
  void delete_returns_409_when_referenced() throws Exception {
    doThrow(new IllegalStateException("Cannot delete fuel cell stack: referenced by powertrain(s)"))
      .when(stackService).deleteById(any());

    mockMvc.perform(delete("/api/v1/fuel-cell-stacks/{id}", 1L))
      .andExpect(status().isConflict());
  }
}
