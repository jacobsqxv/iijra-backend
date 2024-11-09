package dev.aries.iijra.module.staff;

import java.util.List;
import java.util.Optional;

import dev.aries.iijra.search.GetStaffPage;
import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StaffRepository extends JpaRepository<Staff, String>, JpaSpecificationExecutor<Staff> {

	@EntityGraph(value = "Staff.withDetails")
	default Page<Staff> findAll(GetStaffPage request, Pageable pageable) {
		return findAll(StaffSpecification.buildSpecification(request, false), pageable);
	}

	@EntityGraph(value = "Staff.withDetails", type = EntityGraph.EntityGraphType.LOAD)
	Optional<Staff> findByDepartmentIdAndIsHodTrue(Long departmentId);

	@EntityGraph(value = "Staff.withDetails", type = EntityGraph.EntityGraphType.LOAD)
	List<Staff> findAllByDepartmentId(Long departmentId);

	@EntityGraph(value = "Staff.withDetails")
	Optional<Staff> findByUser_Id(Long id);

	@EntityGraph(value = "Staff.withDetails")
	List<Staff> findAllByUser_IdIn(List<Long> userIds);

	@EntityGraph(value = "Staff.withDetails")
	@NonNull
	List<Staff> findAllById(@NonNull Iterable<String> ids);
}
