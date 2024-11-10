package dev.aries.iijra.module.department;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.module.staff.StaffResponse;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DepartmentResponse(
		Long id,
		String name,
		String hod,
		Integer staff,
		StaffResponse hodInfo,
		List<StaffResponse> staffInfo,
		LocalDateTime createdAt
) {

	public static final String NOT_ASSIGNED = "Not assigned";

	public static DepartmentResponse fullResponse(Department dept, DepartmentStaff deptStaff) {
		return DepartmentResponse.builder()
				.id(dept.getId())
				.name(dept.getName())
				.hodInfo(deptStaff.hod != null ? StaffResponse.basicResponse(deptStaff.hod) : null)
				.staffInfo(!deptStaff.staff.isEmpty() ? getStaffResponseList(deptStaff.staff) : null)
				.createdAt(dept.getAuditing().getCreatedAt())
				.build();
	}

	private static List<StaffResponse> getStaffResponseList(List<Staff> staff) {
		return staff.stream().map(StaffResponse::basicResponse).toList();
	}

	public static DepartmentResponse basicResponse(Department dept, DepartmentStaff staff) {
		return DepartmentResponse.builder()
				.id(dept.getId())
				.name(dept.getName())
				.hod(staff.hod() != null ? staff.hod().getFullName() : NOT_ASSIGNED)
				.staff(staff.staff.size() + 1)
				.createdAt(dept.getAuditing().getCreatedAt())
				.build();
	}

	public static DepartmentResponse newResponse(Department dept) {
		return DepartmentResponse.builder()
				.id(dept.getId())
				.name(dept.getName())
				.hod(NOT_ASSIGNED)
				.staff(0)
				.createdAt(dept.getAuditing().getCreatedAt())
				.build();
	}

	public record DepartmentStaff(Staff hod, List<Staff> staff){}
}

