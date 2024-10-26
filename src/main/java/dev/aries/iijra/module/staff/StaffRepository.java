package dev.aries.iijra.module.staff;

import dev.aries.iijra.search.GetStaffPage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StaffRepository extends JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff> {

	@EntityGraph(attributePaths = {"department", "user"})
	default Page<Staff> findAll(GetStaffPage request, Pageable pageable) {
		return findAll(StaffSpecification.buildSpecification(request, false), pageable);
	}

}
