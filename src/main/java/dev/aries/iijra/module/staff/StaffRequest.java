package dev.aries.iijra.module.staff;

import jakarta.validation.constraints.NotBlank;

public record StaffRequest(
		@NotBlank(message = "Email is required")
		String email,
		@NotBlank(message = "Email is required")
		String fullName,
		@NotBlank(message = "Email is required")
		Long departmentId,
		@NotBlank(message = "Email is required")
		Boolean isHod
) {
}
