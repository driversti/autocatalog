package live.yurii.autocatalog.api.image;

import live.yurii.autocatalog.api.shared.GlobalExceptionHandler;
import live.yurii.autocatalog.application.image.ImageSourceService;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageSourceController.class)
@Import(GlobalExceptionHandler.class)
class ImageSourceControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ImageSourceService imageSourceService;

  @Test
  void getAll_returns_paginated_list() throws Exception {
    var response = new ImageSourceResponse(
      1L, "https://example.com/img.jpg", "https://source.com", "Author",
      "CC BY 4.0", "https://license.url", "Copyright 2024", "Editorial use",
      LocalDateTime.now()
    );
    when(imageSourceService.findAll(any()))
      .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

    mockMvc.perform(get("/api/v1/image-sources").param("page", "0").param("size", "20"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content[0].imageUrl").value("https://example.com/img.jpg"))
      .andExpect(jsonPath("$.content[0].author").value("Author"))
      .andExpect(jsonPath("$.content[0].license").value("CC BY 4.0"));
  }

  @Test
  void getByUrl_returns_source_when_found() throws Exception {
    var response = new ImageSourceResponse(
      1L, "https://example.com/img.jpg", null, "Author",
      "CC0", null, null, null, LocalDateTime.now()
    );
    when(imageSourceService.getByImageUrl("https://example.com/img.jpg"))
      .thenReturn(response);

    mockMvc.perform(get("/api/v1/image-sources/by-url")
        .param("imageUrl", "https://example.com/img.jpg"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.imageUrl").value("https://example.com/img.jpg"))
      .andExpect(jsonPath("$.license").value("CC0"));
  }

  @Test
  void getByUrl_returns_404_when_not_found() throws Exception {
    when(imageSourceService.getByImageUrl("https://missing.com/img.jpg"))
      .thenThrow(new EntityNotFoundException("Image source not found"));

    mockMvc.perform(get("/api/v1/image-sources/by-url")
        .param("imageUrl", "https://missing.com/img.jpg"))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_returns_201_with_body() throws Exception {
    var response = new ImageSourceResponse(
      1L, "https://example.com/img.jpg", "https://source.com", "Author",
      "CC BY 4.0", "https://license.url", "Copyright 2024", "Editorial use",
      LocalDateTime.now()
    );
    when(imageSourceService.save(any())).thenReturn(response);

    var body = """
      {"imageUrl":"https://example.com/img.jpg","sourceUrl":"https://source.com",
       "author":"Author","license":"CC BY 4.0","licenseUrl":"https://license.url",
       "copyright":"Copyright 2024","usageTerms":"Editorial use"}""";

    mockMvc.perform(post("/api/v1/image-sources")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.imageUrl").value("https://example.com/img.jpg"))
      .andExpect(jsonPath("$.license").value("CC BY 4.0"));
  }

  @Test
  void create_returns_400_when_imageUrl_blank() throws Exception {
    var body = """
      {"imageUrl":"","license":"CC0"}""";

    mockMvc.perform(post("/api/v1/image-sources")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_returns_400_when_license_missing() throws Exception {
    var body = """
      {"imageUrl":"https://example.com/img.jpg"}""";

    mockMvc.perform(post("/api/v1/image-sources")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void getById_returns_200() throws Exception {
    var response = new ImageSourceResponse(
      42L, "https://example.com/img.jpg", "https://source.com", "Author",
      "CC BY 4.0", "https://license.url", "Copyright 2024", "Editorial use",
      LocalDateTime.now()
    );
    when(imageSourceService.getById(42L)).thenReturn(response);

    mockMvc.perform(get("/api/v1/image-sources/{id}", 42L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(42))
      .andExpect(jsonPath("$.imageUrl").value("https://example.com/img.jpg"))
      .andExpect(jsonPath("$.license").value("CC BY 4.0"));
  }

  @Test
  void getById_returns_404_when_not_found() throws Exception {
    when(imageSourceService.getById(99L))
      .thenThrow(new EntityNotFoundException("ImageSource not found: 99"));

    mockMvc.perform(get("/api/v1/image-sources/{id}", 99L))
      .andExpect(status().isNotFound());
  }

  @Test
  void deleteById_returns_204() throws Exception {
    mockMvc.perform(delete("/api/v1/image-sources/{id}", 1L))
      .andExpect(status().isNoContent());

    verify(imageSourceService).deleteById(1L);
  }

  @Test
  void deleteById_returns_404_when_not_found() throws Exception {
    doThrow(new EntityNotFoundException("ImageSource not found: 99"))
      .when(imageSourceService).deleteById(99L);

    mockMvc.perform(delete("/api/v1/image-sources/{id}", 99L))
      .andExpect(status().isNotFound());
  }

  @Test
  void update_returns_200() throws Exception {
    var response = new ImageSourceResponse(
      1L, "https://example.com/img.jpg", "https://source.com", "New Author",
      "CC0", null, null, null, LocalDateTime.now()
    );
    when(imageSourceService.update(eq(1L), any())).thenReturn(response);

    var body = """
      {"imageUrl":"https://example.com/img.jpg","license":"CC0","author":"New Author"}""";

    mockMvc.perform(patch("/api/v1/image-sources/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.author").value("New Author"))
      .andExpect(jsonPath("$.license").value("CC0"));
  }
}
