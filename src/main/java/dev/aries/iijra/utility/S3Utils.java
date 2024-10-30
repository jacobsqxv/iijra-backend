package dev.aries.iijra.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(file.getBytes());
		}
		return convertedFile;
	}

	public static String generateUniqueKey(MultipartFile file, String fileName) {
		String originalFilename = file.getOriginalFilename();
		String extension = "";

		if (originalFilename != null && originalFilename.contains(".")) {
			extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		}
		// Generate new filename with UUID and custom name
		return UUID.randomUUID() + "-" + fileName + extension;
	}

	public String generateFileUrl(String key) {
		// Option 1: Using the S3 URL pattern
		String fileUrlFormat = "https://%s.s3.%s.amazonaws.com/%s";
		return String.format(fileUrlFormat, bucketName, region , key);

	}

	public boolean isMultipartUploadRequired(long fileSize) {
		return fileSize >= 5_242_880; // 5MB in bytes
	}

	public void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be empty");
		}
	}

	public void uploadSmallFile(MultipartFile file, String key) throws IOException {
		PutObjectRequest request = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.contentType(file.getContentType())
				.build();

		s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
	}

	public void uploadLargeFile(MultipartFile file, String key) throws IOException {
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
		List<CompletedPart> completedParts = new ArrayList<>();

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
			throw e;
		} finally {
			convertedFile.delete();
		}
	}

	private List<CompletedPart> uploadParts(File file, String uploadId, String key) throws IOException {
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