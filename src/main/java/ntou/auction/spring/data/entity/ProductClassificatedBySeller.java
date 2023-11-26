package ntou.auction.spring.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductClassificatedBySeller {
    private Map<String, List<ProductAddAmount>> ProductShowBySeller = new HashMap<>();
    public void addProduct(Product product, Long amount) {
        String seller = product.getSeller();
        if(!ProductShowBySeller.containsKey(seller)) ProductShowBySeller.put(seller, new ArrayList<>());
        List<ProductAddAmount> getProducts = ProductShowBySeller.get(seller);
        getProducts.add(new ProductAddAmount(product, amount));
        ProductShowBySeller.replace(seller, getProducts);
    }
}
