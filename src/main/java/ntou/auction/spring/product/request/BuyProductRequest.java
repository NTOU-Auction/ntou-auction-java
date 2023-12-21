package ntou.auction.spring.product.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyProductRequest {

    @NotNull (message="商品ID不得為空")
    private Long productID;

    @NotNull (message="商品數量不得為空")
    @Min (value = 1,message = "商品至少一個")
    private Long productAmount;

}
