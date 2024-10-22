package dev.aries.iijra.module.department;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
	boolean existsByName(String name);
}
