package team.mediasoft.wareshop.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.mediasoft.wareshop.aop.MarkTime;
import team.mediasoft.wareshop.data.repository.ProductRepository;
import team.mediasoft.wareshop.entity.Product;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!Local")
@ConditionalOnMissingBean(OptimizedProductScheduler.class)
@ConditionalOnExpression("#{${app.scheduling.optimization} == false && ${app.scheduling.enabled:false} == true}")
public class OrdinaryProductScheduler {

    private final ProductRepository productRepository;
    @Value("${app.scheduling.priceIncreasePercentage}")
    private BigDecimal price;

    @MarkTime
    @Transactional
    @Scheduled(fixedDelayString = "${app.scheduling.period}")
    public void updatePrice() {
        log.info("Start to updating price for Ordinary products");

        List<Product> list = productRepository.findAll().stream()
                .peek(product -> product.setPrice(product.getPrice().add(price)))
                .toList();
        productRepository.saveAll(list);

        log.info("Stop to updating price for Ordinary products");
    }
}
