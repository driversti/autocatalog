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

class TransmissionE2ETest extends AbstractE2ETest {

  @Test
  void create_and_fetch_transmission() throws Exception {
    MvcResult created = mockMvc.perform(post("/api/v1/transmissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"type\":\"MANUAL\",\"gearCount\":6}"))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.type").value("MANUAL"))
      .andExpect(jsonPath("$.gearCount").value(6))
      .andReturn();

    String id = extractId(created);

    mockMvc.perform(get("/api/v1/transmissions/{id}", id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.type").value("MANUAL"));
  }

  @Test
  void get_all_returns_every_type() throws Exception {
    createTransmission("MANUAL", 6);
    createTransmission("AUTOMATIC", 8);
    createTransmission("DCT", 7);

    mockMvc.perform(get("/api/v1/transmissions"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
  }

  @Test
  void filter_by_type_returns_only_matching() throws Exception {
    createTransmission("MANUAL", 5);
    createTransmission("MANUAL", 6);
    createTransmission("AUTOMATIC", 8);

    mockMvc.perform(get("/api/v1/transmissions").param("type", "MANUAL"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.type == 'MANUAL')]").isArray())
      .andExpect(jsonPath("$[?(@.type == 'AUTOMATIC')]").isEmpty());
  }

  @Test
  void create_gear_count_above_max_returns_400() throws Exception {
    mockMvc.perform(post("/api/v1/transmissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"type\":\"MANUAL\",\"gearCount\":13}"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_with_invalid_type_returns_400() throws Exception {
    mockMvc.perform(post("/api/v1/transmissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"type\":\"STICKSHIFT\",\"gearCount\":6}"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void get_missing_id_returns_404() throws Exception {
    mockMvc.perform(get("/api/v1/transmissions/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_cvt_single_gear() throws Exception {
    mockMvc.perform(post("/api/v1/transmissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"type\":\"CVT\",\"gearCount\":1}"))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.type").value("CVT"));
  }

  private void createTransmission(String type, int gearCount) throws Exception {
    mockMvc.perform(post("/api/v1/transmissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"type\":\"" + type + "\",\"gearCount\":" + gearCount + "}"))
      .andExpect(status().isCreated());
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
