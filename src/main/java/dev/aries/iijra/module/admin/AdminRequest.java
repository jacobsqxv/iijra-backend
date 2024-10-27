package dev.aries.iijra.module.admin;

import dev.aries.iijra.constant.Patterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdminRequest(
		@NotBlank(message = "Email is required")
		@Pattern(regexp = Patterns.EMAIL)
		String email,
		@NotBlank(message = "Email is required")
		@Pattern(regexp = Patterns.USER_NAME)
		String fullName
) {
}
