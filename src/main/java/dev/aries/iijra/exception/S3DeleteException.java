package dev.aries.iijra.exception;

import dev.aries.iijra.constant.ExceptionConstant;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class S3DeleteException extends RuntimeException {
	public S3DeleteException(@Nullable Exception e) {
		super(ExceptionConstant.FILE_DELETE_FAILURE, e);
	}
}
