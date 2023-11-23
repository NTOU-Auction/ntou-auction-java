package ntou.auction.spring.data.service;

import ntou.auction.spring.data.entity.Product;
import ntou.auction.spring.data.entity.Shoppingcart;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingcartService {
    private final ShoppingcartRepository repository;

    public ShoppingcartService(ShoppingcartRepository repository) {
        this.repository = repository;
    }

    public Shoppingcart getByUserId(Long userId) {
        return repository.findByUserid(userId).orElse(null);
    }

    public List<Shoppingcart> list() {
        return repository.findAll();
    }

    public void addUser(Long userId) {
        List<Long> product = new ArrayList<>();
        repository.addShoppingCart(userId, product);
    }

    public int count() {
        return (int) repository.count();
    }

    public boolean deleteShoppingcartByUserId(Long userId) {
        if (repository.findByUserid(userId).isEmpty()) return false;
        repository.deleteByUserid(userId);
        return true;
    }

    public void addProductByUserId(Long userId, Long productId) {
        Shoppingcart userShoppingcart = getByUserId(userId);
        if (userShoppingcart == null) {
            Map<Long, Long> product = new HashMap<>();
            Shoppingcart newShoppingcart = new Shoppingcart(userId, product);
            repository.save(newShoppingcart);
            userShoppingcart = getByUserId(userId);
        }
        userShoppingcart.addProductId(productId, 1L);
        repository.save(userShoppingcart);
    }

    public boolean deleteProductByUserId(Long userId, Long productId) {
        Shoppingcart userShoppingcart = getByUserId(userId);
        if (userShoppingcart == null) return false;
        boolean result = userShoppingcart.deleteProduct(productId, 1L);
        if (!result) return false;
        repository.save(userShoppingcart);
        if (userShoppingcart.getProductItems().isEmpty()) repository.deleteByUserid(userId);
        return true;
    }
}
