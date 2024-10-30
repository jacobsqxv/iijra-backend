package dev.aries.iijra.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import dev.aries.iijra.exception.S3UploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3UtilsTest {

	private static final String BUCKET_NAME = "test-bucket";
	private static final String REGION = "test-region";
	@Mock
	private S3Client s3Client;
	@InjectMocks
	private S3Utils s3Utils;

	@BeforeEach
	void setUp() {
		s3Utils = new S3Utils(s3Client);
		ReflectionTestUtils.setField(s3Utils, "bucketName", BUCKET_NAME);
		ReflectionTestUtils.setField(s3Utils, "region", REGION);
	}

	@Test
	@DisplayName("Should successfully generate unique key")
	void testGenerateUniqueKey() {
		MultipartFile file = mock(MultipartFile.class);
		when(file.getOriginalFilename()).thenReturn("test.txt");

		String key = S3Utils.generateUniqueKey(file, "custom-name");
		assertTrue(key.matches("[0-9a-fA-F-]+-custom-name\\.txt"));
	}

	@Test
	@DisplayName("Should successfully generate file url with key")
	void testGenerateFileUrl() {
		String key = "test-key";
		String url = s3Utils.generateFileUrl(key);

		String expected = String.format("https://%s.s3.%s.amazonaws.com/test-key",
				BUCKET_NAME, REGION);

		assertEquals(expected, url);
	}

	@Test
	@DisplayName("Should use multipart upload if file is bigger than 5MB")
	void testIsMultipartUploadRequired() {
		assertTrue(s3Utils.isMultipartUploadRequired(5_242_880));
		assertFalse(s3Utils.isMultipartUploadRequired(5_242_879));
	}

	@Test
	@DisplayName("Should successfully validate a multipart file")
	void testValidateFile() {
		MultipartFile file = mock(MultipartFile.class);
		when(file.isEmpty()).thenReturn(false);

		assertDoesNotThrow(() -> s3Utils.validateFile(file));
	}

	@Test
	@DisplayName("Should throw exception when multipart file validation fails")
	void testValidateFileThrowsException() {
		MultipartFile file = mock(MultipartFile.class);
		when(file.isEmpty()).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> s3Utils.validateFile(file));
	}

	@Test
	@DisplayName("Should successfully upload file directly if less than 5MB")
	void testUploadSmallFile() throws IOException {
		MultipartFile file = mock(MultipartFile.class);
		when(file.getInputStream()).thenReturn(mock(InputStream.class));
		when(file.getSize()).thenReturn(1024L);
		when(file.getContentType()).thenReturn("text/plain");

		s3Utils.uploadSmallFile(file, "test-key");

		verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
	}

	@Test
	@DisplayName("Should throw exception when direct file upload fails")
	void testUploadSmallFileThrowsException() throws IOException {
		MultipartFile file = mock(MultipartFile.class);
		when(file.getInputStream()).thenThrow(new IOException("Test exception"));

		assertThrows(S3UploadException.class, () -> s3Utils.uploadSmallFile(file, "test-key"));
	}

	@Test
	@DisplayName("Should successfully upload file in parts if bigger than 5MB")
	void testUploadLargeFile() throws IOException {
		MultipartFile file = mock(MultipartFile.class);
		when(file.getOriginalFilename()).thenReturn("test.txt");
		when(file.getContentType()).thenReturn("text/plain");
		when(file.getBytes()).thenReturn("test content".getBytes());

		CreateMultipartUploadResponse createMultipartUploadResponse = CreateMultipartUploadResponse.builder()
				.uploadId("test-upload-id")
				.build();
		when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
				.thenReturn(createMultipartUploadResponse);

		UploadPartResponse uploadPartResponse = UploadPartResponse.builder()
				.eTag(UUID.randomUUID().toString())
				.build();
		when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
				.thenReturn(uploadPartResponse);

		s3Utils.uploadLargeFile(file, "test-key");

		verify(s3Client).completeMultipartUpload(any(CompleteMultipartUploadRequest.class));
	}

	@Test
	@DisplayName("Should throw exception when upload in parts fail")
	void testUploadLargeFileThrowsException() throws IOException {
		MultipartFile file = mock(MultipartFile.class);
		when(file.getOriginalFilename()).thenReturn("test.txt");
		when(file.getContentType()).thenReturn("text/plain");
		when(file.getBytes()).thenReturn("test content".getBytes());

		CreateMultipartUploadResponse createMultipartUploadResponse = CreateMultipartUploadResponse.builder()
				.uploadId("test-upload-id")
				.build();
		when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
				.thenReturn(createMultipartUploadResponse);

		when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
				.thenThrow(new S3UploadException(new Exception()));

		assertThrows(S3UploadException.class, () -> s3Utils.uploadLargeFile(file, "test-key"));

		verify(s3Client).abortMultipartUpload(any(AbortMultipartUploadRequest.class));
	}
}
