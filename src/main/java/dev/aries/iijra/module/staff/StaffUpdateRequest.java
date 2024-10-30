package dev.aries.iijra.module.staff;

import dev.aries.iijra.constant.Patterns;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import org.springframework.web.multipart.MultipartFile;

@Builder
public record StaffUpdateRequest(
		MultipartFile profileImage,
		@Pattern(regexp = Patterns.USER_NAME)
		String fullName,
		@Size(max = 500, message = "Bio must not exceed 500 characters")
		String bio
) {
}
