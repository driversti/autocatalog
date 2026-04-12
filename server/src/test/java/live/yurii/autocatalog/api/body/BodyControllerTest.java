package live.yurii.autocatalog.api.body;

import live.yurii.autocatalog.api.shared.GlobalExceptionHandler;
import live.yurii.autocatalog.application.body.BodyService;
import live.yurii.autocatalog.application.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.body.Body;
import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.body.BodyStyle;
import live.yurii.autocatalog.domain.generation.GenerationId;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BodyController.class)
@Import(GlobalExceptionHandler.class)
class BodyControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BodyService bodyService;

  @Test
  void getByGeneration_returns_bodies() throws Exception {
    var generationId = GenerationId.generate();
    var b1 = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);
    var b2 = Body.create(generationId, BodyStyle.WAGON, 4700, 1810, 1460, 2800);
    when(bodyService.getByGeneration(any(GenerationId.class))).thenReturn(List.of(b1, b2));

    mockMvc.perform(get("/api/v1/bodies").param("generationId", generationId.value().toString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].bodyStyle").value("SEDAN"))
      .andExpect(jsonPath("$[1].bodyStyle").value("WAGON"));
  }

  @Test
  void getByGeneration_requires_generationId() throws Exception {
    mockMvc.perform(get("/api/v1/bodies"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void getById_returns_body() throws Exception {
    var generationId = GenerationId.generate();
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);
    body.setTrunkVolumeLitres(480);
    when(bodyService.getById(any(BodyId.class))).thenReturn(body);

    mockMvc.perform(get("/api/v1/bodies/{id}", body.id().value()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.bodyStyle").value("SEDAN"))
      .andExpect(jsonPath("$.lengthMm").value(4650))
      .andExpect(jsonPath("$.trunkVolumeLitres").value(480));
  }

  @Test
  void getById_returns_404_when_not_found() throws Exception {
    when(bodyService.getById(any(BodyId.class)))
      .thenThrow(new EntityNotFoundException("Body not found"));

    mockMvc.perform(get("/api/v1/bodies/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_returns_201_with_body() throws Exception {
    var generationId = GenerationId.generate();
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);
    when(bodyService.create(any(GenerationId.class), eq(BodyStyle.SEDAN),
      eq(4650), eq(1810), eq(1440), eq(2800), eq(null), eq(null)))
      .thenReturn(body);

    var requestBody = """
      {"generationId":"%s","bodyStyle":"SEDAN",
       "lengthMm":4650,"widthMm":1810,"heightMm":1440,"wheelbaseMm":2800}
      """.formatted(generationId.value());

    mockMvc.perform(post("/api/v1/bodies")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location",
        "http://localhost/api/v1/bodies/" + body.id().value()))
      .andExpect(jsonPath("$.bodyStyle").value("SEDAN"))
      .andExpect(jsonPath("$.lengthMm").value(4650));
  }

  @Test
  void create_with_trunk_volume_returns_201() throws Exception {
    var generationId = GenerationId.generate();
    var body = Body.create(generationId, BodyStyle.SEDAN, 4650, 1810, 1440, 2800);
    body.setTrunkVolumeLitres(480);
    when(bodyService.create(any(GenerationId.class), eq(BodyStyle.SEDAN),
      eq(4650), eq(1810), eq(1440), eq(2800), eq(480), eq(null)))
      .thenReturn(body);

    var requestBody = """
      {"generationId":"%s","bodyStyle":"SEDAN",
       "lengthMm":4650,"widthMm":1810,"heightMm":1440,"wheelbaseMm":2800,
       "trunkVolumeLitres":480}
      """.formatted(generationId.value());

    mockMvc.perform(post("/api/v1/bodies")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.trunkVolumeLitres").value(480));
  }

  @Test
  void create_returns_409_when_body_style_already_exists() throws Exception {
    when(bodyService.create(any(), any(), any(int.class), any(int.class), any(int.class), any(int.class), any(), any()))
      .thenThrow(new IllegalStateException("Body already exists"));

    var requestBody = """
      {"generationId":"%s","bodyStyle":"SEDAN",
       "lengthMm":4650,"widthMm":1810,"heightMm":1440,"wheelbaseMm":2800}
      """.formatted(UUID.randomUUID());

    mockMvc.perform(post("/api/v1/bodies")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isConflict());
  }

  @Test
  void create_returns_404_when_generation_not_found() throws Exception {
    when(bodyService.create(any(), any(), any(int.class), any(int.class), any(int.class), any(int.class), any(), any()))
      .thenThrow(new EntityNotFoundException("Generation not found"));

    var requestBody = """
      {"generationId":"%s","bodyStyle":"SEDAN",
       "lengthMm":4650,"widthMm":1810,"heightMm":1440,"wheelbaseMm":2800}
      """.formatted(UUID.randomUUID());

    mockMvc.perform(post("/api/v1/bodies")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_returns_400_when_length_not_positive() throws Exception {
    var requestBody = """
      {"generationId":"%s","bodyStyle":"SEDAN",
       "lengthMm":0,"widthMm":1810,"heightMm":1440,"wheelbaseMm":2800}
      """.formatted(UUID.randomUUID());

    mockMvc.perform(post("/api/v1/bodies")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isBadRequest());
  }

  @Test
  void delete_returns_204() throws Exception {
    var id = UUID.randomUUID();

    mockMvc.perform(delete("/api/v1/bodies/{id}", id))
      .andExpect(status().isNoContent());

    verify(bodyService).deleteById(new BodyId(id));
  }
}
