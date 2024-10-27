package dev.aries.iijra.module.auth;

import dev.aries.iijra.constant.Patterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPassword(
		@NotBlank(message = "Email is required")
		String email,
		@NotBlank(message = "Password is required")
		@Pattern(regexp = Patterns.PASSWORD)
		String password
) {
}
