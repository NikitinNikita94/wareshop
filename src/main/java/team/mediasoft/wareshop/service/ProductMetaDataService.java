package team.mediasoft.wareshop.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import team.mediasoft.wareshop.data.repository.ProductRepository;
import team.mediasoft.wareshop.entity.Product;
import team.mediasoft.wareshop.entity.ProductFileMetadata;
import team.mediasoft.wareshop.exception.ProductNotFoundException;
import team.mediasoft.wareshop.util.MinioProperties;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ProductMetaDataService {

    private final ProductRepository productRepository;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    /**
     * Метод для добавления файлов к продукту
     *
     * @param productId -  айди продукта
     * @param file      - файлы
     */
    @SneakyThrows
    @Transactional
    public void uploadFile(@NotNull final UUID productId, @NotNull final MultipartFile... file) {
        Product product = checkProduct(productId);

        List<ProductFileMetadata> productFileMetadataList = new ArrayList<>();
        for (MultipartFile f : file) {
            UUID fileId = UUID.randomUUID();
            minioClient.putObject(putFile(fileId, f));
            productFileMetadataList.add(ProductFileMetadata.builder()
                    .product(product)
                    .fileId(fileId)
                    .build());
        }

        product.setFiles(productFileMetadataList);
    }

    /**
     * Метод для скачивания файлов в Zip формате.
     *
     * @param productId - айди продукта
     * @param response  - ответ
     */
    @SneakyThrows
    @Transactional
    public void downloadFiles(@NotNull final UUID productId, final HttpServletResponse response) {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                .filename("download.zip", StandardCharsets.UTF_8)
                .build()
                .toString());

        Product product = checkProduct(productId);

        List<UUID> listFilesId = product.getFiles().stream()
                .map(ProductFileMetadata::getFileId)
                .toList();

        try (ZipOutputStream zip = new ZipOutputStream(response.getOutputStream())) {
            for (UUID objectId : listFilesId) {
                byte[] file = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(minioProperties.bucketName())
                        .object(objectId.toString())
                        .build()).readAllBytes();

                if (file == null || file.length == 0) continue;
                zip.putNextEntry(new ZipEntry(objectId.toString()));
                StreamUtils.copy(file, zip);
                zip.flush();
            }
        }
    }

    /**
     * Метод для добавления одного экземпляра файла
     *
     * @param object - айди объекта в базе
     * @param file   - файл
     * @return PutObjectArgs
     */
    @SneakyThrows
    private PutObjectArgs putFile(UUID object, MultipartFile file) {
        return PutObjectArgs.builder()
                .bucket(minioProperties.bucketName())
                .object(object.toString())
                .contentType(file.getContentType())
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
    }

    /**
     * Метод для проверки товара на складе.
     *
     * @param productId - айди продукта
     * @return Product - возвращает товар.
     */
    private Product checkProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product by id " + productId + " not found"));
    }
}
