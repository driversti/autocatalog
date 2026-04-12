package live.yurii.autocatalog.api.generation;

import live.yurii.autocatalog.api.shared.GlobalExceptionHandler;
import live.yurii.autocatalog.application.generation.GenerationService;
import live.yurii.autocatalog.application.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.generation.Generation;
import live.yurii.autocatalog.domain.generation.GenerationId;
import live.yurii.autocatalog.domain.model.ModelId;
import live.yurii.autocatalog.domain.shared.YearRange;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenerationController.class)
@Import(GlobalExceptionHandler.class)
class GenerationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private GenerationService generationService;

  @Test
  void getByModel_returns_generations() throws Exception {
    var modelId = ModelId.generate();
    var g1 = Generation.create(modelId, "E90", new YearRange(2005, 2012));
    var g2 = Generation.create(modelId, "F30", new YearRange(2012, null));
    when(generationService.getByModel(any(ModelId.class))).thenReturn(List.of(g1, g2));

    mockMvc.perform(get("/api/v1/generations").param("modelId", modelId.value().toString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].name").value("E90"))
      .andExpect(jsonPath("$[0].yearFrom").value(2005))
      .andExpect(jsonPath("$[0].yearTo").value(2012))
      .andExpect(jsonPath("$[1].name").value("F30"))
      .andExpect(jsonPath("$[1].yearTo").isEmpty());
  }

  @Test
  void getByModel_requires_modelId() throws Exception {
    mockMvc.perform(get("/api/v1/generations"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void getById_returns_generation() throws Exception {
    var modelId = ModelId.generate();
    var gen = Generation.create(modelId, "F30", new YearRange(2012, 2019));
    when(generationService.getById(any(GenerationId.class))).thenReturn(gen);

    mockMvc.perform(get("/api/v1/generations/{id}", gen.id().value()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("F30"))
      .andExpect(jsonPath("$.yearFrom").value(2012))
      .andExpect(jsonPath("$.yearTo").value(2019));
  }

  @Test
  void getById_returns_404_when_not_found() throws Exception {
    when(generationService.getById(any(GenerationId.class)))
      .thenThrow(new EntityNotFoundException("Generation not found"));

    mockMvc.perform(get("/api/v1/generations/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_returns_201_with_created_generation() throws Exception {
    var modelId = ModelId.generate();
    var gen = Generation.create(modelId, "F30", new YearRange(2012, null));
    when(generationService.create(any(ModelId.class), eq("F30"), any(YearRange.class)))
      .thenReturn(gen);

    var body = "{\"modelId\":\"" + modelId.value() + "\",\"name\":\"F30\",\"yearFrom\":2012}";

    mockMvc.perform(post("/api/v1/generations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value("F30"))
      .andExpect(jsonPath("$.yearFrom").value(2012))
      .andExpect(jsonPath("$.yearTo").isEmpty());
  }

  @Test
  void create_returns_400_when_year_out_of_range() throws Exception {
    var body = "{\"modelId\":\"" + UUID.randomUUID() + "\",\"name\":\"Old\",\"yearFrom\":1800}";

    mockMvc.perform(post("/api/v1/generations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_returns_400_when_name_blank() throws Exception {
    var body = "{\"modelId\":\"" + UUID.randomUUID() + "\",\"name\":\"\",\"yearFrom\":2000}";

    mockMvc.perform(post("/api/v1/generations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_returns_404_when_model_not_found() throws Exception {
    when(generationService.create(any(), any(), any()))
      .thenThrow(new EntityNotFoundException("Model not found"));
    var body = "{\"modelId\":\"" + UUID.randomUUID() + "\",\"name\":\"X\",\"yearFrom\":2012}";

    mockMvc.perform(post("/api/v1/generations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isNotFound());
  }

  @Test
  void close_returns_generation_with_end_year() throws Exception {
    var modelId = ModelId.generate();
    var gen = Generation.create(modelId, "F30", new YearRange(2012, null));
    gen.close(2019);
    when(generationService.close(any(GenerationId.class), eq(2019))).thenReturn(gen);

    mockMvc.perform(patch("/api/v1/generations/{id}/close", gen.id().value())
        .param("year", "2019"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.yearTo").value(2019));
  }

  @Test
  void close_returns_400_when_year_before_start() throws Exception {
    when(generationService.close(any(), eq(2000)))
      .thenThrow(new IllegalArgumentException("Close year cannot be before start year"));

    mockMvc.perform(patch("/api/v1/generations/{id}/close", UUID.randomUUID())
        .param("year", "2000"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.detail").value("Close year cannot be before start year"));
  }
}
