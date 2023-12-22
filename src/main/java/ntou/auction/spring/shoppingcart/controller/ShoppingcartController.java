package ntou.auction.spring.shoppingcart.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.account.entity.User;
import ntou.auction.spring.product.service.ProductService;
import ntou.auction.spring.shoppingcart.service.ShoppingcartService;
import ntou.auction.spring.account.response.UserIdentity;
import ntou.auction.spring.account.service.UserService;
import ntou.auction.spring.product.entity.Product;
import ntou.auction.spring.shoppingcart.entity.Shoppingcart;
import ntou.auction.spring.shoppingcart.response.ProductAddAmount;
import ntou.auction.spring.shoppingcart.response.ProductClassificatedBySeller;
import ntou.auction.spring.shoppingcart.request.ShoppingcartRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/shoppingcart", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = {"http://localhost:3000","https://ntou-auction.com","http://ntou-auction.com"})
public class ShoppingcartController {
    private final ShoppingcartService shoppingcartService;
    private final ProductService productService;
    private static final Map<String,String> successMessage = Collections.singletonMap("message","成功");
    private static final Map<String,String> failMessage = Collections.singletonMap("message","操作失敗");

    private static final Map<String,String> ErrorIdMessage = Collections.singletonMap("message","商品不存在");

    private static final Map<String,String> ErrorAmountZeroMessage = Collections.singletonMap("message","商品數量不可變為負的");

    private static final Map<String,String> ErrorAmountExceedMessage = Collections.singletonMap("message","加入的商品數量過多");

    private final UserService userService;

    private final UserIdentity userIdentity;

    public ShoppingcartController(ShoppingcartService shoppingcartService, ProductService productService, UserService userService, UserIdentity userIdentity) {
        this.shoppingcartService = shoppingcartService;
        this.productService = productService;
        this.userService = userService;
        this.userIdentity = userIdentity;
    }
    /*
    @GetMapping("/view")
    @ResponseBody
    List<Shoppingcart> getShoppingcartProfile() { return shoppingcartService.list(); }
    */
    @GetMapping("/shoppingcart")
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
    @PostMapping("/increase")
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
