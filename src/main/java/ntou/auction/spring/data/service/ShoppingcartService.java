package ntou.auction.spring.data.service;

import ntou.auction.spring.data.entity.Product;
import ntou.auction.spring.data.entity.Shoppingcart;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingcartService {
    private final ShoppingcartRepository repository;
    private final ProductService productService;

    public ShoppingcartService(ShoppingcartRepository repository, ProductService productService) {
        this.repository = repository;
        this.productService = productService;
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

    public boolean addProductByUserId(Long userId, Long productId, Long amount) {
        Shoppingcart userShoppingcart = getByUserId(userId);
        if (userShoppingcart == null) {
            Map<Long, Long> product = new HashMap<>();
            Shoppingcart newShoppingcart = new Shoppingcart(userId, product);
            repository.save(newShoppingcart);
            userShoppingcart = getByUserId(userId);
        }
        Product product = productService.getID(productId);
        if (product == null) return false; //already been checked
        Long alreadyAmount = userShoppingcart.getProductItems().get(productId) == null ? 0L : userShoppingcart.getProductItems().get(productId);
        if (alreadyAmount + amount > product.getProductAmount()) return false;
        userShoppingcart.addProductId(productId, amount);
        repository.save(userShoppingcart);
        return true;
    }

    public Long decreaseProductByUserId(Long userId, Long productId, Long amount) {
        Shoppingcart userShoppingcart = getByUserId(userId);
        if (userShoppingcart == null) return 0L;
        Long result = userShoppingcart.decreaseProduct(productId, amount);
        if (!result.equals(2L)) return result;
        repository.save(userShoppingcart);
        if (userShoppingcart.getProductItems().isEmpty()) repository.deleteByUserid(userId);
        return 2L;
    }

    public boolean deleteProductByUserId(Long userId, Long productId) {
        Shoppingcart userShoppingcart = getByUserId(userId);
        if (userShoppingcart == null) return false;
        boolean result = userShoppingcart.deleteProduct(productId);
        if (!result) return false;
        repository.save(userShoppingcart);
        if (userShoppingcart.getProductItems().isEmpty()) repository.deleteByUserid(userId);
        return true;
    }

    public boolean checkIsEnoughAmount(Long userId, Long productId, Long amount) {
        Shoppingcart userShoppingcart = getByUserId(userId);
        if (userShoppingcart == null) return false;
        return userShoppingcart.checkIsEnoughAmountInProductItems(productId, amount);
    }

    public Long checkIsProductAllInShoppingCart(List<List<Long>> order, Long userid) {
        // -1: format error, 0: false, 1: true
        for(List<Long> product: order) {
            if(product.size()!=2) return -1L;
            if(!checkIsEnoughAmount(userid, product.get(0), product.get(1))) {
                return 0L;
            }
        }
        return 1L;
    }
}
