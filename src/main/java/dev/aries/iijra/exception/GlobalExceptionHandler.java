package dev.aries.iijra.exception;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.global.Response;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({LockedException.class, DisabledException.class,
			UnauthorizedAccessException.class, AccessDeniedException.class})
	public ResponseEntity<Object> handleUnauthorizedExceptions(Exception exp) {
		logException(exp);
		Set<String> details = new HashSet<>();
		switch (exp) {
			case UnauthorizedAccessException ex -> details.add(ex.getMessage());
			case AccessDeniedException ex -> details.add(ExceptionConstant.ACCESS_DENIED);
			case DisabledException ex -> details.add(ExceptionConstant.ACCOUNT_DISABLED);
			default -> details.add(exp.getMessage());
		}
		HttpStatus code = HttpStatus.FORBIDDEN;
		return Response.error(code, code.getReasonPhrase(), details);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleArgumentNotValidException(MethodArgumentNotValidException exp) {
		logException(exp);
		Set<String> details = new HashSet<>();
		exp.getBindingResult().getAllErrors().forEach(error -> {
			String errorMessage = error.getDefaultMessage();
			details.add(errorMessage);
		});
		HttpStatus code = HttpStatus.BAD_REQUEST;
		return Response.error(code, code.getReasonPhrase(), details);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleNotFoundException(EntityNotFoundException exp) {
		logException(exp);
		Set<String> details = new HashSet<>();
		details.add(exp.getMessage());
		HttpStatus code = HttpStatus.NOT_FOUND;
		return Response.error(code, code.getReasonPhrase(), details);
	}

	@ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class,
			BadCredentialsException.class, InvalidTokenException.class})
	public ResponseEntity<Object> handleBadRequests(Exception exp) {
		logException(exp);
		Set<String> details = new HashSet<>();
		if (Objects.requireNonNull(exp) instanceof BadCredentialsException) {
			details.add(ExceptionConstant.INVALID_CREDENTIALS);
		} else {
			details.add(exp.getMessage());
		}
		HttpStatus code = HttpStatus.BAD_REQUEST;
		return Response.error(code, code.getReasonPhrase(), details);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleInvalidRequestFormat(HttpMessageNotReadableException exp) {
		logException(exp);
		Set<String> details = new HashSet<>();
		Throwable rootCause = exp.getRootCause();
		if (rootCause instanceof IllegalArgumentException exception) {
			return handleBadRequests(exception);
		} else {
			details.add(ExceptionConstant.INVALID_REQUEST);
		}
		HttpStatus code = HttpStatus.BAD_REQUEST;
		return Response.error(code, code.getReasonPhrase(), details);
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<Object> handleOtherExceptions(Exception exp) {
		logException(exp);
		Set<String> details = new HashSet<>();
		details.add(exp.getMessage());
		HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
		return Response.error(code, code.getReasonPhrase(), details);
	}

	private void logException(Exception ex) {
		log.error("Error occurred: ", ex);
	}
}
