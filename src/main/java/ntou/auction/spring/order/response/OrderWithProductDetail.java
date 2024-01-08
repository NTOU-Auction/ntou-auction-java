package ntou.auction.spring.order.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ntou.auction.spring.shoppingcart.response.ProductAddAmount;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderWithProductDetail {
    @NotNull
    private Long orderid;

    @NotNull
    private Long buyerid;

    @NotNull
    private Long sellerid;

    private List<ProductAddAmount> productAddAmountList = new ArrayList<>();

    @NotNull
    private Long status; // 0: reject, 1: waiting for submit, 2: submitted but not paid, 3: order done

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
