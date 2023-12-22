package ntou.auction.spring.product.service;

import ntou.auction.spring.shoppingcart.service.ShoppingcartService;
import ntou.auction.spring.account.response.UserIdentity;
import ntou.auction.spring.account.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ntou.auction.spring.product.entity.Product;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
@Component
public class TimerTask {

    private final  ProductService productService;
    private final ShoppingcartService shoppingcartService;

    public TimerTask(ProductService productService, ShoppingcartService shoppingcartService, UserIdentity userIdentity, UserService userService) {
        this.productService = productService;
        this.shoppingcartService = shoppingcartService;
    }
    @Transactional
    @Scheduled(cron = "0 * * * * ?") //每分鐘的第0秒
    public void execute() {
        List <Product> productList = productService.findByProductNonFixed();

        for (Product product : productList) {
            System.out.println(product.getId());
            if (product.isExpired()) { //競標結束
                Map<Long,Long> productMap= product.getBidInfo();


                Optional<Map.Entry<Long,Long>> max0 = productMap.entrySet()
                        .stream().max(Map.Entry.comparingByValue());
                if(max0.isPresent()) {
                    shoppingcartService.addProductByUserId(max0.get().getKey(),product.getId(),1L);
                    product.setIsAuction(true);
                    productService.store(product);
                }
            }
        }
    }
}