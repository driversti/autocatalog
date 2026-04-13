package live.yurii.autocatalog.application.image;

import live.yurii.autocatalog.api.image.ImageSourceRequest;
import live.yurii.autocatalog.api.image.ImageSourceResponse;
import live.yurii.autocatalog.domain.shared.EntityNotFoundException;
import live.yurii.autocatalog.infrastructure.persistence.image.ImageSourceJpaEntity;
import live.yurii.autocatalog.infrastructure.persistence.image.ImageSourceJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageSourceServiceTest {

  @Mock
  private ImageSourceJpaRepository repository;

  @InjectMocks
  private ImageSourceService service;

  @Test
  void getByImageUrl_returns_response_when_found() {
    var entity = new ImageSourceJpaEntity(
      "https://example.com/img.jpg", "https://source.com", "Author",
      "CC BY 4.0", "https://license.url", "Copyright 2024", "Editorial use"
    );
    when(repository.findByImageUrl("https://example.com/img.jpg"))
      .thenReturn(Optional.of(entity));

    var result = service.getByImageUrl("https://example.com/img.jpg");

    assertThat(result).isInstanceOf(ImageSourceResponse.class);
    assertThat(result.imageUrl()).isEqualTo("https://example.com/img.jpg");
    assertThat(result.author()).isEqualTo("Author");
  }

  @Test
  void getByImageUrl_throws_when_not_found() {
    when(repository.findByImageUrl("https://missing.com/img.jpg"))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getByImageUrl("https://missing.com/img.jpg"))
      .isInstanceOf(EntityNotFoundException.class)
      .hasMessageContaining("https://missing.com/img.jpg");
  }

  @Test
  void save_delegates_to_repository() {
    var request = new ImageSourceRequest(
      "https://example.com/img.jpg", null, null,
      "CC0", null, null, null
    );
    var entity = new ImageSourceJpaEntity(
      "https://example.com/img.jpg", null, null,
      "CC0", null, null, null
    );
    when(repository.save(any())).thenReturn(entity);

    var result = service.save(request);

    assertThat(result).isInstanceOf(ImageSourceResponse.class);
    assertThat(result.imageUrl()).isEqualTo("https://example.com/img.jpg");
    verify(repository).save(any());
  }

  @Test
  void findAll_returns_page_of_responses() {
    var entity = new ImageSourceJpaEntity(
      "https://example.com/img.jpg", null, null,
      "CC0", null, null, null
    );
    var pageable = PageRequest.of(0, 20);
    when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity)));

    Page<ImageSourceResponse> result = service.findAll(pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst()).isInstanceOf(ImageSourceResponse.class);
  }
}
