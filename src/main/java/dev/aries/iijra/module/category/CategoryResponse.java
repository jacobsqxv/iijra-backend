package dev.aries.iijra.module.category;

import java.time.LocalDateTime;

public record CategoryResponse(
		Long id,
		String name,
		LocalDateTime createdAt
) {

	public static CategoryResponse toResponse(Category category) {
		return new CategoryResponse(category.getId(),
				category.getName(),
				category.getAuditing().getCreatedAt());
	}
}
