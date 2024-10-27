package dev.aries.iijra.module.auth;

import dev.aries.iijra.constant.Patterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
		@NotBlank(message = "Email is required")
		@Pattern(regexp = Patterns.EMAIL)
		String email,
		@NotBlank(message = "Password is required")
		String password
) {
}
