package ntou.auction.spring.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

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
    private Map<Long, Long> productItems = new HashMap<>();

    public @NotNull Long getUserId() {
        return userid;
    }

    public void addProductId(Long product, Long amount) {
        if(!productItems.containsKey(product)) productItems.put(product, 0L);
        productItems.replace(product, productItems.get(product)+amount);
    }

    public boolean decreaseProduct(Long product, Long amount) {
        if (productItems.get(product) == null) return false;
        if (productItems.get(product) == 0L) {
            productItems.remove(product);
            return false;
        }
        if(productItems.get(product) < amount) return false;
        productItems.replace(product, productItems.get(product) - amount);
        if (productItems.get(product) == 0L) productItems.remove(product);
        return true;
    }

    public boolean deleteProduct(Long product) {
        if (productItems.get(product) == null) return false;
        if (productItems.get(product) == 0L) {
            productItems.remove(product);
            return false;
        }
        productItems.remove(product);
        return true;
    }
}
