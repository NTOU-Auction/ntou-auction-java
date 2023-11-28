package ntou.auction.spring.data.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingcartRequest {
    @NotNull
    private Long productId;
    @NotNull
    private Long amount;
}
