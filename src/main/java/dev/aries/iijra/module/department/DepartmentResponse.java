package dev.aries.iijra.module.department;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
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

	public static DepartmentResponse fullResponse(Department dept) {
		return DepartmentResponse.builder()
				.id(dept.getId())
				.name(dept.getName())
				.hodInfo(dept.getHod() != null ? StaffResponse.basicResponse(dept.getHod()) : null)
				.staffInfo(!dept.getStaff().isEmpty() ? getStaffResponseList(dept) : null)
				.createdAt(dept.getAuditing().getCreatedAt())
				.build();
	}

	private static List<StaffResponse> getStaffResponseList(Department dept) {
		return dept.getStaff().stream().map(StaffResponse::basicResponse).toList();
	}

	public static DepartmentResponse listResponse(Department dept) {
		return DepartmentResponse.builder()
				.id(dept.getId())
				.name(dept.getName())
				.hod(dept.getHod() != null ? dept.getHod().getFullName() : NOT_ASSIGNED)
				.staff(dept.getHod() != null ? dept.getStaff().size() + 1 : dept.getStaff().size())
				.createdAt(dept.getAuditing().getCreatedAt())
				.build();
	}

	public static DepartmentResponse newResponse(Department dept) {
		return DepartmentResponse.builder()
				.id(dept.getId())
				.name(dept.getName())
				.hod(NOT_ASSIGNED)
				.staff(dept.getStaff().size())
				.createdAt(dept.getAuditing().getCreatedAt())
				.build();
	}
}
