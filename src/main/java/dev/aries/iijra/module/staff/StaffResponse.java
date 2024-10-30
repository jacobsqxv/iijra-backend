package dev.aries.iijra.module.staff;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StaffResponse(
		String staffId,
		String profileImage,
		String fullName,
		String email,
		String bio,
		String department,
		String position,
		String status,
		LocalDateTime createdAt
) {
	public static StaffResponse fullResponse(Staff staff) {

		return new StaffResponse(
				staff.getId(),
				staff.getProfileImage(),
				staff.getFullName(),
				staff.getUser().getEmail(),
				staff.getBio(),
				staff.getDepartment().getName(),
				returnPosition(staff),
				staff.getUser().getStatus().name(),
				staff.getAuditing().getCreatedAt()
		);
	}

	public static StaffResponse basicResponse(Staff staff) {
		return StaffResponse.builder()
				.staffId(staff.getId())
				.profileImage(staff.getProfileImage())
				.fullName(staff.getFullName())
				.build();
	}

	private static String returnPosition(Staff staff) {
		if (Boolean.TRUE.equals(staff.getIsHod())) {
			return "Head of Department";
		} else {
			return "Staff";
		}
	}
}
