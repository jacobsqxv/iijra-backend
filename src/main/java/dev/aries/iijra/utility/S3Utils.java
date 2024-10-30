package dev.aries.iijra.utility;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.aries.iijra.exception.FileConversionException;
import dev.aries.iijra.exception.S3UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3Utils {
	private final S3Client s3Client;
	@Value("${aws.s3.bucket}")
	private String bucketName;
	@Value("${aws.region}")
	private String region;

	public static String generateUniqueKey(MultipartFile file, String fileName) {
		String originalFilename = file.getOriginalFilename();
		String extension = "";

		if (originalFilename != null && originalFilename.contains(".")) {
			extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		}
		return UUID.randomUUID() + "-" + fileName + extension;
	}

	@SuppressWarnings("java:S5443")
	private File convertMultiPartToFile(MultipartFile file) {
		try {
			// Use a temporary directory to store the file
			Path tempDir = Files.createTempDirectory("uploaded-files");
			Path tempFilePath = tempDir.resolve(UUID.randomUUID().toString());

			// Write the file to the temporary location
			Files.write(tempFilePath, file.getBytes());

			return tempFilePath.toFile();
		} catch (IOException e) {
			log.info("Error converting multipart file");
			throw new FileConversionException(e);
		}
	}

	public String generateFileUrl(String key) {
		String fileUrlFormat = "https://%s.s3.%s.amazonaws.com/%s";
		return String.format(fileUrlFormat, bucketName, region, key);
	}

	public boolean isMultipartUploadRequired(long fileSize) {
		return fileSize >= 5_242_880; // 5MB in bytes
	}

	public void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be empty");
		}
	}

	public void uploadSmallFile(MultipartFile file, String key) {
		PutObjectRequest request = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.contentType(file.getContentType())
				.build();
		try {
			s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
		} catch (IOException e) {
			throw new S3UploadException(e);
		}
	}

	@SuppressWarnings({"java:S4042", "java:S899"})
	public void uploadLargeFile(MultipartFile file, String key) {
		// Create multipart upload request
		CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
				.bucket(bucketName)
				.key(key)
				.contentType(file.getContentType())
				.build();

		CreateMultipartUploadResponse createMultipartUploadResponse =
				s3Client.createMultipartUpload(createMultipartUploadRequest);
		String uploadId = createMultipartUploadResponse.uploadId();

		File convertedFile = convertMultiPartToFile(file);
		List<CompletedPart> completedParts;

		try {
			// Upload parts
			completedParts = uploadParts(convertedFile, uploadId, key);

			List<CompletedPart> finalCompletedParts = completedParts;
			CompleteMultipartUploadRequest completeMultipartUploadRequest =
					CompleteMultipartUploadRequest.builder()
							.bucket(bucketName)
							.key(key)
							.uploadId(uploadId)
							.multipartUpload(c -> c.parts(finalCompletedParts))
							.build();

			s3Client.completeMultipartUpload(completeMultipartUploadRequest);
		} catch (Exception e) {
			abortMultipartUpload(key, uploadId);
			throw new S3UploadException(e);
		} finally {
			boolean delete = convertedFile.delete();
			log.info("File delete status: {}", delete);
		}
	}

	private List<CompletedPart> uploadParts(File file, String uploadId, String key) {
		List<CompletedPart> completedParts = new ArrayList<>();
		long partSize = 5L * 1024 * 1024; // 5MB part size

		try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
			long fileSize = raf.length();
			long position = 0;
			int partNumber = 1;

			while (position < fileSize) {
				long contentLength = Math.min(partSize, fileSize - position);
				byte[] buffer = new byte[(int) contentLength];
				raf.read(buffer);

				UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
						.bucket(bucketName)
						.key(key)
						.uploadId(uploadId)
						.partNumber(partNumber)
						.build();

				UploadPartResponse uploadPartResponse = s3Client.uploadPart(
						uploadPartRequest,
						RequestBody.fromBytes(buffer)
				);

				CompletedPart part = CompletedPart.builder()
						.partNumber(partNumber)
						.eTag(uploadPartResponse.eTag())
						.build();
				completedParts.add(part);

				position += contentLength;
				partNumber++;
			}
		} catch (IOException e) {
			throw new S3UploadException(e);
		}
		return completedParts;
	}

	private void abortMultipartUpload(String key, String uploadId) {
		try {
			AbortMultipartUploadRequest abortMultipartUploadRequest =
					AbortMultipartUploadRequest.builder()
							.bucket(bucketName)
							.key(key)
							.uploadId(uploadId)
							.build();
			s3Client.abortMultipartUpload(abortMultipartUploadRequest);
		} catch (Exception e) {
			log.error("Failed to abort multipart upload", e);
		}
	}

}
