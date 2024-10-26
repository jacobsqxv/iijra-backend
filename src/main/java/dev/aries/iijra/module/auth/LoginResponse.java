package dev.aries.iijra.module.auth;

import dev.aries.iijra.module.user.User;

public record LoginResponse(
		String token,
		Long userId,
		String email,
		String role,
		String status
) {
	public static LoginResponse newResponse(User user, String token) {
		return new LoginResponse(
				token,
				user.getId(),
				user.getEmail(),
				user.getRole().name(),
				user.getStatus().name()
		);
	}
}
