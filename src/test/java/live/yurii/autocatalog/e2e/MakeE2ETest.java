package live.yurii.autocatalog.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MakeE2ETest extends AbstractE2ETest {

  @Test
  void create_then_get_by_id_returns_same_make() throws Exception {
    MvcResult created = mockMvc.perform(post("/api/v1/makes")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"BMW\",\"country\":\"Germany\"}"))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").exists())
      .andExpect(jsonPath("$.name").value("BMW"))
      .andExpect(jsonPath("$.country").value("Germany"))
      .andExpect(jsonPath("$.slug").value("bmw"))
      .andExpect(header().exists("Location"))
      .andReturn();

    String id = extractId(created);

    mockMvc.perform(get("/api/v1/makes/{id}", id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("BMW"));
  }

  @Test
  void create_then_get_by_slug_returns_same_make() throws Exception {
    mockMvc.perform(post("/api/v1/makes")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Audi\",\"country\":\"Germany\"}"))
      .andExpect(status().isCreated());

    mockMvc.perform(get("/api/v1/makes/slug/{slug}", "audi"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Audi"));
  }

  @Test
  void rename_updates_name_and_slug() throws Exception {
    String id = createMake("Opel", "Germany");

    mockMvc.perform(patch("/api/v1/makes/{id}/name", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Opel AG\",\"country\":\"Germany\"}"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Opel AG"))
      .andExpect(jsonPath("$.slug").value("opel-ag"));

    mockMvc.perform(get("/api/v1/makes/{id}", id))
      .andExpect(jsonPath("$.name").value("Opel AG"));
  }

  @Test
  void create_duplicate_name_returns_409() throws Exception {
    createMake("Ferrari", "Italy");

    mockMvc.perform(post("/api/v1/makes")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Ferrari\",\"country\":\"Italy\"}"))
      .andExpect(status().isConflict());
  }

  @Test
  void get_missing_id_returns_404() throws Exception {
    mockMvc.perform(get("/api/v1/makes/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound());
  }

  @Test
  void get_missing_slug_returns_404() throws Exception {
    mockMvc.perform(get("/api/v1/makes/slug/{slug}", "does-not-exist"))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_with_blank_name_returns_400() throws Exception {
    mockMvc.perform(post("/api/v1/makes")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"\",\"country\":\"Germany\"}"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void get_all_returns_created_makes() throws Exception {
    createMake("Lancia", "Italy");
    createMake("Skoda", "Czechia");

    mockMvc.perform(get("/api/v1/makes"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
  }

  private String createMake(String name, String country) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/v1/makes")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"" + name + "\",\"country\":\"" + country + "\"}"))
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
