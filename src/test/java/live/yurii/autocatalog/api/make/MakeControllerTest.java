package live.yurii.autocatalog.api.make;

import live.yurii.autocatalog.api.shared.GlobalExceptionHandler;
import live.yurii.autocatalog.application.make.MakeService;
import live.yurii.autocatalog.application.shared.EntityNotFoundException;
import live.yurii.autocatalog.domain.make.Make;
import live.yurii.autocatalog.domain.make.MakeId;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MakeController.class)
@Import(GlobalExceptionHandler.class)
class MakeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MakeService makeService;

  @Test
  void getAll_returns_json_list() throws Exception {
    var bmw = Make.create("BMW", "Germany");
    var audi = Make.create("Audi", "Germany");
    when(makeService.getAll()).thenReturn(List.of(bmw, audi));

    mockMvc.perform(get("/api/v1/makes"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].name").value("BMW"))
      .andExpect(jsonPath("$[0].slug").value("bmw"))
      .andExpect(jsonPath("$[1].name").value("Audi"));
  }

  @Test
  void getAll_returns_empty_array_when_no_makes() throws Exception {
    when(makeService.getAll()).thenReturn(List.of());

    mockMvc.perform(get("/api/v1/makes"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void getById_returns_make() throws Exception {
    var make = Make.create("BMW", "Germany");
    when(makeService.getById(any(MakeId.class))).thenReturn(make);

    mockMvc.perform(get("/api/v1/makes/{id}", make.id().value()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(make.id().value().toString()))
      .andExpect(jsonPath("$.name").value("BMW"))
      .andExpect(jsonPath("$.country").value("Germany"));
  }

  @Test
  void getById_returns_404_when_not_found() throws Exception {
    when(makeService.getById(any(MakeId.class)))
      .thenThrow(new EntityNotFoundException("Make not found"));

    mockMvc.perform(get("/api/v1/makes/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.detail").value("Make not found"));
  }

  @Test
  void getBySlug_returns_make() throws Exception {
    var make = Make.create("BMW", "Germany");
    when(makeService.getBySlug("bmw")).thenReturn(make);

    mockMvc.perform(get("/api/v1/makes/slug/{slug}", "bmw"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.slug").value("bmw"));
  }

  @Test
  void getBySlug_returns_404_when_not_found() throws Exception {
    when(makeService.getBySlug("missing"))
      .thenThrow(new EntityNotFoundException("Make not found: missing"));

    mockMvc.perform(get("/api/v1/makes/slug/{slug}", "missing"))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_returns_201_with_location_and_body() throws Exception {
    var made = Make.create("BMW", "Germany");
    when(makeService.create("BMW", "Germany")).thenReturn(made);

    var body = "{\"name\":\"BMW\",\"country\":\"Germany\"}";

    mockMvc.perform(post("/api/v1/makes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location",
        "http://localhost/api/v1/makes/" + made.id().value()))
      .andExpect(jsonPath("$.name").value("BMW"))
      .andExpect(jsonPath("$.country").value("Germany"))
      .andExpect(jsonPath("$.slug").value("bmw"));
  }

  @Test
  void create_returns_400_when_name_blank() throws Exception {
    var body = "{\"name\":\"\",\"country\":\"Germany\"}";

    mockMvc.perform(post("/api/v1/makes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.detail").value("Validation failed"))
      .andExpect(jsonPath("$.errors").isArray());
  }

  @Test
  void create_returns_400_when_country_missing() throws Exception {
    var body = "{\"name\":\"BMW\"}";

    mockMvc.perform(post("/api/v1/makes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_returns_409_when_duplicate() throws Exception {
    when(makeService.create("BMW", "Germany"))
      .thenThrow(new IllegalStateException("Make already exists: BMW"));
    var body = "{\"name\":\"BMW\",\"country\":\"Germany\"}";

    mockMvc.perform(post("/api/v1/makes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.detail").value("Make already exists: BMW"));
  }

  @Test
  void rename_returns_200_with_updated_body() throws Exception {
    var make = Make.create("BMW", "Germany");
    when(makeService.rename(any(MakeId.class), eq("BMW Group"))).thenAnswer(invocation -> {
      make.rename("BMW Group");
      return make;
    });
    var body = "{\"name\":\"BMW Group\",\"country\":\"Germany\"}";

    mockMvc.perform(patch("/api/v1/makes/{id}/name", make.id().value())
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("BMW Group"))
      .andExpect(jsonPath("$.slug").value("bmw-group"));

    verify(makeService).rename(any(MakeId.class), eq("BMW Group"));
  }

  @Test
  void rename_returns_404_when_make_missing() throws Exception {
    when(makeService.rename(any(MakeId.class), any()))
      .thenThrow(new EntityNotFoundException("Make not found"));
    var body = "{\"name\":\"NewName\",\"country\":\"Germany\"}";

    mockMvc.perform(patch("/api/v1/makes/{id}/name", UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isNotFound());
  }
}
