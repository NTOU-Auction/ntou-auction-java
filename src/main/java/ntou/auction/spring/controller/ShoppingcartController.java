package ntou.auction.spring.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.data.entity.ProductClassificatedBySeller;
import ntou.auction.spring.data.entity.Shoppingcart;
import ntou.auction.spring.data.entity.ShoppingcartRequest;
import ntou.auction.spring.data.service.ProductService;
import ntou.auction.spring.data.service.ShoppingcartService;
import ntou.auction.spring.data.service.UserIdentity;
import ntou.auction.spring.data.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/shoppingcart", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class ShoppingcartController {
    private final ShoppingcartService shoppingcartService;
    private final ProductService productService;
    private static final Map<String,String> successMessage = Collections.singletonMap("message","成功");
    private static final Map<String,String> failMessage = Collections.singletonMap("message","好像發生了什麼錯誤，請檢查一下腦袋");

    private final UserService userService;

    private final UserIdentity userIdentity;

    public ShoppingcartController(ShoppingcartService shoppingcartService, ProductService productService, UserService userService, UserIdentity userIdentity) {
        this.shoppingcartService = shoppingcartService;
        this.productService = productService;
        this.userService = userService;
        this.userIdentity = userIdentity;
    }
    @GetMapping("/shoppingcart")
    @ResponseBody
    List<Shoppingcart> getShoppingcartProfile() { return shoppingcartService.list(); }

    @GetMapping("/view")
    @ResponseBody
    ProductClassificatedBySeller getProduct() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Shoppingcart userShoppingcart = shoppingcartService.getByUserId(userId);
        if(userShoppingcart==null) return null;
        ProductClassificatedBySeller result = new ProductClassificatedBySeller();
        for(Map.Entry<Long, Long> product: userShoppingcart.getProductItems().entrySet()) {
            result.addProduct(productService.getID(product.getKey()), product.getValue());
        }
        return result;
    }
    @PostMapping("/add")
    ResponseEntity<Map<String,String>> addProduct(@Valid @RequestBody ShoppingcartRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Long addProductId = request.getProductId();
        Long amount = request.getAmount();
        shoppingcartService.addProductByUserId(userId, addProductId, amount==null?1L:amount);
        return ResponseEntity.ok(successMessage);
    }

    @DeleteMapping("/decrease")
    ResponseEntity<Map<String,String>> decreaseProduct(@Valid @RequestBody ShoppingcartRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Long addProductId = request.getProductId();
        Long amount = request.getAmount();
        boolean result = shoppingcartService.decreaseProductByUserId(userId, addProductId, amount==null?1L:amount);
        return (result?ResponseEntity.ok(successMessage):ResponseEntity.badRequest().body(failMessage));
    }

    @DeleteMapping("/delete")
    ResponseEntity<Map<String,String>> deleteProduct(@Valid @RequestBody ShoppingcartRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Long addProductId = request.getProductId();
        boolean result = shoppingcartService.deleteProductByUserId(userId, addProductId);
        return (result?ResponseEntity.ok(successMessage):ResponseEntity.badRequest().body(failMessage));
    }

    @DeleteMapping("/deleteall")
    ResponseEntity<Map<String,String>> deleteAllProduct() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        boolean result = shoppingcartService.deleteShoppingcartByUserId(userId);
        if(!result) return ResponseEntity.badRequest().body(failMessage);
        return ResponseEntity.ok(successMessage);
    }
}
