package dev.aries.iijra.module.user;

import dev.aries.iijra.constant.Patterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordUpdateRequest(
		@NotBlank(message = "Current password is required")
		@Pattern(regexp = Patterns.PASSWORD)
		String currentPassword,
		@NotBlank(message = "New password is required")
		@Pattern(regexp = Patterns.PASSWORD)
		String newPassword
) {
}
