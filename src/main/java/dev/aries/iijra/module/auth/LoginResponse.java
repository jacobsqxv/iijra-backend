package dev.aries.iijra.module.auth;

import dev.aries.iijra.module.staff.Staff;

public record LoginResponse(
		String token,
		String staffId,
		String fullName,
		String email,
		String role,
		String status
) {
	public static LoginResponse newResponse(Staff staff, String token) {
		return new LoginResponse(
				token,
				staff.getProfile().getId(),
				staff.getProfile().getFullName(),
				staff.getEmail(),
				staff.getRole().name(),
				staff.getStatus().name()
		);
	}
}
