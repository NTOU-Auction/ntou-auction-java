package ntou.auction.spring.data.entity;

import ch.qos.logback.core.joran.sanity.Pair;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderRequest {
    //List<Long> -> (productid, amount)
    @NotNull
    List<List<Long>> productList;
}
