package live.yurii.autocatalog.api.variant;

import live.yurii.autocatalog.api.shared.GlobalExceptionHandler;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import live.yurii.autocatalog.application.variant.VariantBatchService;
import live.yurii.autocatalog.application.variant.VariantService;
import live.yurii.autocatalog.domain.body.BodyId;
import live.yurii.autocatalog.domain.powertrain.PowertrainId;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.variant.Drivetrain;
import live.yurii.autocatalog.domain.variant.Variant;
import live.yurii.autocatalog.domain.variant.VariantId;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VariantController.class)
@Import(GlobalExceptionHandler.class)
class VariantControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private VariantService variantService;

  @MockitoBean
  private VariantBatchService variantBatchService;

  @Test
  void getByBody_returns_variants() throws Exception {
    var bodyId = BodyId.generate();
    var powertrainId = PowertrainId.generate();
    var transmissionId = TransmissionId.generate();
    var v1 = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);
    var v2 = Variant.create(bodyId, PowertrainId.generate(), TransmissionId.generate(), Drivetrain.AWD, 1500);
    when(variantService.getByBody(any(BodyId.class))).thenReturn(List.of(v1, v2));

    mockMvc.perform(get("/api/v1/variants").param("bodyId", bodyId.value().toString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].drivetrain").value("FWD"))
      .andExpect(jsonPath("$[1].drivetrain").value("AWD"));
  }

  @Test
  void getByBody_requires_bodyId() throws Exception {
    mockMvc.perform(get("/api/v1/variants"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void getById_returns_variant() throws Exception {
    var bodyId = BodyId.generate();
    var powertrainId = PowertrainId.generate();
    var transmissionId = TransmissionId.generate();
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.RWD, 1600);
    variant.setGroundClearanceMm(130);
    when(variantService.getById(any(VariantId.class))).thenReturn(variant);

    mockMvc.perform(get("/api/v1/variants/{id}", variant.id().value()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.drivetrain").value("RWD"))
      .andExpect(jsonPath("$.curbWeightKg").value(1600))
      .andExpect(jsonPath("$.groundClearanceMm").value(130))
      .andExpect(jsonPath("$.powertrainId").value(powertrainId.value().toString()));
  }

  @Test
  void getById_returns_404_when_not_found() throws Exception {
    when(variantService.getById(any(VariantId.class)))
      .thenThrow(new EntityNotFoundException("Variant not found"));

    mockMvc.perform(get("/api/v1/variants/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_returns_201_with_variant() throws Exception {
    var bodyId = BodyId.generate();
    var powertrainId = PowertrainId.generate();
    var transmissionId = TransmissionId.generate();
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);
    when(variantService.create(any(BodyId.class), any(PowertrainId.class),
      any(TransmissionId.class), eq(Drivetrain.FWD), eq(1350), eq(null)))
      .thenReturn(variant);

    var requestBody = """
      {"bodyId":"%s","powertrainId":"%s","transmissionId":"%s","drivetrain":"FWD","curbWeightKg":1350}
      """.formatted(bodyId.value(), powertrainId.value(), transmissionId.value());

    mockMvc.perform(post("/api/v1/variants")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location",
        "http://localhost/api/v1/variants/" + variant.id().value()))
      .andExpect(jsonPath("$.drivetrain").value("FWD"))
      .andExpect(jsonPath("$.curbWeightKg").value(1350));
  }

  @Test
  void create_returns_409_when_duplicate() throws Exception {
    when(variantService.create(any(), any(), any(), any(), any(int.class), any()))
      .thenThrow(new IllegalStateException("Variant already exists"));

    var requestBody = """
      {"bodyId":"%s","powertrainId":"%s","transmissionId":"%s","drivetrain":"FWD","curbWeightKg":1350}
      """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

    mockMvc.perform(post("/api/v1/variants")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isConflict());
  }

  @Test
  void create_returns_400_when_curb_weight_not_positive() throws Exception {
    var requestBody = """
      {"bodyId":"%s","powertrainId":"%s","transmissionId":"%s","drivetrain":"FWD","curbWeightKg":0}
      """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

    mockMvc.perform(post("/api/v1/variants")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isBadRequest());
  }

  @Test
  void addMarket_returns_variant_with_market() throws Exception {
    var bodyId = BodyId.generate();
    var powertrainId = PowertrainId.generate();
    var transmissionId = TransmissionId.generate();
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);
    variant.addMarket("UA");
    when(variantService.addMarket(any(VariantId.class), eq("UA"))).thenReturn(variant);

    mockMvc.perform(post("/api/v1/variants/{id}/markets/{market}",
        variant.id().value(), "UA"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.markets[0]").value("UA"));
  }

  @Test
  void delete_returns_204() throws Exception {
    var id = UUID.randomUUID();

    mockMvc.perform(delete("/api/v1/variants/{id}", id))
      .andExpect(status().isNoContent());

    verify(variantService).deleteById(new VariantId(id));
  }

  @Test
  void createBatch_returns_mixed_results_when_some_fail() throws Exception {
    var bodyId = BodyId.generate();
    var powertrainId = PowertrainId.generate();
    var transmissionId = TransmissionId.generate();
    var successVariant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);

    when(variantBatchService.createAll(anyList())).thenReturn(List.of(
      VariantBatchService.BatchResult.success(0, successVariant),
      VariantBatchService.BatchResult.failure(1, "Body not found: abc")
    ));

    var requestBody = """
      [
        {"bodyId":"%s","powertrainId":"%s","transmissionId":"%s","drivetrain":"FWD","curbWeightKg":1350},
        {"bodyId":"%s","powertrainId":"%s","transmissionId":"%s","drivetrain":"AWD","curbWeightKg":1600}
      ]
      """.formatted(bodyId.value(), powertrainId.value(), transmissionId.value(),
        UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

    mockMvc.perform(post("/api/v1/variants/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].status").value("SUCCESS"))
      .andExpect(jsonPath("$[0].index").value(0))
      .andExpect(jsonPath("$[0].variant.drivetrain").value("FWD"))
      .andExpect(jsonPath("$[0].error").doesNotExist())
      .andExpect(jsonPath("$[1].status").value("FAILURE"))
      .andExpect(jsonPath("$[1].index").value(1))
      .andExpect(jsonPath("$[1].error").value("Body not found: abc"))
      .andExpect(jsonPath("$[1].variant").doesNotExist());
  }

  @Test
  void createBatch_returns_all_successes() throws Exception {
    var bodyId = BodyId.generate();
    var powertrainId = PowertrainId.generate();
    var transmissionId = TransmissionId.generate();
    var v1 = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1350);
    var v2 = Variant.create(bodyId, PowertrainId.generate(), TransmissionId.generate(), Drivetrain.AWD, 1600);

    when(variantBatchService.createAll(anyList())).thenReturn(List.of(
      VariantBatchService.BatchResult.success(0, v1),
      VariantBatchService.BatchResult.success(1, v2)
    ));

    var requestBody = """
      [
        {"bodyId":"%s","powertrainId":"%s","transmissionId":"%s","drivetrain":"FWD","curbWeightKg":1350},
        {"bodyId":"%s","powertrainId":"%s","transmissionId":"%s","drivetrain":"AWD","curbWeightKg":1600}
      ]
      """.formatted(bodyId.value(), powertrainId.value(), transmissionId.value(),
        bodyId.value(), PowertrainId.generate().value(), TransmissionId.generate().value());

    mockMvc.perform(post("/api/v1/variants/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].status").value("SUCCESS"))
      .andExpect(jsonPath("$[1].status").value("SUCCESS"));
  }

  @Test
  void createBatch_returns_400_when_list_is_empty() throws Exception {
    mockMvc.perform(post("/api/v1/variants/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .content("[]"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void createBatch_returns_400_when_item_has_invalid_curb_weight() throws Exception {
    var requestBody = """
      [{"bodyId":"%s","powertrainId":"%s","transmissionId":"%s","drivetrain":"FWD","curbWeightKg":0}]
      """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

    mockMvc.perform(post("/api/v1/variants/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isBadRequest());
  }

  @Test
  void removeMarket_returns_204() throws Exception {
    var id = UUID.randomUUID();

    mockMvc.perform(delete("/api/v1/variants/{id}/markets/{market}", id, "UA"))
      .andExpect(status().isNoContent());

    verify(variantService).removeMarket(new VariantId(id), "UA");
  }

  @Test
  void updateCurbWeight_returns_200() throws Exception {
    var bodyId = BodyId.generate();
    var powertrainId = PowertrainId.generate();
    var transmissionId = TransmissionId.generate();
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.FWD, 1400);
    when(variantService.updateCurbWeightKg(any(VariantId.class), eq(1400))).thenReturn(variant);

    var requestBody = """
      {"bodyId":"%s","powertrainId":"%s","transmissionId":"%s","drivetrain":"FWD","curbWeightKg":1400}
      """.formatted(bodyId.value(), powertrainId.value(), transmissionId.value());

    mockMvc.perform(patch("/api/v1/variants/{id}/weight", variant.id().value())
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.curbWeightKg").value(1400));
  }

  @Test
  void updateGroundClearance_returns_200() throws Exception {
    var bodyId = BodyId.generate();
    var powertrainId = PowertrainId.generate();
    var transmissionId = TransmissionId.generate();
    var variant = Variant.create(bodyId, powertrainId, transmissionId, Drivetrain.AWD, 1600);
    variant.setGroundClearanceMm(180);
    when(variantService.updateGroundClearanceMm(any(VariantId.class), eq(180))).thenReturn(variant);

    var requestBody = """
      {"bodyId":"%s","powertrainId":"%s","transmissionId":"%s","drivetrain":"AWD","curbWeightKg":1600,"groundClearanceMm":180}
      """.formatted(bodyId.value(), powertrainId.value(), transmissionId.value());

    mockMvc.perform(patch("/api/v1/variants/{id}/ground-clearance", variant.id().value())
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.groundClearanceMm").value(180));
  }
}
