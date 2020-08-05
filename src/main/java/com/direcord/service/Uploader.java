package com.direcord.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class Uploader {
	public static void uploadObject(String projectId, String bucketName, String objectName, String filePath)
			throws IOException {
		// The ID of your GCP project
		// String projectId = "your-project-id";

		// The ID of your GCS bucket
		// String bucketName = "your-unique-bucket-name";

		// The ID of your GCS object
		// String objectName = "your-object-name";

		// The path to your file to upload
		// String filePath = "path/to/your/file"

		BlobId blobId = BlobId.of(bucketName, objectName);
		Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));

		System.out.println("File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
	}
}