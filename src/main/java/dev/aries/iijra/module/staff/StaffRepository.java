package dev.aries.iijra.module.staff;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {
	Optional<Staff> findByEmail(String email);

	boolean existsByEmail(String email);
}
