package dev.aries.iijra.module.admin;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AdminResponse(
		Long id,
		String fullName,
		String email,
		String role,
		String status,
		LocalDateTime createdAt
) {
	public static AdminResponse newResponse(Admin admin) {
		return new AdminResponse(
				admin.getId(),
				admin.getFullName(),
				admin.getUser().getEmail(),
				admin.getUser().getRole().name(),
				admin.getUser().getStatus().name(),
				admin.getAuditing().getCreatedAt()
		);
	}
}
