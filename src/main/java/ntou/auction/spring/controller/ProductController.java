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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/api/v1/product", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    private final ProductService productService;
    private final UserIdentity userIdentity;
    private final UserService userService;
    private final ShoppingcartService shoppingcartService;


    public ProductController(ProductService productService, UserIdentity userIdentity, UserService userService, ShoppingcartService shoppingcartService) {
        this.productService = productService;
        this.userIdentity = userIdentity;
        this.userService = userService;
        this.shoppingcartService = shoppingcartService;
    }


    @GetMapping("/product")
    @ResponseBody
    public List<Product>getProductName(@Valid @RequestBody ProductRequestGet request) {

        long type =Integer.parseInt(request.getSearchType());

        if(type == 1) { //find by name
            String pn = request.getProductName();
            return productService.findByProductName(pn);
        }

        else if(type == 2){ //find by classification
            String pt = request.getProductType();
            return productService.findByProductClassification(pt);
        }

        return productService.list();
    }


    @GetMapping("/products")
    @ResponseBody
    List<Product> getProductProfile() {
        return productService.list();
    }

    @GetMapping("/{ID}")
    @ResponseBody
    Product getProduct(@PathVariable long ID) {
        return productService.getID(ID);
    }

    @PostMapping("/fixedproduct")
    ResponseEntity<Map<String,String>> postProduct(@Valid @RequestBody PostFixedPriceProductRequest request){   //productrequest的限制

        Map<String,String> successMessage = Collections.singletonMap("message","成功上架");


        Product product = new Product();

        product.setProductName(request.getProductName());
        product.setProductDescription(request.getProductDescription());
        product.setIsFixedPrice(true);
        product.setProductImage(request.getProductImage());
        product.setProductType(request.getProductType());
        product.setCurrentPrice(request.getCurrentPrice());
        product.setUpsetPrice(null);
        product.setBidIncrement(null);
        product.setProductAmount(request.getProductAmount());
        product.setSellerID(userService.findByUsername(userIdentity.getUsername()).getId());

        product.setUpdateTime(LocalDateTime.now());


        productService.store(product);
        return ResponseEntity.ok(successMessage);
    }

    @PostMapping("/nonfixedproduct")
    ResponseEntity<Map<String,String>> postProduct(@Valid @RequestBody PostNonFixedPriceProductRequest request){   //productrequest的限制

        Map<String,String> successMessage = Collections.singletonMap("message","成功上架");
        Map<String,String> fail = Collections.singletonMap("message","截止時間錯誤");

        Product product = new Product();

        product.setProductName(request.getProductName());
        product.setProductDescription(request.getProductDescription());
        product.setIsFixedPrice(false);
        product.setProductImage(request.getProductImage());
        product.setProductType(request.getProductType());
        product.setCurrentPrice(request.getUpsetPrice());
        product.setUpsetPrice(request.getUpsetPrice());
        product.setBidIncrement(request.getBidIncrement());
        product.setProductAmount(1L);

        LocalDateTime now = LocalDateTime.now();

        product.setSellerID(userService.findByUsername(userIdentity.getUsername()).getId());
        product.setUpdateTime(now);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(request.getFinishTime(), formatter);

        if(!now.isBefore(dateTime)){
            return ResponseEntity.badRequest().body(fail);
        }
        product.setFinishTime(dateTime);

        productService.store(product);
        return ResponseEntity.ok(successMessage);
    }

    @PatchMapping("/bid") //商品ID 出價。出價也需傳入token
    ResponseEntity<Map<String,String>> bidProduct(@Valid @RequestBody BidRequest request){

        Map<String,String> successMessage = Collections.singletonMap("message","成功出價");
        Map<String,String> failMessage = Collections.singletonMap("message","出價不合理，出價需比當前最高價高" + productService.getID(request.getProductID()).getBidIncrement());
        Map<String,String> expired = Collections.singletonMap("message","競標已結束");

        LocalDateTime now = LocalDateTime.now();
        if(!now.isBefore(productService.getID(request.getProductID()).getFinishTime())){
            return ResponseEntity.badRequest().body(expired);
        }

        if(!productService.isBidReasonable(request.getBid(), request.getProductID())) {
            return ResponseEntity.badRequest().body(failMessage);
        }
        System.out.println(userIdentity.getUsername());
        productService.bid(request.getBid(), request.getProductID(),userService.findByUsername(userIdentity.getUsername()).getId());

        return ResponseEntity.ok(successMessage);
    }

    @PostMapping("/buy")
    ResponseEntity<Map<String,String>> buyProduct(@Valid @RequestBody BuyProductRequest request){

        Map<String,String> successMessage = Collections.singletonMap("message","成功加入購物車");
        Map<String,String> notEnoughMessage = Collections.singletonMap("message","買太多嚕");
        Map<String,String> errorMessage = Collections.singletonMap("message","你只能將不二價商品加入購物車");
        Map<String, String> productNotExistMessage = Collections.singletonMap("message", "商品不存在或無法購買");

        // 商品是否存在
        if( productService.getID(request.getProductID())==null){
            return ResponseEntity.badRequest().body(productNotExistMessage);
        }

        // 購物車是空的
        // 只檢查request送來的加入數量
        if (shoppingcartService.getByUserId(userService.findByUsername(userIdentity.getUsername()).getId())==null){
            if (request.getProductAmount() > productService.getID(request.getProductID()).getProductAmount()) {
                return ResponseEntity.badRequest().body(notEnoughMessage);
            } else {
                shoppingcartService.addProductByUserId(userService.findByUsername(userIdentity.getUsername()).getId(), request.getProductID(), request.getProductAmount());
                return ResponseEntity.ok(successMessage);
            }
        }
        // 購物車裡面還沒有要加入的商品
        // 只檢查request送來的加入數量
        if(shoppingcartService.getByUserId(userService.findByUsername(userIdentity.getUsername()).getId()).getProductItems().get(request.getProductID())==null){
            if (request.getProductAmount() > productService.getID(request.getProductID()).getProductAmount()) {
                return ResponseEntity.badRequest().body(notEnoughMessage);
            } else {
                shoppingcartService.addProductByUserId(userService.findByUsername(userIdentity.getUsername()).getId(), request.getProductID(), request.getProductAmount());
                return ResponseEntity.ok(successMessage);
            }
        }

        // 購物車裡面已經有要加入的商品
        // 檢查request送來的加入數量加上原先購物車內的商品數量
        if (request.getProductAmount() + shoppingcartService.getByUserId(userService.findByUsername(userIdentity.getUsername()).getId()).getProductItems().get(request.getProductID()) > productService.getID(request.getProductID()).getProductAmount()) { //要買的數量 > 商品剩餘數量
            return ResponseEntity.badRequest().body(notEnoughMessage);
        }
        if(!productService.getID(request.getProductID()).getIsFixedPrice()){
            return ResponseEntity.badRequest().body(errorMessage);
        }
        //public void addProductByUserId(Long userId, Long productId, Long amount) {
        shoppingcartService.addProductByUserId(userService.findByUsername(userIdentity.getUsername()).getId(), request.getProductID(), request.getProductAmount());
        return ResponseEntity.ok(successMessage);
    }

    @GetMapping("/sellercenter")
    @ResponseBody
    List<Product> getProductInSellerCenter() {
        return productService.findBySellerID(userService.findByUsername(userIdentity.getUsername()).getId());
    }

}
