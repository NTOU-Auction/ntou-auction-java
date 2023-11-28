package ntou.auction.spring.data.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ntou.auction.spring.data.entity.Product;

import java.util.List;
@Component
public class TimerTask {

    private final  ProductService productService;

    public TimerTask(ProductService productService) {
        this.productService = productService;
    }

    @Scheduled(cron = "0 * * * * ?") //每分鐘的第0秒
    public void execute() {
        List <Product> productList = productService.findByProductNonFixed();

        for (Product product : productList) {
            if (product.isExpired()) {
                System.out.println("這個id = "+product.getId() + "的商品競標結束了");
            }
        }
    }
}
