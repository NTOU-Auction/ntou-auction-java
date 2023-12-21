package ntou.auction.spring.shoppingcart.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ntou.auction.spring.product.entity.Product;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddAmount{
    private Product product;
    private Long amount;
}
