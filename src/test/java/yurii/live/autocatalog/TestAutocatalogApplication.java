package yurii.live.autocatalog;

import org.springframework.boot.SpringApplication;

public class TestAutocatalogApplication {

  public static void main(String[] args) {
    SpringApplication.from(AutocatalogApplication::main).with(TestcontainersConfiguration.class).run(args);
  }

}
