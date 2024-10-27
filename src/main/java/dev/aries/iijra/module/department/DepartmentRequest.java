package dev.aries.iijra.module.department;

import dev.aries.iijra.constant.Patterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DepartmentRequest(
		@NotBlank(message = "Department name is required")
		@Pattern(regexp = Patterns.DEPARTMENT_NAME)
		String name
) {
}
