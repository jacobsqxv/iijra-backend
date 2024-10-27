package dev.aries.iijra.exception;

import dev.aries.iijra.constant.ExceptionConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvalidTokenException extends RuntimeException {
	public InvalidTokenException() {
		super(ExceptionConstant.INVALID_TOKEN);
	}
}
