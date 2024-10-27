package dev.aries.iijra.module.staff;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StaffRequest(
		@NotBlank(message = "Email is required")
		String email,
		@NotBlank(message = "Email is required")
		String fullName,
		@NotNull(message = "Email is required")
		Long departmentId,
		@NotNull(message = "Email is required")
		Boolean isHod
) {
}
