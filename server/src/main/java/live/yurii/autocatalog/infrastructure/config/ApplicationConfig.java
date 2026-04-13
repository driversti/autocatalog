package live.yurii.autocatalog.infrastructure.config;

import live.yurii.autocatalog.application.body.BodyService;
import live.yurii.autocatalog.application.electricmotor.ElectricMotorService;
import live.yurii.autocatalog.application.engine.EngineService;
import live.yurii.autocatalog.application.fuelcellstack.FuelCellStackService;
import live.yurii.autocatalog.application.image.ImageSourceService;
import live.yurii.autocatalog.application.generation.GenerationService;
import live.yurii.autocatalog.application.make.MakeService;
import live.yurii.autocatalog.application.model.CarModelService;
import live.yurii.autocatalog.application.powertrain.PowertrainService;
import live.yurii.autocatalog.application.transmission.TransmissionService;
import live.yurii.autocatalog.application.variant.VariantBatchService;
import live.yurii.autocatalog.application.variant.VariantService;
import live.yurii.autocatalog.domain.body.BodyRepository;
import live.yurii.autocatalog.domain.electricmotor.ElectricMotorRepository;
import live.yurii.autocatalog.domain.engine.EngineRepository;
import live.yurii.autocatalog.domain.fuelcellstack.FuelCellStackRepository;
import live.yurii.autocatalog.domain.generation.GenerationRepository;
import live.yurii.autocatalog.domain.make.MakeRepository;
import live.yurii.autocatalog.domain.model.CarModelRepository;
import live.yurii.autocatalog.domain.powertrain.PowertrainRepository;
import live.yurii.autocatalog.domain.powerunit.PowerUnitRepository;
import live.yurii.autocatalog.domain.transmission.TransmissionRepository;
import live.yurii.autocatalog.domain.variant.VariantRepository;
import live.yurii.autocatalog.infrastructure.persistence.image.ImageSourceJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  @Bean
  public MakeService makeService(MakeRepository makeRepository,
                                 CarModelRepository carModelRepository) {
    return new MakeService(makeRepository, carModelRepository);
  }

  @Bean
  public CarModelService carModelService(CarModelRepository modelRepository,
                                         MakeRepository makeRepository,
                                         GenerationRepository generationRepository) {
    return new CarModelService(modelRepository, makeRepository, generationRepository);
  }

  @Bean
  public EngineService engineService(EngineRepository engineRepository,
                                     PowertrainRepository powertrainRepository) {
    return new EngineService(engineRepository, powertrainRepository);
  }

  @Bean
  public ElectricMotorService electricMotorService(ElectricMotorRepository motorRepository,
                                                   PowertrainRepository powertrainRepository) {
    return new ElectricMotorService(motorRepository, powertrainRepository);
  }

  @Bean
  public FuelCellStackService fuelCellStackService(FuelCellStackRepository stackRepository,
                                                   PowertrainRepository powertrainRepository) {
    return new FuelCellStackService(stackRepository, powertrainRepository);
  }

  @Bean
  public TransmissionService transmissionService(TransmissionRepository transmissionRepository,
                                                 VariantRepository variantRepository) {
    return new TransmissionService(transmissionRepository, variantRepository);
  }

  @Bean
  public GenerationService generationService(GenerationRepository generationRepository,
                                             CarModelRepository modelRepository,
                                             BodyRepository bodyRepository) {
    return new GenerationService(generationRepository, modelRepository, bodyRepository);
  }

  @Bean
  public BodyService bodyService(BodyRepository bodyRepository,
                                 GenerationRepository generationRepository,
                                 VariantRepository variantRepository) {
    return new BodyService(bodyRepository, generationRepository, variantRepository);
  }

  @Bean
  public PowertrainService powertrainService(PowertrainRepository powertrainRepository,
                                             PowerUnitRepository powerUnitRepository,
                                             VariantRepository variantRepository) {
    return new PowertrainService(powertrainRepository, powerUnitRepository, variantRepository);
  }

  @Bean
  public VariantService variantService(VariantRepository variantRepository,
                                       BodyRepository bodyRepository,
                                       PowertrainRepository powertrainRepository,
                                       TransmissionRepository transmissionRepository) {
    return new VariantService(variantRepository, bodyRepository, powertrainRepository,
      transmissionRepository);
  }

  @Bean
  public VariantBatchService variantBatchService(VariantService variantService) {
    return new VariantBatchService(variantService);
  }

  @Bean
  public ImageSourceService imageSourceService(ImageSourceJpaRepository imageSourceJpaRepository) {
    return new ImageSourceService(imageSourceJpaRepository);
  }
}
