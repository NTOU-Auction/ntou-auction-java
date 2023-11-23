package ntou.auction.spring.data.service;

import ntou.auction.spring.data.entity.Product;
import ntou.auction.spring.data.entity.Shoppingcart;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
        if(repository.findByUserid(userId).isEmpty()) return false;
        repository.deleteByUserid(userId);
        return true;
    }

    public void addProductByUserId(Long userId, Long productId) {
        Shoppingcart userShoppingcart = getByUserId(userId);
        if(userShoppingcart==null) {
            List<Long> product = new ArrayList<>();
            Shoppingcart newShoppingcart = new Shoppingcart(userId, product);
            repository.save(newShoppingcart);
            userShoppingcart = getByUserId(userId);
        }
        userShoppingcart.addProductId(productId);
        repository.save(userShoppingcart);
    }

    public boolean deleteProductByUserId(Long userId, Long productId) {
        Shoppingcart userShoppingcart = getByUserId(userId);
        boolean result = userShoppingcart.deleteProduct(productId);
        if(!result) return false;
        repository.save(userShoppingcart);
        return true;
    }
}
