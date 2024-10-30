package dev.aries.iijra.utility;

import java.net.URL;

import dev.aries.iijra.exception.S3DeleteException;
import dev.aries.iijra.exception.S3UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static dev.aries.iijra.utility.S3Utils.generateUniqueKey;
import static org.springframework.util.ResourceUtils.toURL;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
	private final S3Client s3Client;
	private final S3Utils s3Utils;
	@Value("${aws.s3.bucket}")
	private String bucketName;

	public String uploadFile(MultipartFile file, String fileName) {
		try {
			s3Utils.validateFile(file);
			String key = generateUniqueKey(file, fileName);

			if (s3Utils.isMultipartUploadRequired(file.getSize())) {
				s3Utils.uploadLargeFile(file, key);
			} else {
				s3Utils.uploadSmallFile(file, key);
			}

			return s3Utils.generateFileUrl(key);

		} catch (Exception e) {
			log.error("Failed to upload file", e);
			throw new S3UploadException(e);
		}
	}

	public void deleteFile(String fileUrl) {
		try {
			URL s3Url = toURL(fileUrl);
			String host = s3Url.getHost();
			// Extract bucket name from the hostname (format: bucket-name.s3.region.amazonaws.com)
			String urlBucketName = host.split("\\.")[0];
			log.info("Bucket name: {}", urlBucketName);

			// The path is the key, remove leading slash
			String key = s3Url.getPath().substring(1);
			// Validate bucket name matches configured bucket
			if (!urlBucketName.equals(bucketName)) {
				throw new IllegalArgumentException("URL bucket name does not match configured bucket");
			}

			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
					.bucket(bucketName)
					.key(key).build();

			s3Client.deleteObject(deleteObjectRequest);
		} catch (java.net.MalformedURLException e) {
			log.error("Invalid S3 URL format", e);
			throw new S3DeleteException(e);
		}
	}
}
