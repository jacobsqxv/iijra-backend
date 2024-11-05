package dev.aries.iijra.global;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record PageResponse<T>(
		List<T> data,
		PageMetadata metadata
) {
	public static <T> ResponseEntity<PageResponse<T>> of(Page<T> page, HttpStatus status) {
		return new ResponseEntity<>(new PageResponse<>(
				page.getContent(),
				new PageMetadata(
						page.getNumber(),
						page.getSize(),
						page.getTotalElements(),
						page.getTotalPages()
				)
		),
				status);
	}

	public record PageMetadata(int page, int size, Long totalElements, int totalPages) {
	}
}
