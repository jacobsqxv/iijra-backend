package dev.aries.iijra.module.department;

import java.util.List;

import dev.aries.iijra.constant.ExceptionConstant;
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
	private final DepartmentStaff deptStaff;
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
		return DepartmentResponse.newResponse(newDept);
	}

	private void validateDepartmentName(@NonNull String name) {
		if (departmentRepo.existsByName(name)) {
			throw new EntityExistsException(ExceptionConstant.DEPT_NAME_ALREADY_EXISTS + name);
		}
	}

	@Transactional(readOnly = true)
	public List<DepartmentResponse> getAllDepartments() {
		return departmentRepo.findByIsArchivedFalse().stream()
				.map(d -> {
					DepartmentResponse.DepartmentStaff staff = deptStaff.getDepartmentStaff(d.getId());
					return DepartmentResponse.basicResponse(d, staff);
				})
				.toList();
	}

	@Transactional(readOnly = true)
	public DepartmentResponse getDepartment(Long id) {
		DepartmentResponse.DepartmentStaff staff = deptStaff.getDepartmentStaff(id);
		return DepartmentResponse.fullResponse(getDepartmentById(id), staff);
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

	@Transactional
	public DepartmentResponse updateDepartmentInfo(@NonNull Long id, @Valid DepartmentRequest request) {
		Department department = getDepartmentById(id);
		validateDepartmentName(request.name());
		department.setName(request.name());
		departmentRepo.save(department);
		return DepartmentResponse.newResponse(department);
	}
}
