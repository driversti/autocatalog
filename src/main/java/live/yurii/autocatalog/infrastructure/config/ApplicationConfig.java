package live.yurii.autocatalog.infrastructure.config;

import live.yurii.autocatalog.application.body.BodyService;
import live.yurii.autocatalog.application.engine.EngineService;
import live.yurii.autocatalog.application.generation.GenerationService;
import live.yurii.autocatalog.application.make.MakeService;
import live.yurii.autocatalog.application.model.CarModelService;
import live.yurii.autocatalog.application.transmission.TransmissionService;
import live.yurii.autocatalog.application.variant.VariantService;
import live.yurii.autocatalog.domain.body.BodyRepository;
import live.yurii.autocatalog.domain.engine.EngineRepository;
import live.yurii.autocatalog.domain.generation.GenerationRepository;
import live.yurii.autocatalog.domain.make.MakeRepository;
import live.yurii.autocatalog.domain.model.CarModelRepository;
import live.yurii.autocatalog.domain.transmission.TransmissionRepository;
import live.yurii.autocatalog.domain.variant.VariantRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  @Bean
  public MakeService makeService(MakeRepository makeRepository) {
    return new MakeService(makeRepository);
  }

  @Bean
  public CarModelService carModelService(CarModelRepository modelRepository,
                                         MakeRepository makeRepository) {
    return new CarModelService(modelRepository, makeRepository);
  }

  @Bean
  public EngineService engineService(EngineRepository engineRepository) {
    return new EngineService(engineRepository);
  }

  @Bean
  public TransmissionService transmissionService(TransmissionRepository transmissionRepository) {
    return new TransmissionService(transmissionRepository);
  }

  @Bean
  public GenerationService generationService(GenerationRepository generationRepository,
                                             CarModelRepository modelRepository) {
    return new GenerationService(generationRepository, modelRepository);
  }

  @Bean
  public BodyService bodyService(BodyRepository bodyRepository,
                                 GenerationRepository generationRepository) {
    return new BodyService(bodyRepository, generationRepository);
  }

  @Bean
  public VariantService variantService(VariantRepository variantRepository,
                                       BodyRepository bodyRepository,
                                       EngineRepository engineRepository,
                                       TransmissionRepository transmissionRepository) {
    return new VariantService(variantRepository, bodyRepository, engineRepository, transmissionRepository);
  }
}
