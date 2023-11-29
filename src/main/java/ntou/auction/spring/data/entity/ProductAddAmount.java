package ntou.auction.spring.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddAmount{
    private Product product;
    private Long amount;
}
