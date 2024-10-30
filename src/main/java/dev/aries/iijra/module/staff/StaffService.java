package dev.aries.iijra.module.staff;

import java.util.List;
import java.util.Objects;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.Role;
import dev.aries.iijra.enums.Status;
import dev.aries.iijra.module.department.Department;
import dev.aries.iijra.module.department.DepartmentService;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.module.user.UserService;
import dev.aries.iijra.search.GetStaffPage;
import dev.aries.iijra.utility.Checks;
import dev.aries.iijra.utility.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffService {
	private static final String STAFF_ID_FORMAT = "ST%04d";
	private final StaffRepository staffRepo;
	private final UserService userService;
	private final DepartmentService departmentService;
	private final S3Service s3Service;
	@Value("${user.default-profile-image}")
	private String defaultProfileImage;

	/**
	 * Adds a new staff member to the system.
	 *
	 * @param request the request object containing the details of the new staff member
	 * @return a response object containing the details of the newly added staff member
	 * @throws jakarta.persistence.EntityExistsException if a staff member with the given email already exists
	 */
	@Transactional
	public StaffResponse addNewStaff(StaffRequest request) {

		Department department = departmentService.getDepartmentById(request.departmentId());

		Staff newStaff = createStaff(request, department);
		newStaff.setProfileImage(defaultProfileImage);

		if (Boolean.TRUE.equals(request.isHod())) {
			assignDepartmentPosition(newStaff);
		}
		staffRepo.save(newStaff);
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
	 * Check and assign a HOD position in a department to a staff member.
	 *
	 * @param staff the staff member to be assigned the position
	 */
	private void assignDepartmentPosition(Staff staff) {
		Staff departmentHod = getDepartmentHod(staff.getDepartment().getId());
		if (departmentHod != null) {
			throw new IllegalStateException(String.format("Department already has HOD: %s.",
					departmentHod.getFullName())
			);
		} else {
			staff.setIsHod(true);
			staff.getUser().setRole(Role.HOD);
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
		return String.format(STAFF_ID_FORMAT, id);
	}

	@Transactional(readOnly = true)
	public StaffResponse getStaffById(Long id) {
		return StaffResponse.fullResponse(getStaff(id));
	}

	private Staff getDepartmentHod(Long departmentId) {
		return staffRepo.findByDepartmentIdAndIsHodTrue(departmentId)
				.orElse(null);
	}
	private Staff getStaff(Long id) {
		String staffId = formatStaffId(id);
		return staffRepo.findById(staffId)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionConstant.STAFF_ID_DOESNT_EXIST + id));
	}

	public StaffResponse updateStaffInfo(Long id, StaffUpdateRequest request) {
		Staff staff = getStaff(id);

		Checks.updateField(staff::setFullName, staff.getFullName(), request.fullName());
		Checks.updateField(staff::setBio, staff.getBio(), request.bio());
		if (request.profileImage() != null) {
			String profileImage = s3Service.uploadFile(request.profileImage(), staff.getId());
			deletePreviousImage(staff.getProfileImage());
			Checks.updateField(staff::setProfileImage, staff.getProfileImage(), profileImage);
		}
		staffRepo.save(staff);
		return StaffResponse.fullResponse(staff);
	}

	private void deletePreviousImage(String oldImage) {
		if (oldImage != null && !Objects.equals(oldImage, defaultProfileImage)) {
			s3Service.deleteFile(oldImage);
			log.info("Old profile image has been removed");
		}
	}
}
