package team.mediasoft.wareshop.scheduler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.mediasoft.wareshop.entity.Product;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Profile("dev")
@RequiredArgsConstructor
@ConditionalOnExpression("#{${app.scheduling.optimization} == true}")
@ConditionalOnProperty(prefix = "app.scheduling", name = "enabled", havingValue = "true")
public class OptimizedProductScheduler {

    private final JdbcTemplate jdbcTemplate;
    @Value("${app.scheduling.priceIncreasePercentage}")
    private BigDecimal price;
    @Value("${app.pathToFile}")
    private Path pathToWriteFile;


    @Transactional
    @Scheduled(fixedDelayString = "${app.scheduling.period}")
    public void updatePrice() throws IOException {
        log.info("Start to updating price for Optimized productsUpdateToBatch");

        List<Product> allProducts = findAllProducts().stream()
                .peek(product -> product.setPrice(product.getPrice().add(price)))
                .toList();

        List<Product> productsUpdateToBatch = new ArrayList<>();
        allProducts.forEach(product -> {
            productsUpdateToBatch.add(product);
            if (productsUpdateToBatch.size() == 100_000) {
                int lengthUpdateProducts = updateProducts(productsUpdateToBatch).length;
                log.info("Update {} record for Optimized productsUpdateToBatch", lengthUpdateProducts);
                productsUpdateToBatch.clear();
            }
        });

        List<String> listRowProducts = findAllProducts().stream()
                .map(Product::toString)
                .toList();
        writeToFile(listRowProducts);

        log.info("Stop to updating price for Optimized productsUpdateToBatch");
    }

    private List<Product> findAllProducts() {
        String sql = "SELECT * FROM PRODUCT";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class));
    }

    private int[] updateProducts(List<Product> products) {
        String sql = "UPDATE PRODUCT SET price = ? WHERE id = ?";
        return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                ps.setBigDecimal(1, products.get(i).getPrice().add(price));
                ps.setObject(2, products.get(i).getId(), java.sql.Types.OTHER);
            }

            @Override
            public int getBatchSize() {
                return 100_000;
            }
        });
    }

    private void writeToFile(List<String> productsStringRow) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(pathToWriteFile.toFile()))) {
            for (String product : productsStringRow) {
                out.write(product);
                out.newLine();
            }
        }
    }

}
