package live.yurii.autocatalog.api.transmission;

import live.yurii.autocatalog.api.shared.GlobalExceptionHandler;
import live.yurii.autocatalog.application.shared.EntityNotFoundException;
import live.yurii.autocatalog.application.transmission.TransmissionService;
import live.yurii.autocatalog.domain.transmission.Transmission;
import live.yurii.autocatalog.domain.transmission.TransmissionId;
import live.yurii.autocatalog.domain.transmission.TransmissionType;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransmissionController.class)
@Import(GlobalExceptionHandler.class)
class TransmissionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private TransmissionService transmissionService;

  @Test
  void getAll_without_filter_delegates_to_getAll() throws Exception {
    when(transmissionService.getAll()).thenReturn(List.of(
      Transmission.create(TransmissionType.MANUAL, 6),
      Transmission.create(TransmissionType.AUTOMATIC, 8)
    ));

    mockMvc.perform(get("/api/v1/transmissions"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].type").value("MANUAL"))
      .andExpect(jsonPath("$[0].gearCount").value(6))
      .andExpect(jsonPath("$[1].type").value("AUTOMATIC"));

    verify(transmissionService).getAll();
  }

  @Test
  void getAll_with_type_filter_delegates_to_getByType() throws Exception {
    when(transmissionService.getByType(TransmissionType.DCT)).thenReturn(List.of(
      Transmission.create(TransmissionType.DCT, 7)
    ));

    mockMvc.perform(get("/api/v1/transmissions").param("type", "DCT"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].type").value("DCT"));

    verify(transmissionService).getByType(TransmissionType.DCT);
  }

  @Test
  void getAll_with_invalid_type_returns_400() throws Exception {
    mockMvc.perform(get("/api/v1/transmissions").param("type", "FOO"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void getById_returns_transmission() throws Exception {
    var t = Transmission.create(TransmissionType.MANUAL, 6);
    when(transmissionService.getById(any(TransmissionId.class))).thenReturn(t);

    mockMvc.perform(get("/api/v1/transmissions/{id}", t.id().value()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(t.id().value().toString()))
      .andExpect(jsonPath("$.gearCount").value(6));
  }

  @Test
  void getById_returns_404_when_not_found() throws Exception {
    when(transmissionService.getById(any(TransmissionId.class)))
      .thenThrow(new EntityNotFoundException("Transmission not found"));

    mockMvc.perform(get("/api/v1/transmissions/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_returns_201_with_location() throws Exception {
    var t = Transmission.create(TransmissionType.DCT, 7);
    when(transmissionService.create(TransmissionType.DCT, 7)).thenReturn(t);
    var body = "{\"type\":\"DCT\",\"gearCount\":7}";

    mockMvc.perform(post("/api/v1/transmissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location",
        "http://localhost/api/v1/transmissions/" + t.id().value()))
      .andExpect(jsonPath("$.type").value("DCT"))
      .andExpect(jsonPath("$.gearCount").value(7));
  }

  @Test
  void create_returns_400_when_gear_count_out_of_range() throws Exception {
    var body = "{\"type\":\"MANUAL\",\"gearCount\":20}";

    mockMvc.perform(post("/api/v1/transmissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_returns_400_when_gear_count_below_one() throws Exception {
    var body = "{\"type\":\"MANUAL\",\"gearCount\":0}";

    mockMvc.perform(post("/api/v1/transmissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_returns_400_when_type_null() throws Exception {
    var body = "{\"gearCount\":6}";

    mockMvc.perform(post("/api/v1/transmissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }
}
