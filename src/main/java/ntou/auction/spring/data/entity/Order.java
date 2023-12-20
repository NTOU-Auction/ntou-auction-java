package ntou.auction.spring.data.entity;

import ch.qos.logback.core.joran.sanity.Pair;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order extends AbstractEntity {
    @NotNull
    private Long buyerid;

    @NotNull
    private Long sellerid;

    private List<List<Long>> productAddAmountList = new ArrayList<>();

    @NotNull
    private Long status; // 0: reject, 1: waiting for submit, 2: submitted but not paid, 3: order done

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public void addProductAddAmount(List<Long> product) {
        productAddAmountList.add(product);
    }
}
