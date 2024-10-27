package dev.aries.iijra.exception;

import dev.aries.iijra.constant.ExceptionConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class S3DownloadException extends RuntimeException {
	public S3DownloadException(Exception e) {
		super(ExceptionConstant.FILE_DOWNLOAD_FAILURE, e);
	}
}
