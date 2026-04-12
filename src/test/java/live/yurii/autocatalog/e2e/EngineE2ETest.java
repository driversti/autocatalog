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

class EngineE2ETest extends AbstractE2ETest {

  @Test
  void create_engine_with_all_required_fields_and_fetch_it_back() throws Exception {
    String body = "{\"code\":\"B58\",\"name\":\"3.0L TwinPower\",\"fuelType\":\"PETROL\","
      + "\"displacementCc\":2998,\"powerKw\":250,\"torqueNm\":500}";

    MvcResult created = mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.code").value("B58"))
      .andExpect(jsonPath("$.powerKw").value(250))
      .andExpect(jsonPath("$.powerHp").value(340))
      .andReturn();

    String id = extractId(created);

    mockMvc.perform(get("/api/v1/engines/{id}", id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.code").value("B58"))
      .andExpect(jsonPath("$.displacementCc").value(2998))
      .andExpect(jsonPath("$.powerKw").value(250))
      .andExpect(jsonPath("$.torqueNm").value(500));
  }

  @Test
  void create_engine_with_optional_fields_persists_them() throws Exception {
    String body = "{\"code\":\"B58-OPT\",\"name\":\"3.0L TwinPower\",\"fuelType\":\"PETROL\","
      + "\"displacementCc\":2998,\"powerKw\":250,\"torqueNm\":500,"
      + "\"cylinderCount\":6,\"configuration\":\"I6\"}";

    MvcResult created = mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.cylinderCount").value(6))
      .andExpect(jsonPath("$.configuration").value("I6"))
      .andReturn();

    String id = extractId(created);

    mockMvc.perform(get("/api/v1/engines/{id}", id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.cylinderCount").value(6))
      .andExpect(jsonPath("$.configuration").value("I6"));
  }

  @Test
  void create_hybrid_engine_persists_system_power() throws Exception {
    String body = "{\"code\":\"2ZR-FXE-T\",\"name\":\"1.8 Hybrid\",\"fuelType\":\"HYBRID\","
      + "\"displacementCc\":1798,\"powerKw\":73,\"torqueNm\":142,"
      + "\"cylinderCount\":4,\"configuration\":\"I4\",\"systemPowerKw\":100}";

    MvcResult created = mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.systemPowerKw").value(100))
      .andReturn();

    String id = extractId(created);

    mockMvc.perform(get("/api/v1/engines/{id}", id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.systemPowerKw").value(100));
  }

  @Test
  void create_engine_without_optional_fields() throws Exception {
    mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"code":"E-MOTOR-1","name":"Electric","fuelType":"ELECTRIC",
           "displacementCc":1,"powerKw":150,"torqueNm":300}"""))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.cylinderCount").isEmpty())
      .andExpect(jsonPath("$.configuration").isEmpty());
  }

  @Test
  void create_duplicate_code_returns_409() throws Exception {
    String body = """
      {"code":"M57","name":"3.0D","fuelType":"DIESEL",
       "displacementCc":2993,"powerKw":210,"torqueNm":600}""";

    mockMvc.perform(post("/api/v1/engines").contentType(MediaType.APPLICATION_JSON).content(body))
      .andExpect(status().isCreated());
    mockMvc.perform(post("/api/v1/engines").contentType(MediaType.APPLICATION_JSON).content(body))
      .andExpect(status().isConflict());
  }

  @Test
  void create_with_zero_displacement_returns_400() throws Exception {
    mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"code":"X","name":"X","fuelType":"PETROL",
           "displacementCc":0,"powerKw":100,"torqueNm":200}"""))
      .andExpect(status().isBadRequest());
  }

  @Test
  void filter_by_fuel_type_returns_only_matching() throws Exception {
    createEngine("P1", "PETROL");
    createEngine("D1", "DIESEL");
    createEngine("P2", "PETROL");

    mockMvc.perform(get("/api/v1/engines").param("fuelType", "PETROL"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.fuelType == 'PETROL')]").isArray())
      .andExpect(jsonPath("$[?(@.fuelType == 'DIESEL')]").isEmpty());
  }

  @Test
  void get_all_returns_all_created_engines() throws Exception {
    createEngine("A1", "PETROL");
    createEngine("A2", "HYBRID");

    mockMvc.perform(get("/api/v1/engines"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
  }

  @Test
  void get_missing_returns_404() throws Exception {
    mockMvc.perform(get("/api/v1/engines/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_with_blank_code_returns_400() throws Exception {
    mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"code":"","name":"name","fuelType":"PETROL",
           "displacementCc":2000,"powerKw":100,"torqueNm":200}"""))
      .andExpect(status().isBadRequest());
  }

  private void createEngine(String code, String fuelType) throws Exception {
    mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"code\":\"" + code + "\",\"name\":\"" + code + "\",\"fuelType\":\"" + fuelType + "\","
          + "\"displacementCc\":2000,\"powerKw\":120,\"torqueNm\":250}"))
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
