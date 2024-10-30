package dev.aries.iijra.exception;

import dev.aries.iijra.constant.ExceptionConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FileConversionException extends RuntimeException {
	public FileConversionException(Exception e) {
		super(ExceptionConstant.FILE_CONVERSION_FAILURE, e);
	}
}
