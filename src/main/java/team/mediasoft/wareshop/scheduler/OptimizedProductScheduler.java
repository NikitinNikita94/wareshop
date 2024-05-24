package team.mediasoft.wareshop.scheduler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
import java.math.RoundingMode;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Profile("!local")
@RequiredArgsConstructor
@ConditionalOnExpression("${app.scheduling.optimization:false == true} && ${app.scheduling.enabled:false == true}")
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

        List<Product> allProducts = findAllProducts();
        allProducts.forEach(product -> product.setPrice(getNewPrice(product.getPrice(), price)));

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

    private BigDecimal getNewPrice(BigDecimal oldPrice, BigDecimal increase) {
        return oldPrice.multiply(BigDecimal.ONE.add(increase.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
    }

    private void writeToFile(List<String> productsStringRow) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(pathToWriteFile.toFile()))) {
            for (String product : productsStringRow) {
                out.write(product);
                out.newLine();
            }
        }
    }

    /**
     @Slf4j
     @Component
     @Profile("!local")
     @ConditionalOnExpression(value = "#{'${app.scheduling.mode:none}'.equals('optimized')}")
     public class OptimizedProductPriceScheduler {

     private final EntityManagerFactory entityManagerFactory;

     private final BigDecimal priceIncreasePercentage;

     private final Boolean exclusiveLock;

     public OptimizedProductPriceScheduler(
     EntityManagerFactory entityManagerFactory,
     @Value("#{new java.math.BigDecimal(\ " $ { app.scheduling.priceIncreasePercentage : 10 } \ ")}")
     BigDecimal priceIncreasePercentage,
     @Value("${app.scheduling.optimization.exclusive-lock:false}") Boolean exclusiveLock
     ) {
     this.entityManagerFactory = entityManagerFactory;
     this.priceIncreasePercentage = priceIncreasePercentage;
     this.exclusiveLock = exclusiveLock;
     }

     @MeasureExecutionTime
     @Scheduled(fixedRateString = "${app.scheduling.period}")
     public void increaseProductPrice() {
     log.info("Start optimized scheduling");
     final Session session = entityManagerFactory.createEntityManager().unwrap(Session.class);

     try (session) {
     session.doWork(new Work() {
     @Override public void execute(Connection connection) throws SQLException {
     try (
     BufferedWriter fileWriter = new BufferedWriter(new FileWriter("results.txt"));
     connection
     ) {
     connection.setAutoCommit(false);
     long rowCounter = 0L;

     if (exclusiveLock) {
     String lock = "LOCK TABLE products IN ACCESS EXCLUSIVE MODE";
     Statement lockStatement = connection.createStatement();
     lockStatement.execute(lock);
     }

     String selectQuery = "SELECT * FROM products FOR UPDATE";
     Statement statement = connection.createStatement();

     String updateQuery = "UPDATE products SET PRICE = ? WHERE id = ?";
     PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);

     ResultSet rs = statement.executeQuery(selectQuery);
     while (rs.next()) {
     final BigDecimal newPrice = getNewPrice(rs.getBigDecimal("price"), priceIncreasePercentage);
     preparedStatement.setBigDecimal(1, newPrice);
     preparedStatement.setObject(2, UUID.fromString(rs.getString("id")));
     preparedStatement.addBatch();

     fileWriter.write(buildString(rs, rowCounter, newPrice));
     fileWriter.newLine();

     rowCounter++;
     if (rowCounter % 100000 == 0) {
     preparedStatement.executeBatch();
     log.debug("Rows updated: " + rowCounter);
     }
     }
     preparedStatement.executeBatch();

     log.debug("Committing");
     connection.commit();
     } catch (Exception e) {
     connection.rollback();
     throw new RuntimeException(e);
     }
     }
     });
     }
     log.info("End optimized scheduling");
     }

     //        session.beginTransaction();
     //        Query<ProductEntity> query = session.createNativeQuery("select * from products FOR UPDATE", ProductEntity.class);
     //        ScrollableResults<ProductEntity> results = query.scroll();
     //        while (results.next()) {
     //            ProductEntity product = results.get();
     //            BigDecimal currentPrice = product.getPrice();
     //            BigDecimal newPrice = currentPrice.multiply(BigDecimal.ONE.add(priceIncreasePercentage.divide(BigDecimal.valueOf(100))));
     //            product.setPrice(newPrice.setScale(2, RoundingMode.HALF_UP));
     //
     //        }
     //        session.getTransaction().commit();
     //        session.close();

     private BigDecimal getNewPrice(BigDecimal oldPrice, BigDecimal increase) {
     return oldPrice.multiply(BigDecimal.ONE.add(increase.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
     }

     private String buildString(ResultSet resultSet, Long counter, BigDecimal newPrice) throws SQLException {
     final StringBuilder builder = new StringBuilder();
     builder.append(counter);
     builder.append(" ");
     builder.append(resultSet.getString("id"));
     builder.append(" ");
     builder.append(resultSet.getString("name"));
     builder.append(" ");
     builder.append(resultSet.getString("description"));
     builder.append(" ");
     builder.append(newPrice);
     builder.append(" ");
     builder.append(resultSet.getString("article"));

     return builder.toString();
     }
     }
     */
}
