package dev.aries.iijra.module.staff;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.Role;
import dev.aries.iijra.enums.Status;
import dev.aries.iijra.module.department.Department;
import dev.aries.iijra.module.department.DepartmentService;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.module.user.UserService;
import dev.aries.iijra.search.GetStaffPage;
import dev.aries.iijra.utility.Checks;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffService {
	private final StaffRepository staffRepo;
	private final UserService userService;
	private final DepartmentService departmentService;

	/**
	 * Adds a new staff member to the system.
	 *
	 * @param request the request object containing the details of the new staff member
	 * @return a response object containing the details of the newly added staff member
	 * @throws EntityExistsException if a staff member with the given email already exists
	 */
	@Transactional
	public StaffResponse addNewStaff(StaffRequest request) {

		Department department = departmentService.getDepartmentById(request.departmentId());

		Staff newStaff = createStaff(request, department);
		staffRepo.save(newStaff);
		assignDepartmentPosition(newStaff, request.isHod());

		return StaffResponse.fullResponse(newStaff);
	}


	private Staff createStaff(StaffRequest request, Department department) {
		User newUser = userService.createUser(request.email(), Role.STAFF);
		String staffId = formatStaffId(newUser.getId());
		return new Staff(
				staffId,
				request.fullName(),
				newUser,
				department);
	}

	/**
	 * Assigns a department position to a staff member.
	 *
	 * @param staff the staff member to be assigned a position
	 * @param isHod flag indicating if the staff member is to be assigned as Head of Department (HOD)
	 */
	private void assignDepartmentPosition(Staff staff, Boolean isHod) {
		if (Boolean.TRUE.equals(isHod)) {
			departmentService.assignHod(staff, false);
		} else {
			departmentService.addStaffMember(staff);
		}
	}

	@Transactional(readOnly = true)
	public Page<StaffResponse> getAllStaff(GetStaffPage request, Pageable pageable) {
		if (request.status() != null) {
			Checks.checkIfEnumExists(Status.class, request.status());
		}
		return staffRepo.findAll(request, pageable)
				.map(StaffResponse::fullResponse);
	}

	private String formatStaffId(Long id) {
		return String.format("ST%04d", id);
	}

	public StaffResponse getStaffById(Long id) {
		String staffId = formatStaffId(id);
		return StaffResponse.fullResponse(getStaff(staffId));
	}

	private Staff getStaff(String staffId) {
		return staffRepo.findById(staffId)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionConstant.STAFF_ID_DOESNT_EXIST + staffId));
	}
}
