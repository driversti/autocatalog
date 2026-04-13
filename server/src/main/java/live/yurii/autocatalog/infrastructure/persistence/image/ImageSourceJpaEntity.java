package live.yurii.autocatalog.infrastructure.persistence.image;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "image_sources")
public class ImageSourceJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "image_url", nullable = false, unique = true)
  private String imageUrl;

  @Column(name = "source_url")
  private String sourceUrl;

  @Column(length = 255)
  private String author;

  @Column(nullable = false, length = 100)
  private String license;

  @Column(name = "license_url")
  private String licenseUrl;

  @Column(length = 500)
  private String copyright;

  @Column(name = "usage_terms")
  private String usageTerms;

  @Column(name = "obtained_at", nullable = false)
  private LocalDateTime obtainedAt;

  protected ImageSourceJpaEntity() {
  }

  public ImageSourceJpaEntity(String imageUrl, String sourceUrl, String author,
                              String license, String licenseUrl, String copyright,
                              String usageTerms) {
    this.imageUrl = imageUrl;
    this.sourceUrl = sourceUrl;
    this.author = author;
    this.license = license;
    this.licenseUrl = licenseUrl;
    this.copyright = copyright;
    this.usageTerms = usageTerms;
    this.obtainedAt = LocalDateTime.now();
  }

  public Long id() {
    return id;
  }

  public String imageUrl() {
    return imageUrl;
  }

  public String sourceUrl() {
    return sourceUrl;
  }

  public String author() {
    return author;
  }

  public String license() {
    return license;
  }

  public String licenseUrl() {
    return licenseUrl;
  }

  public String copyright() {
    return copyright;
  }

  public String usageTerms() {
    return usageTerms;
  }

  public LocalDateTime obtainedAt() {
    return obtainedAt;
  }

  public void updateMetadata(String author, String license, String licenseUrl,
                             String copyright, String usageTerms) {
    if (author != null) this.author = author;
    if (license != null) this.license = license;
    if (licenseUrl != null) this.licenseUrl = licenseUrl;
    if (copyright != null) this.copyright = copyright;
    if (usageTerms != null) this.usageTerms = usageTerms;
  }
}
