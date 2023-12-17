package ntou.auction.spring.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.data.entity.FavoriteRequest;
import ntou.auction.spring.data.entity.Product;
import ntou.auction.spring.data.entity.User;
import ntou.auction.spring.data.service.ProductService;
import ntou.auction.spring.data.service.UserIdentity;
import ntou.auction.spring.data.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/account", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;

    private final UserIdentity userIdentity;
    private final ProductService productService;

    public UserController(UserService userService, UserIdentity userIdentity, ProductService productService) {
        this.userService = userService;
        this.userIdentity = userIdentity;
        this.productService = productService;
    }

    // for admin usage
    /*
    @GetMapping("/users/{username}")
    @ResponseBody
    User getByUser(@PathVariable String username) {
        return userService.findByUsername(username);
    }
    */

    @GetMapping("/users")
    @ResponseBody
    User getUserProfileByJWT() {
        return userService.findByUsername(userIdentity.getUsername());
    }

    @GetMapping("/favorite")
    @ResponseBody
    List<Product> getFavorite() {
        Set<Long> favoriteProductIds = userService.getFavoriteProducts(userService.findByUsername(userIdentity.getUsername()).getId());
        List<Product> favoriteProducts = new ArrayList<>();
        for (Long favoriteProductId : favoriteProductIds) {
            favoriteProducts.add(productService.getID(favoriteProductId));
        }
        return favoriteProducts;
    }

    @PostMapping("/favorite")
    @ResponseBody
    ResponseEntity<Map<String, String>> addFavorite(@Valid @RequestBody FavoriteRequest request) {
        Map<String, String> duplicatedProduct = Collections.singletonMap("message", "商品已在我的最愛");
        Map<String, String> productNotFound = Collections.singletonMap("message", "找不到商品");
        Map<String, String> success = Collections.singletonMap("message", "成功將商品加入我的最愛");
        if (productService.getID(request.getProductId()) == null) {
            return ResponseEntity.badRequest().body(productNotFound);
        }
        if (userService.addFavoriteProducts(userService.findByUsername(userIdentity.getUsername()).getId(), request.getProductId())) {
            return ResponseEntity.ok(success);
        } else {
            return ResponseEntity.badRequest().body(duplicatedProduct);
        }
    }

    @DeleteMapping("/favorite")
    @ResponseBody
    ResponseEntity<Map<String, String>> removeFavorite(@Valid @RequestBody FavoriteRequest request) {
        Map<String, String> failed = Collections.singletonMap("message", "因為商品不存在，無法將商品從我的最愛移除");
        Map<String, String> success = Collections.singletonMap("message", "成功將商品從我的最愛移除");
        if (userService.removeFavoriteProducts(userService.findByUsername(userIdentity.getUsername()).getId(), request.getProductId())) {
            return ResponseEntity.ok(success);
        } else {
            return ResponseEntity.badRequest().body(failed);
        }
    }

}
