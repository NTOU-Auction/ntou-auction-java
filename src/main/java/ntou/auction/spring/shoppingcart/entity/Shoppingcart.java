package ntou.auction.spring.shoppingcart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ntou.auction.spring.util.AbstractEntity;

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

    public Long decreaseProduct(Long product, Long amount) {
        if (productItems.get(product) == null) return 0L;
        if (productItems.get(product) == 0L) {
            productItems.remove(product);
            return 0L;
        }
        if(productItems.get(product) < amount) return 1L;
        productItems.replace(product, productItems.get(product) - amount);
        if (productItems.get(product) == 0L) productItems.remove(product);
        return 2L;
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

    public boolean checkIsEnoughAmountInProductItems(Long product, Long amount) {
        if(amount.equals(0L)) return true; // this may not be happened
        if(!productItems.containsKey(product)) return false;
        return productItems.get(product) >= amount;
    }
}
