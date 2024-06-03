package team.mediasoft.wareshop.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rest.minio")
public record MinioProperties(String url, String username, String password, String bucketName) {
}
