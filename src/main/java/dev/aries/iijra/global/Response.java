package dev.aries.iijra.global;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record Response(
		int code,
		Object data
) {
	public static ResponseEntity<Object> success(HttpStatus status, Object data) {
		return response(status.value(), status, data);
	}

	public static ResponseEntity<Object> error(HttpStatus status, String error, Set<String> details) {
		return new ResponseEntity<>(new ErrorResponse(status.value(), error, details),
				status);
	}

	private static ResponseEntity<Object> response(int code, HttpStatus status, Object data) {
		return new ResponseEntity<>(
				new Response(code, data),
				status
		);
	}

	public record ErrorResponse(
			int code,
			String error,
			Set<String> details
	) {
	}
}

