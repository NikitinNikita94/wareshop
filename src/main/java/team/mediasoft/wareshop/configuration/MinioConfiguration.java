package team.mediasoft.wareshop.configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.mediasoft.wareshop.util.MinioProperties;

@Configuration
@RequiredArgsConstructor
public class MinioConfiguration {

    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient client = MinioClient.builder()
                .endpoint(minioProperties.url())
                .credentials(minioProperties.username(), minioProperties.password())
                .build();
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.bucketName()).build())) {
            client.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(minioProperties.bucketName())
                            .build()
            );
        }
        return client;
    }
}
