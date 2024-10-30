package dev.aries.iijra.utility;

import dev.aries.iijra.exception.S3DeleteException;
import dev.aries.iijra.exception.S3UploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

	private static final String BUCKET_NAME = "test-bucket";
	private static final String FILE_URL = "https://test-bucket.s3.region.amazonaws.com/test-file.txt";
	private static final String GENERATED_URL = "https://generated-url.com/file.txt";
	@Mock
	private S3Client s3Client;
	@Mock
	private S3Utils s3Utils;
	@InjectMocks
	private S3Service s3Service;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(s3Service, "bucketName", BUCKET_NAME);
	}

	@Test
	@DisplayName("Should upload small multipart file successfully")
	void uploadFile_SmallFile_Success() {
		// Arrange
		MockMultipartFile file = new MockMultipartFile(
				"file",
				"test.txt",
				"text/plain",
				"test content".getBytes()
		);
		when(s3Utils.isMultipartUploadRequired(file.getSize())).thenReturn(false);
		when(s3Utils.generateFileUrl(any())).thenReturn(GENERATED_URL);

		// Act
		String result = s3Service.uploadFile(file, "test.txt");

		// Assert
		verify(s3Utils).validateFile(file);
		verify(s3Utils).uploadSmallFile(eq(file), any());
		verify(s3Utils, never()).uploadLargeFile(any(), any());
		assertEquals(GENERATED_URL, result);
	}

	@Test
	@DisplayName("Should upload large multipart file successfully")
	void uploadFile_LargeFile_Success() {
		// Arrange
		MockMultipartFile file = new MockMultipartFile(
				"file",
				"test.txt",
				"text/plain",
				"test content".getBytes()
		);
		when(s3Utils.isMultipartUploadRequired(file.getSize())).thenReturn(true);
		when(s3Utils.generateFileUrl(any())).thenReturn(GENERATED_URL);

		// Act
		String result = s3Service.uploadFile(file, "test.txt");

		// Assert
		verify(s3Utils).validateFile(file);
		verify(s3Utils).uploadLargeFile(eq(file), any());
		verify(s3Utils, never()).uploadSmallFile(any(), any());
		assertEquals(GENERATED_URL, result);
	}

	@Test
	@DisplayName("Should throw exception if file invalid")
	void uploadFile_ValidationFails_ThrowsException() {
		// Arrange
		MockMultipartFile file = new MockMultipartFile(
				"file",
				"test.txt",
				"text/plain",
				"test content".getBytes()
		);
		doThrow(new IllegalArgumentException("Invalid file"))
				.when(s3Utils).validateFile(file);

		// Act & Assert
		assertThrows(S3UploadException.class, () -> s3Service.uploadFile(file, "test.txt"));
	}

	@Test
	@DisplayName("Should successfully delete file from S3 if url is valid")
	void deleteFile_Success() {
		// Act
		s3Service.deleteFile(FILE_URL);

		// Assert
		verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
	}

	@Test
	@DisplayName("Should throw exception if bucket name in url is valid")
	void deleteFile_InvalidBucketName_ThrowsException() {
		// Arrange
		String wrongBucketUrl = "https://wrong-bucket.s3.region.amazonaws.com/test-file.txt";

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> s3Service.deleteFile(wrongBucketUrl));
		verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
	}

	@Test
	@DisplayName("Should throw exception if url is malformed")
	void deleteFile_MalformedUrl_ThrowsException() {
		// Arrange
		String invalidUrl = "not-a-url";

		// Act & Assert
		assertThrows(S3DeleteException.class, () -> s3Service.deleteFile(invalidUrl));
		verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
	}

	@Test
	@DisplayName("Should successfully delete file from S3 if url is valid and key exists")
	void deleteFile_VerifyCorrectKeyExtraction() {
		// Arrange
		String fileUrl = "https://test-bucket.s3.region.amazonaws.com/folder/test-file.txt";
		DeleteObjectRequest expectedRequest = DeleteObjectRequest.builder()
				.bucket(BUCKET_NAME)
				.key("folder/test-file.txt")
				.build();

		// Act
		s3Service.deleteFile(fileUrl);

		// Assert
		verify(s3Client).deleteObject(expectedRequest);
	}
}
