package dev.aries.iijra.module.user;

import java.time.LocalDateTime;
import java.util.Optional;

import dev.aries.iijra.enums.Role;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	@EntityGraph(attributePaths = {"staff", "admin"}, type = EntityGraph.EntityGraphType.LOAD)
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByRole(Role role);

	int deleteByIsArchivedTrueAndArchivedAtBefore(LocalDateTime cutOff);
}
