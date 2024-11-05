package dev.aries.iijra.global;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PageResponseTest {
	private static int page;
	private static int size;
	private static long totalElements;
	private static int totalPages;

	@BeforeAll
	static void setUp() {
		page = 1;
		size = 10;
		totalElements = 25;
		totalPages = 3;
	}

	@Test
	@DisplayName("Should successfully return page response data")
	void testPageResponseOf() {
		// Arrange
		List<String> data = Arrays.asList("item1", "item2", "item3");

		Page<String> pageData = mock(Page.class);
		when(pageData.getContent()).thenReturn(data);
		when(pageData.getNumber()).thenReturn(page);
		when(pageData.getSize()).thenReturn(size);
		when(pageData.getTotalElements()).thenReturn(totalElements);
		when(pageData.getTotalPages()).thenReturn(totalPages);

		// Act
		ResponseEntity<PageResponse<String>> response = PageResponse.of(pageData, HttpStatus.OK);

		// Assert
		assertEquals(data, Objects.requireNonNull(response.getBody()).data());
		assertEquals(page, response.getBody().metadata().page());
		assertEquals(size, response.getBody().metadata().size());
		assertEquals(totalElements, response.getBody().metadata().totalElements());
		assertEquals(totalPages, response.getBody().metadata().totalPages());
	}

	@Test
	@DisplayName("Should successfully return page metadata")
	void testPageMetadata() {
		// Act
		PageResponse.PageMetadata metadata = new PageResponse.PageMetadata(page, size, totalElements, totalPages);

		// Assert
		assertEquals(page, metadata.page());
		assertEquals(size, metadata.size());
		assertEquals(totalElements, metadata.totalElements());
		assertEquals(totalPages, metadata.totalPages());
	}
}
