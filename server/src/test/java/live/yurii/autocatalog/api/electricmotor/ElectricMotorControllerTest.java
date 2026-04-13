package live.yurii.autocatalog.api.electricmotor;

import live.yurii.autocatalog.api.shared.GlobalExceptionHandler;
import live.yurii.autocatalog.application.electricmotor.ElectricMotorService;
import live.yurii.autocatalog.domain.electricmotor.ElectricMotor;
import live.yurii.autocatalog.domain.electricmotor.ElectricMotorId;
import live.yurii.autocatalog.domain.electricmotor.MotorType;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ElectricMotorController.class)
@Import(GlobalExceptionHandler.class)
class ElectricMotorControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ElectricMotorService motorService;

  private ElectricMotor motorWithId(long id) {
    return ElectricMotor.reconstitute(new ElectricMotorId(id),
      "Rear EM 75kW", 75, 250, MotorType.PERMANENT_MAGNET);
  }

  @Test
  void getAll_returns_motors() throws Exception {
    when(motorService.getAll()).thenReturn(List.of(motorWithId(1L)));

    mockMvc.perform(get("/api/v1/electric-motors"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].label").value("Rear EM 75kW"));
  }

  @Test
  void getById_returns_motor() throws Exception {
    when(motorService.getById(new ElectricMotorId(1L))).thenReturn(motorWithId(1L));

    mockMvc.perform(get("/api/v1/electric-motors/{id}", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.label").value("Rear EM 75kW"))
      .andExpect(jsonPath("$.powerKw").value(75));
  }

  @Test
  void getById_returns_404_when_not_found() throws Exception {
    when(motorService.getById(any())).thenThrow(new EntityNotFoundException("not found"));

    mockMvc.perform(get("/api/v1/electric-motors/{id}", 999L))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_returns_201() throws Exception {
    when(motorService.create(any(), any(), any(), any())).thenReturn(motorWithId(1L));

    mockMvc.perform(post("/api/v1/electric-motors")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"label":"Rear EM 75kW","powerKw":75,"torqueNm":250,"motorType":"PERMANENT_MAGNET"}"""))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", "http://localhost/api/v1/electric-motors/1"))
      .andExpect(jsonPath("$.label").value("Rear EM 75kW"));
  }

  @Test
  void create_returns_400_when_label_blank() throws Exception {
    mockMvc.perform(post("/api/v1/electric-motors")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"label":"","powerKw":75}"""))
      .andExpect(status().isBadRequest());
  }

  @Test
  void delete_returns_204() throws Exception {
    mockMvc.perform(delete("/api/v1/electric-motors/{id}", 1L))
      .andExpect(status().isNoContent());
  }

  @Test
  void delete_returns_409_when_referenced() throws Exception {
    doThrow(new IllegalStateException("Cannot delete electric motor: referenced by powertrain(s)"))
      .when(motorService).deleteById(any());

    mockMvc.perform(delete("/api/v1/electric-motors/{id}", 1L))
      .andExpect(status().isConflict());
  }

  @Test
  void updateLabel_returns_200() throws Exception {
    when(motorService.updateLabel(any(), any())).thenReturn(motorWithId(1L));

    mockMvc.perform(patch("/api/v1/electric-motors/{id}/label", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"label":"New Label"}"""))
      .andExpect(status().isOk());
  }

  @Test
  void updateSpecs_returns_200() throws Exception {
    when(motorService.updateSpecs(any(), any(), any(), any())).thenReturn(motorWithId(1L));

    mockMvc.perform(patch("/api/v1/electric-motors/{id}/specs", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"label":"EM","powerKw":100,"torqueNm":300,"motorType":"INDUCTION"}"""))
      .andExpect(status().isOk());
  }
}
