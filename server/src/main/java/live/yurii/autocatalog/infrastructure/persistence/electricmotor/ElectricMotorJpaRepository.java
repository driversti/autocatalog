package live.yurii.autocatalog.infrastructure.persistence.electricmotor;

import org.springframework.data.jpa.repository.JpaRepository;

interface ElectricMotorJpaRepository extends JpaRepository<ElectricMotorJpaEntity, Long> {
}
