package dev.aries.iijra.module.staff;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StaffResponse(
		Long userId,
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
				staff.getUser().getId(),
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
				.userId(staff.getUser().getId())
				.staffId(staff.getId())
				.profileImage(staff.getProfileImage())
				.fullName(staff.getFullName())
				.build();
	}

	private static String returnPosition(Staff staff) {
		if (Boolean.TRUE.equals(staff.getHod())) {
			return "Head of Department";
		} else {
			return "Staff";
		}
	}
}
