package live.yurii.autocatalog.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImageSourceE2ETest extends AbstractE2ETest {

  @Test
  void create_and_fetch_by_url() throws Exception {
    var body = """
      {"imageUrl":"https://example.com/car.jpg","sourceUrl":"https://commons.wikimedia.org/car",
       "author":"Wiki User","license":"CC BY-SA 4.0",
       "licenseUrl":"https://creativecommons.org/licenses/by-sa/4.0/",
       "copyright":"Copyright 2024 Wiki User","usageTerms":"Attribution required"}""";

    mockMvc.perform(post("/api/v1/image-sources")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.imageUrl").value("https://example.com/car.jpg"))
      .andExpect(jsonPath("$.license").value("CC BY-SA 4.0"))
      .andExpect(jsonPath("$.obtainedAt").isNotEmpty());

    mockMvc.perform(get("/api/v1/image-sources/by-url")
        .param("imageUrl", "https://example.com/car.jpg"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.author").value("Wiki User"))
      .andExpect(jsonPath("$.copyright").value("Copyright 2024 Wiki User"));
  }

  @Test
  void fetch_missing_url_returns_404() throws Exception {
    mockMvc.perform(get("/api/v1/image-sources/by-url")
        .param("imageUrl", "https://nonexistent.com/img.jpg"))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_with_minimal_fields() throws Exception {
    var body = """
      {"imageUrl":"https://example.com/minimal.jpg","license":"CC0"}""";

    mockMvc.perform(post("/api/v1/image-sources")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.imageUrl").value("https://example.com/minimal.jpg"))
      .andExpect(jsonPath("$.license").value("CC0"))
      .andExpect(jsonPath("$.author").isEmpty());
  }

  @Test
  void create_duplicate_url_returns_409() throws Exception {
    var body = """
      {"imageUrl":"https://example.com/dup.jpg","license":"CC0"}""";

    mockMvc.perform(post("/api/v1/image-sources")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated());

    mockMvc.perform(post("/api/v1/image-sources")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isConflict());
  }

  @Test
  void get_all_returns_paginated_results() throws Exception {
    for (int i = 0; i < 3; i++) {
      mockMvc.perform(post("/api/v1/image-sources")
          .contentType(MediaType.APPLICATION_JSON)
          .content("{\"imageUrl\":\"https://example.com/page-" + i + ".jpg\",\"license\":\"CC0\"}"))
        .andExpect(status().isCreated());
    }

    mockMvc.perform(get("/api/v1/image-sources").param("page", "0").param("size", "2"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content.length()").value(2))
      .andExpect(jsonPath("$.totalElements").value(3))
      .andExpect(jsonPath("$.totalPages").value(2));
  }

  @Test
  void create_without_license_returns_400() throws Exception {
    mockMvc.perform(post("/api/v1/image-sources")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"imageUrl\":\"https://example.com/nolicense.jpg\"}"))
      .andExpect(status().isBadRequest());
  }
}
