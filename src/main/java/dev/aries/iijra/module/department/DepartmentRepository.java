package dev.aries.iijra.module.department;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
	boolean existsByName(String name);

	@EntityGraph(attributePaths = {"hod", "staff"})
	List<Department> findByIsArchivedFalse();

	int deleteByIsArchivedTrueAndArchivedAtBefore(LocalDateTime cutOff);
}
