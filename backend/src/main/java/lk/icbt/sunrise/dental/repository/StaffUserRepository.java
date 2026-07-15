package lk.icbt.sunrise.dental.repository;

import lk.icbt.sunrise.dental.domain.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffUserRepository extends JpaRepository<StaffUser, Long> {
    Optional<StaffUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
