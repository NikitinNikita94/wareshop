package team.mediasoft.wareshop.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.mediasoft.wareshop.aop.MarkTime;
import team.mediasoft.wareshop.data.repository.ProductRepository;
import team.mediasoft.wareshop.entity.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!local")
@ConditionalOnMissingBean(OptimizedProductScheduler.class)
@ConditionalOnExpression("#{${app.scheduling.optimization} == false && ${app.scheduling.enabled:false}}")
public class OrdinaryProductScheduler {

    private final ProductRepository productRepository;
    @Value("${app.scheduling.priceIncreasePercentage}")
    private BigDecimal price;

    @MarkTime
    @Transactional
    @Scheduled(fixedDelayString = "${app.scheduling.period}")
    public void updatePrice() {
        log.info("Start to updating price for Ordinary products");

        List<Product> list = productRepository.findAll();
        list.forEach(product -> product.setPrice(getNewPrice(product.getPrice(), price)));
        productRepository.saveAll(list);

        log.info("Stop to updating price for Ordinary products");
    }

    private BigDecimal getNewPrice(BigDecimal oldPrice, BigDecimal increase) {
        return oldPrice.multiply(BigDecimal.ONE.add(increase.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
    }
}
