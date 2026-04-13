package live.yurii.autocatalog.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * E2E test for Powertrain creation using the new PowerUnit composition model.
 */
class PowertrainE2ETest extends AbstractE2ETest {

  /**
   * Creates an electric motor and returns its power_unit id.
   */
  private long createElectricMotor(String label) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/v1/electric-motors")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"label\":\"" + label + "\",\"powerKw\":150,\"torqueNm\":350}"))
      .andExpect(status().isCreated())
      .andReturn();
    String body = result.getResponse().getContentAsString();
    int start = body.indexOf("\"id\":") + 5;
    int end = start;
    while (end < body.length() && Character.isDigit(body.charAt(end))) end++;
    return Long.parseLong(body.substring(start, end));
  }

  @Test
  void create_bev_powertrain_with_single_electric_motor() throws Exception {
    long motorId = createElectricMotor("EQA Front EM 190kW");

    String powertrainBody = """
      {
        "name": "EQA 250 BEV",
        "drivetrainType": "BEV",
        "combinedPowerHp": 190,
        "batteryCapacityKwh": 66.5,
        "electricRangeKm": 426,
        "units": [{"powerUnitId": %d, "role": "PRIMARY"}]
      }
      """.formatted(motorId);

    MvcResult created = mockMvc.perform(post("/api/v1/powertrains")
        .contentType(MediaType.APPLICATION_JSON)
        .content(powertrainBody))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value("EQA 250 BEV"))
      .andExpect(jsonPath("$.drivetrainType").value("BEV"))
      .andExpect(jsonPath("$.combinedPowerHp").value(190))
      .andExpect(jsonPath("$.batteryCapacityKwh").value(66.5))
      .andExpect(jsonPath("$.electricRangeKm").value(426))
      .andExpect(jsonPath("$.units.length()").value(1))
      .andExpect(jsonPath("$.units[0].powerUnitId").value((int) motorId))
      .andExpect(jsonPath("$.units[0].role").value("PRIMARY"))
      .andReturn();

    // Fetch it back
    String ptId = extractUUID(created);
    mockMvc.perform(get("/api/v1/powertrains/{id}", ptId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("EQA 250 BEV"))
      .andExpect(jsonPath("$.units.length()").value(1));
  }

  @Test
  void create_phev_powertrain_with_engine_and_electric_motor() throws Exception {
    // Create an ICE engine
    MvcResult engineResult = mockMvc.perform(post("/api/v1/engines")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"code":"M260-PHEV","name":"2.0L Turbo","fuelType":"PETROL",
           "displacementCc":1991,"powerKw":120,"torqueNm":250}"""))
      .andExpect(status().isCreated())
      .andReturn();
    long enginePuId = extractLongId(engineResult);

    // Create an electric motor
    long motorId = createElectricMotor("A250e Front EM");

    // Create PHEV powertrain
    mockMvc.perform(post("/api/v1/powertrains")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "name": "A250e PHEV",
            "drivetrainType": "PHEV",
            "combinedPowerHp": 218,
            "combinedTorqueNm": 450,
            "batteryCapacityKwh": 15.6,
            "electricRangeKm": 80,
            "units": [
              {"powerUnitId": %d, "role": "PRIMARY"},
              {"powerUnitId": %d, "role": "SECONDARY"}
            ]
          }
          """.formatted(enginePuId, motorId)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.drivetrainType").value("PHEV"))
      .andExpect(jsonPath("$.units.length()").value(2));
  }

  @Test
  void delete_electric_motor_when_referenced_returns_409() throws Exception {
    long motorId = createElectricMotor("Protected EM");

    mockMvc.perform(post("/api/v1/powertrains")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "name": "Protected BEV",
            "drivetrainType": "BEV",
            "combinedPowerHp": 150,
            "batteryCapacityKwh": 50.0,
            "electricRangeKm": 300,
            "units": [{"powerUnitId": %d, "role": "PRIMARY"}]
          }
          """.formatted(motorId)))
      .andExpect(status().isCreated());

    // Try to delete the motor — should be 409
    mockMvc.perform(delete("/api/v1/electric-motors/{id}", motorId))
      .andExpect(status().isConflict());
  }

  @Test
  void duplicate_powertrain_name_returns_409() throws Exception {
    long motorId = createElectricMotor("Dup EM");

    String body = """
      {
        "name": "Unique Name BEV",
        "drivetrainType": "BEV",
        "combinedPowerHp": 150,
        "batteryCapacityKwh": 50.0,
        "electricRangeKm": 300,
        "units": [{"powerUnitId": %d, "role": "PRIMARY"}]
      }
      """.formatted(motorId);

    mockMvc.perform(post("/api/v1/powertrains")
        .contentType(MediaType.APPLICATION_JSON).content(body))
      .andExpect(status().isCreated());

    mockMvc.perform(post("/api/v1/powertrains")
        .contentType(MediaType.APPLICATION_JSON).content(body))
      .andExpect(status().isConflict());
  }

  private String extractUUID(MvcResult result) throws Exception {
    String body = result.getResponse().getContentAsString();
    int start = body.indexOf("\"id\":\"") + 6;
    int end = body.indexOf('"', start);
    String id = body.substring(start, end);
    assertThat(id).matches("[0-9a-f-]{36}");
    return id;
  }

  private long extractLongId(MvcResult result) throws Exception {
    String body = result.getResponse().getContentAsString();
    int start = body.indexOf("\"id\":") + 5;
    int end = start;
    while (end < body.length() && Character.isDigit(body.charAt(end))) end++;
    return Long.parseLong(body.substring(start, end));
  }
}
