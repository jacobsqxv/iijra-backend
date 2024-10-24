package dev.aries.iijra.module.auth;

import jakarta.validation.constraints.NotNull;

public record ForgotPassword(
		@NotNull(message = "Email is required")
		String email
) {
}
