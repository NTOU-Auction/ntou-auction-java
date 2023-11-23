package ntou.auction.spring.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.data.entity.Product;
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
    @GetMapping("/{userId}")
    @ResponseBody
    Shoppingcart getUserShoppingcart(@PathVariable long userId) {
        return shoppingcartService.getByUserId(userId);
    }

    @GetMapping("/view")
    @ResponseBody
    List<Product> getProduct() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Shoppingcart userShoppingcart = getUserShoppingcart(userId);
        if(userShoppingcart==null) return null;
        List<Product> result = new ArrayList<>();
        for(Long productId: userShoppingcart.getProductId()) {
            result.add(productService.getID(productId));
        }
        return result;
    }
    @PostMapping("/add")
    ResponseEntity<Map<String,String>> addProduct(@Valid @RequestBody ShoppingcartRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Long addProductId = request.getProductId();
        shoppingcartService.addProductByUserId(userId, addProductId);
        return ResponseEntity.ok(successMessage);
    }

    @DeleteMapping("/delete")
    ResponseEntity<Map<String,String>> deleteProduct(@Valid @RequestBody ShoppingcartRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Long addProductId = request.getProductId();
        boolean result = shoppingcartService.deleteProductByUserId(userId, addProductId);
        return (result?ResponseEntity.ok(successMessage):ResponseEntity.ok(failMessage));
    }

    @DeleteMapping("/deleteall")
    ResponseEntity<Map<String,String>> deleteAllProduct() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        boolean result = shoppingcartService.deleteShoppingcartByUserId(userId);
        if(!result) return ResponseEntity.ok(failMessage);
        return ResponseEntity.ok(successMessage);
    }
}
