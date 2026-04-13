package live.yurii.autocatalog.infrastructure.persistence.powerunit;

import org.springframework.data.jpa.repository.JpaRepository;

// Public visibility required: PowertrainRepositoryAdapter in the powertrain package
// needs to load PowerUnitJpaEntity references for join-table wiring.
public interface PowerUnitJpaRepository extends JpaRepository<PowerUnitJpaEntity, Long> {
}
