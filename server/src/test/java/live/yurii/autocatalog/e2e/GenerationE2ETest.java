package live.yurii.autocatalog.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GenerationE2ETest extends AbstractE2ETest {

  @Test
  void create_generation_for_existing_model_and_fetch_it() throws Exception {
    String makeId = createMake("BMW", "Germany");
    String modelId = createModel(makeId, "3 Series");

    MvcResult created = mockMvc.perform(post("/api/v1/generations")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"modelId\":\"" + modelId + "\",\"name\":\"F30\",\"yearFrom\":2012}"))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value("F30"))
      .andExpect(jsonPath("$.yearFrom").value(2012))
      .andExpect(jsonPath("$.yearTo").isEmpty())
      .andReturn();

    String genId = extractId(created);

    mockMvc.perform(get("/api/v1/generations/{id}", genId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("F30"));
  }

  @Test
  void create_generation_for_missing_model_returns_404() throws Exception {
    mockMvc.perform(post("/api/v1/generations")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"modelId\":\"" + UUID.randomUUID() + "\",\"name\":\"X\",\"yearFrom\":2000}"))
      .andExpect(status().isNotFound());
  }

  @Test
  void close_generation_sets_end_year() throws Exception {
    String makeId = createMake("BMW", "Germany");
    String modelId = createModel(makeId, "3 Series");
    String genId = createGeneration(modelId, "F30", 2012);

    mockMvc.perform(patch("/api/v1/generations/{id}/close", genId).param("year", "2019"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.yearTo").value(2019));

    mockMvc.perform(get("/api/v1/generations/{id}", genId))
      .andExpect(jsonPath("$.yearTo").value(2019));
  }

  @Test
  void close_generation_before_start_year_returns_400() throws Exception {
    String makeId = createMake("BMW", "Germany");
    String modelId = createModel(makeId, "3 Series");
    String genId = createGeneration(modelId, "F30", 2012);

    mockMvc.perform(patch("/api/v1/generations/{id}/close", genId).param("year", "2000"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void get_generations_by_model_returns_all_for_that_model() throws Exception {
    String makeId = createMake("BMW", "Germany");
    String modelId = createModel(makeId, "3 Series");
    createGeneration(modelId, "E90", 2005);
    createGeneration(modelId, "F30", 2012);
    createGeneration(modelId, "G20", 2019);

    mockMvc.perform(get("/api/v1/generations").param("modelId", modelId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  void create_with_invalid_year_returns_400() throws Exception {
    String makeId = createMake("BMW", "Germany");
    String modelId = createModel(makeId, "3 Series");

    mockMvc.perform(post("/api/v1/generations")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"modelId\":\"" + modelId + "\",\"name\":\"Prehistoric\",\"yearFrom\":1800}"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void get_missing_generation_returns_404() throws Exception {
    mockMvc.perform(get("/api/v1/generations/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound());
  }

  // helpers --------------------------------------------------------------

  private String createMake(String name, String country) throws Exception {
    return extractId(mockMvc.perform(post("/api/v1/makes")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"" + name + "\",\"country\":\"" + country + "\"}"))
      .andExpect(status().isCreated())
      .andReturn());
  }

  private String createModel(String makeId, String name) throws Exception {
    return extractId(mockMvc.perform(post("/api/v1/models")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"makeId\":\"" + makeId + "\",\"name\":\"" + name + "\"}"))
      .andExpect(status().isCreated())
      .andReturn());
  }

  private String createGeneration(String modelId, String name, int yearFrom) throws Exception {
    return extractId(mockMvc.perform(post("/api/v1/generations")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"modelId\":\"" + modelId + "\",\"name\":\"" + name + "\",\"yearFrom\":" + yearFrom + "}"))
      .andExpect(status().isCreated())
      .andReturn());
  }

  private String extractId(MvcResult result) throws Exception {
    String body = result.getResponse().getContentAsString();
    int start = body.indexOf("\"id\":\"") + 6;
    int end = body.indexOf('"', start);
    String id = body.substring(start, end);
    assertThat(id).matches("[0-9a-f-]{36}");
    return id;
  }
}
