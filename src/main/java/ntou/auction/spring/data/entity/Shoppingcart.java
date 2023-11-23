package ntou.auction.spring.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shoppingcart")
public class Shoppingcart extends AbstractEntity {

    @NotNull
    private Long userid;

    @ElementCollection
    @CollectionTable(name = "productId")
    private List<Long> productId = new ArrayList<>();

    public @NotNull Long getUserId() {
        return userid;
    }
    public void addProductId(Long product) {
        productId.add(product);
    }

    public boolean deleteProduct(Long product) {
        return productId.remove(product);
    }
}
