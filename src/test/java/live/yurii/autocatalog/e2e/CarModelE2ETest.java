package live.yurii.autocatalog.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CarModelE2ETest extends AbstractE2ETest {

  @Test
  void create_model_for_existing_make_and_fetch_it_back() throws Exception {
    String makeId = createMake("BMW", "Germany");

    MvcResult created = mockMvc.perform(post("/api/v1/models")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"makeId\":\"" + makeId + "\",\"name\":\"3 Series\"}"))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value("3 Series"))
      .andExpect(jsonPath("$.slug").value("3-series"))
      .andExpect(jsonPath("$.makeId").value(makeId))
      .andReturn();

    String modelId = extractId(created);

    mockMvc.perform(get("/api/v1/models/{id}", modelId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("3 Series"));
  }

  @Test
  void create_model_for_missing_make_returns_404() throws Exception {
    mockMvc.perform(post("/api/v1/models")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"makeId\":\"" + UUID.randomUUID() + "\",\"name\":\"X\"}"))
      .andExpect(status().isNotFound());
  }

  @Test
  void get_models_by_make_returns_all_for_that_make() throws Exception {
    String makeId = createMake("Audi", "Germany");
    createModel(makeId, "A3");
    createModel(makeId, "A4");
    createModel(makeId, "A6");

    mockMvc.perform(get("/api/v1/models").param("makeId", makeId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  void link_successor_relation_between_two_models() throws Exception {
    String makeId = createMake("BMW", "Germany");
    String e90Id = createModel(makeId, "E90");
    String f30Id = createModel(makeId, "F30");

    mockMvc.perform(post("/api/v1/models/{id}/relations", e90Id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"targetModelId\":\"" + f30Id + "\",\"type\":\"SUCCESSOR\",\"note\":\"next-gen\"}"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.relations.length()").value(1))
      .andExpect(jsonPath("$.relations[0].targetModelId").value(f30Id))
      .andExpect(jsonPath("$.relations[0].type").value("SUCCESSOR"))
      .andExpect(jsonPath("$.relations[0].note").value("next-gen"));
  }

  @Test
  void link_relation_persists_across_subsequent_get() throws Exception {
    String makeId = createMake("Opel", "Germany");
    String fromId = createModel(makeId, "Astra G");
    String toId = createModel(makeId, "Astra H");

    mockMvc.perform(post("/api/v1/models/{id}/relations", fromId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"targetModelId\":\"" + toId + "\",\"type\":\"SUCCESSOR\"}"))
      .andExpect(status().isOk());

    mockMvc.perform(get("/api/v1/models/{id}", fromId))
      .andExpect(jsonPath("$.relations.length()").value(1))
      .andExpect(jsonPath("$.relations[0].targetModelId").value(toId));
  }

  @Test
  void link_self_relation_returns_400() throws Exception {
    String makeId = createMake("Fiat", "Italy");
    String modelId = createModel(makeId, "Panda");

    mockMvc.perform(post("/api/v1/models/{id}/relations", modelId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"targetModelId\":\"" + modelId + "\",\"type\":\"SUCCESSOR\"}"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void link_duplicate_relation_returns_409() throws Exception {
    String makeId = createMake("Ford", "USA");
    String fromId = createModel(makeId, "Focus Mk1");
    String toId = createModel(makeId, "Focus Mk2");

    mockMvc.perform(post("/api/v1/models/{id}/relations", fromId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"targetModelId\":\"" + toId + "\",\"type\":\"SUCCESSOR\"}"))
      .andExpect(status().isOk());

    mockMvc.perform(post("/api/v1/models/{id}/relations", fromId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"targetModelId\":\"" + toId + "\",\"type\":\"SUCCESSOR\"}"))
      .andExpect(status().isConflict());
  }

  @Test
  void get_missing_model_returns_404() throws Exception {
    mockMvc.perform(get("/api/v1/models/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound());
  }

  private String createMake(String name, String country) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/v1/makes")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"" + name + "\",\"country\":\"" + country + "\"}"))
      .andExpect(status().isCreated())
      .andReturn();
    return extractId(result);
  }

  private String createModel(String makeId, String name) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/v1/models")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"makeId\":\"" + makeId + "\",\"name\":\"" + name + "\"}"))
      .andExpect(status().isCreated())
      .andReturn();
    return extractId(result);
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
