package dev.aries.iijra.module.department;

import java.util.List;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.Role;
import dev.aries.iijra.module.staff.Staff;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService {

	private final DepartmentRepository departmentRepo;

	/**
	 * Add a new department
	 *
	 * @param request {@link DepartmentRequest} containing department information
	 * @return {@link DepartmentResponse} containing saved department information
	 * @throws EntityExistsException when department with name already exists
	 */
	@Transactional
	public DepartmentResponse addNewDepartment(DepartmentRequest request) {
		validateDepartmentName(request.name());
		Department newDept = new Department(request.name());
		departmentRepo.save(newDept);
		log.info("New department added: {}", newDept.getId());
		return DepartmentResponse.basicResponse(newDept);
	}

	private void validateDepartmentName(@NonNull String name) {
		if (departmentRepo.existsByName(name)) {
			throw new EntityExistsException(ExceptionConstant.DEPT_NAME_ALREADY_EXISTS + name);
		}
	}

	@Transactional(readOnly = true)
	public List<DepartmentResponse> getAllDepartments() {
		return departmentRepo.findByIsArchivedFalse().stream()
				.map(DepartmentResponse::basicResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public DepartmentResponse getDepartment(Long id) {
		return DepartmentResponse.fullResponse(getDepartmentById(id));
	}

	@Transactional
	public String archiveDepartment(Long id) {
		Department dept = getDepartmentById(id);
		if (Boolean.TRUE.equals(dept.getIsArchived())) {
			throw new IllegalStateException(ExceptionConstant.DEPT_ALREADY_ARCHIVED + id);
		}
		dept.archive();
		departmentRepo.save(dept);
		return String.format("Department %s has been archived", dept.getName());
	}

	@Transactional
	public String restoreArchivedDepartment(Long id) {
		Department dept = getDepartmentById(id);
		if (Boolean.FALSE.equals(dept.getIsArchived())) {
			throw new IllegalStateException(ExceptionConstant.DEPT_NOT_ARCHIVED + id);
		}
		dept.restore();
		departmentRepo.save(dept);
		return String.format("Department %s has been restored", dept.getName());
	}

	public Department getDepartmentById(Long id) {
		return departmentRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionConstant.DEPT_ID_DOESNT_EXIST + id));
	}

	/**
	 * Assigns a Head of Department (HOD) to a department.
	 *
	 * @param staff    the staff member to be assigned as HOD
	 * @param isUpdate flag indicating if this is an update to the current HOD
	 * @throws IllegalStateException if the department already has a HOD and isUpdate is false
	 */
	@Transactional
	public void assignHod(Staff staff, Boolean isUpdate) {
		Department department = staff.getDepartment();
		Staff currentHod = department.getHod();

		if (currentHod != null && Boolean.FALSE.equals(isUpdate)) {
			throw new IllegalStateException(
					String.format("Department %s already has HOD: %s.",
							department.getName(),
							currentHod.getFullName())
			);
		}
		// If there's a current HOD and this is an update
		if (currentHod != null) {
			// Demote current HOD to regular staff
			currentHod.setIsHod(false);
			currentHod.getUser().setRole(Role.STAFF);
			// Add them to regular staff if not already there
			department.getStaff().add(currentHod);
			log.info("Previous HOD {} demoted to staff", currentHod.getFullName());
		}
		// Set new HOD
		staff.setIsHod(true);
		staff.getUser().setRole(Role.HOD);
		department.setHod(staff);

		departmentRepo.save(department);
		log.info("New HOD {} assigned to department {}",
				staff.getFullName(),
				department.getName());
	}

	/**
	 * Adds a staff member to their department.
	 *
	 * @param staff the staff member to be added to the department
	 */
	@Transactional
	public void addStaffMember(Staff staff) {
		Department department = staff.getDepartment();

		if (!department.getStaff().contains(staff)) {
			department.getStaff().add(staff);
			departmentRepo.save(department);
			log.info("Staff member {} added to department {}",
					staff.getFullName(),
					department.getName());
		}
	}

	@Transactional
	public DepartmentResponse updateDepartmentInfo(@NonNull Long id, @Valid DepartmentRequest request) {
		Department department = getDepartmentById(id);
		validateDepartmentName(request.name());
		department.setName(request.name());
		departmentRepo.save(department);
		return DepartmentResponse.basicResponse(department);
	}
}
