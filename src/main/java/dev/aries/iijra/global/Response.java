package dev.aries.iijra.global;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record Response(
		Object data
) {
	public static ResponseEntity<Object> success(HttpStatus status, Object data) {
		return response(status, data);
	}

	public static ResponseEntity<Object> error(HttpStatus status, String error, Set<String> details) {
		return new ResponseEntity<>(new ErrorResponse(error, details),
				status);
	}

	private static ResponseEntity<Object> response(HttpStatus status, Object data) {
		return new ResponseEntity<>(
				new Response(data),
				status
		);
	}

	public record ErrorResponse(
			String error,
			Set<String> details
	) {
	}
}
