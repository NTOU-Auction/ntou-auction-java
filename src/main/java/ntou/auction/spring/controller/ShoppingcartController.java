package ntou.auction.spring.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.data.entity.*;
import ntou.auction.spring.data.service.ProductService;
import ntou.auction.spring.data.service.ShoppingcartService;
import ntou.auction.spring.data.service.UserIdentity;
import ntou.auction.spring.data.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/shoppingcart", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class ShoppingcartController {
    private final ShoppingcartService shoppingcartService;
    private final ProductService productService;
    private static final Map<String,String> successMessage = Collections.singletonMap("message","成功");
    private static final Map<String,String> failMessage = Collections.singletonMap("message","好像發生了什麼錯誤，請檢查一下腦袋");

    private static final Map<String,String> ErrorIdMessage = Collections.singletonMap("message","沒這商品，跟你女友一樣不存在");

    private static final Map<String,String> ErrorAmountZeroMessage = Collections.singletonMap("message","你知道這商品的數量會變得跟你腦袋一樣是負的嗎");

    private static final Map<String,String> ErrorAmountExceedMessage = Collections.singletonMap("message","商品滿書來了");

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
        Map<String, List<ProductAddAmount>> result = new HashMap<>();
        for(Map.Entry<Long, Long> product: userShoppingcart.getProductItems().entrySet()) {
            System.out.println(product.getKey() + " " + product.getValue());
            Product nowProduct = productService.getID(product.getKey());
            Long sellerId = nowProduct.getSellerID();
            Optional<User> sellerUser = userService.get(sellerId);
            if (sellerUser.isEmpty()) {
                shoppingcartService.deleteProductByUserId(userId, product.getKey());
                continue;
            }
            String sellerName = sellerUser.get().getUsername();
            if (!result.containsKey("@" + sellerName)) {
                result.put("@" + sellerName, new ArrayList<>());
            }
            List<ProductAddAmount> getProducts = result.get("@" + sellerName);
            if (getProducts == null) getProducts = new ArrayList<>();
            getProducts.add(new ProductAddAmount(nowProduct, product.getValue()));
            result.replace(sellerName, getProducts);
        }
        return new ProductClassificatedBySeller(result);
    }
    @PostMapping("/add")
    ResponseEntity<Map<String,String>> addProduct(@Valid @RequestBody ShoppingcartRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Long addProductId = request.getProductId();
        Long amount = request.getAmount();
        if(productService.getID(addProductId)==null) return ResponseEntity.badRequest().body(ErrorIdMessage);
        boolean result = shoppingcartService.addProductByUserId(userId, addProductId, amount==null?1L:amount);
        return result?ResponseEntity.ok(successMessage):ResponseEntity.badRequest().body(ErrorAmountExceedMessage);
    }

    @DeleteMapping("/decrease")
    ResponseEntity<Map<String,String>> decreaseProduct(@Valid @RequestBody ShoppingcartRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Long addProductId = request.getProductId();
        Long amount = request.getAmount();
        Long result = shoppingcartService.decreaseProductByUserId(userId, addProductId, amount==null?1L:amount);
        // 0: exist error, 1: amount error, 2: OK
        if(result.equals(0L)) return ResponseEntity.badRequest().body(ErrorIdMessage); //shoppingcart does not exist
        if(result.equals(1L)) return ResponseEntity.badRequest().body(ErrorAmountZeroMessage); //amount error
        return ResponseEntity.ok(successMessage);
    }


    @DeleteMapping("/delete")
    ResponseEntity<Map<String,String>> deleteProduct(@Valid @RequestBody ShoppingcartRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Long addProductId = request.getProductId();
        boolean result = shoppingcartService.deleteProductByUserId(userId, addProductId);
        return (result?ResponseEntity.ok(successMessage):ResponseEntity.badRequest().body(ErrorIdMessage));
    }

    @DeleteMapping("/deleteall")
    ResponseEntity<Map<String,String>> deleteAllProduct() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        boolean result = shoppingcartService.deleteShoppingcartByUserId(userId);
        if(!result) return ResponseEntity.badRequest().body(failMessage);
        return ResponseEntity.ok(successMessage);
    }
}
