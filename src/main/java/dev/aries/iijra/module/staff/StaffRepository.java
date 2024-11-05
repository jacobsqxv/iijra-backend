package dev.aries.iijra.module.staff;

import java.util.List;
import java.util.Optional;

import dev.aries.iijra.search.GetStaffPage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StaffRepository extends JpaRepository<Staff, String>, JpaSpecificationExecutor<Staff> {

	@EntityGraph(attributePaths = {"department", "user"})
	default Page<Staff> findAll(GetStaffPage request, Pageable pageable) {
		return findAll(StaffSpecification.buildSpecification(request, false), pageable);
	}
	@EntityGraph(attributePaths = {"department", "user"})
	Optional<Staff> findByDepartmentIdAndIsHodTrue(Long departmentId);

	@EntityGraph(attributePaths = {"department", "user"}, type = EntityGraph.EntityGraphType.LOAD)
	List<Staff> findAllByDepartmentId(Long departmentId);

	@EntityGraph(attributePaths = {"department", "user"}, type = EntityGraph.EntityGraphType.LOAD)
	Optional<Staff> findByUser_Email(String email);
}
