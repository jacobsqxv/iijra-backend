package dev.aries.iijra.global;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseTest {

	@Test
	@DisplayName("Should return success response entity when success method is called")
	void success_shouldReturnSuccessResponseEntity() {
		Object data = new Object();
		ResponseEntity<Object> response = Response.success(HttpStatus.OK, data);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(new Response(HttpStatus.OK.value(), data), response.getBody());
	}

	@Test
	@DisplayName("Should return error response entity when error method is called")
	void error_shouldReturnErrorResponseEntity() {
		String error = "Error message";
		Set<String> details = new HashSet<>(Set.of("Detail 1", "Detail 2"));
		ResponseEntity<Object> response = Response.error(HttpStatus.BAD_REQUEST, error, details);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(new Response.ErrorResponse(HttpStatus.BAD_REQUEST.value(), error, details), response.getBody());
	}

	@Test
	@DisplayName("Should return correct fields in response object")
	void errorResponse_shouldHaveCorrectFields() {
		int code = 400;
		String error = "Error message";
		Set<String> details = new HashSet<>(Set.of("Detail 1", "Detail 2"));
		Response.ErrorResponse errorResponse = new Response.ErrorResponse(code, error, details);

		assertEquals(code, errorResponse.code());
		assertEquals(error, errorResponse.error());
		assertEquals(details, errorResponse.details());
	}
}
