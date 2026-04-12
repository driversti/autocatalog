package live.yurii.autocatalog.api.model;

import live.yurii.autocatalog.api.shared.GlobalExceptionHandler;
import live.yurii.autocatalog.application.model.CarModelService;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.make.MakeId;
import live.yurii.autocatalog.domain.model.CarModel;
import live.yurii.autocatalog.domain.model.ModelId;
import live.yurii.autocatalog.domain.model.ModelRelation;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarModelController.class)
@Import(GlobalExceptionHandler.class)
class CarModelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CarModelService carModelService;

  @Test
  void getByMake_returns_models() throws Exception {
    var makeId = MakeId.generate();
    var m1 = CarModel.create(makeId, "3 Series");
    var m2 = CarModel.create(makeId, "5 Series");
    when(carModelService.getByMake(any(MakeId.class))).thenReturn(List.of(m1, m2));

    mockMvc.perform(get("/api/v1/models").param("makeId", makeId.value().toString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].name").value("3 Series"))
      .andExpect(jsonPath("$[1].name").value("5 Series"));
  }

  @Test
  void getByMake_requires_makeId_query_param() throws Exception {
    mockMvc.perform(get("/api/v1/models"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void getById_returns_model_with_relations() throws Exception {
    var makeId = MakeId.generate();
    var model = CarModel.create(makeId, "E90");
    var targetId = ModelId.generate();
    model.addRelation(targetId, ModelRelation.Type.SUCCESSOR, "replaced by F30");
    when(carModelService.getById(any(ModelId.class))).thenReturn(model);

    mockMvc.perform(get("/api/v1/models/{id}", model.id().value()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("E90"))
      .andExpect(jsonPath("$.relations.length()").value(1))
      .andExpect(jsonPath("$.relations[0].targetModelId").value(targetId.value().toString()))
      .andExpect(jsonPath("$.relations[0].type").value("SUCCESSOR"))
      .andExpect(jsonPath("$.relations[0].note").value("replaced by F30"));
  }

  @Test
  void getById_returns_404_when_not_found() throws Exception {
    when(carModelService.getById(any(ModelId.class)))
      .thenThrow(new EntityNotFoundException("Model not found"));

    mockMvc.perform(get("/api/v1/models/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_returns_201_with_location() throws Exception {
    var makeId = MakeId.generate();
    var model = CarModel.create(makeId, "3 Series");
    when(carModelService.create(any(MakeId.class), eq("3 Series"))).thenReturn(model);

    var body = "{\"makeId\":\"" + makeId.value() + "\",\"name\":\"3 Series\"}";

    mockMvc.perform(post("/api/v1/models")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location",
        "http://localhost/api/v1/models/" + model.id().value()))
      .andExpect(jsonPath("$.name").value("3 Series"))
      .andExpect(jsonPath("$.slug").value("3-series"));
  }

  @Test
  void create_returns_400_when_make_id_null() throws Exception {
    var body = "{\"name\":\"3 Series\"}";

    mockMvc.perform(post("/api/v1/models")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_returns_400_when_name_blank() throws Exception {
    var body = "{\"makeId\":\"" + UUID.randomUUID() + "\",\"name\":\"\"}";

    mockMvc.perform(post("/api/v1/models")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_returns_404_when_make_not_found() throws Exception {
    when(carModelService.create(any(MakeId.class), any()))
      .thenThrow(new EntityNotFoundException("Make not found"));
    var body = "{\"makeId\":\"" + UUID.randomUUID() + "\",\"name\":\"X\"}";

    mockMvc.perform(post("/api/v1/models")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isNotFound());
  }

  @Test
  void addRelation_links_models_and_returns_200() throws Exception {
    var makeId = MakeId.generate();
    var from = CarModel.create(makeId, "E90");
    var targetId = ModelId.generate();
    from.addRelation(targetId, ModelRelation.Type.SUCCESSOR, "note");
    when(carModelService.linkRelation(any(ModelId.class), any(ModelId.class),
      eq(ModelRelation.Type.SUCCESSOR), eq("note"))).thenReturn(from);

    var body = "{\"targetModelId\":\"" + targetId.value() + "\",\"type\":\"SUCCESSOR\",\"note\":\"note\"}";

    mockMvc.perform(post("/api/v1/models/{id}/relations", from.id().value())
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.relations.length()").value(1));
  }

  @Test
  void addRelation_returns_400_when_target_is_null() throws Exception {
    var body = "{\"type\":\"SUCCESSOR\"}";

    mockMvc.perform(post("/api/v1/models/{id}/relations", UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void addRelation_returns_409_when_duplicate() throws Exception {
    when(carModelService.linkRelation(any(), any(), any(), any()))
      .thenThrow(new IllegalStateException("Relation already exists"));
    var body = "{\"targetModelId\":\"" + UUID.randomUUID() + "\",\"type\":\"PLATFORM_SIBLING\"}";

    mockMvc.perform(post("/api/v1/models/{id}/relations", UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isConflict());
  }

  @Test
  void addRelation_returns_400_when_self_reference() throws Exception {
    when(carModelService.linkRelation(any(), any(), any(), any()))
      .thenThrow(new IllegalArgumentException("Model cannot relate to itself"));
    var body = "{\"targetModelId\":\"" + UUID.randomUUID() + "\",\"type\":\"SUCCESSOR\"}";

    mockMvc.perform(post("/api/v1/models/{id}/relations", UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }
}
