package dev.aries.iijra.exception;

import dev.aries.iijra.constant.ExceptionConstant;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class S3UploadException extends RuntimeException {
	public S3UploadException(@Nullable Exception e) {
		super(ExceptionConstant.FILE_UPLOAD_FAILURE, e);
	}
}
