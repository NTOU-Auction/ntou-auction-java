package ntou.auction.spring.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.data.entity.*;
import ntou.auction.spring.data.service.ProductService;
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


    public ProductController(ProductService productService,UserIdentity userIdentity,UserService userService) {
        this.productService = productService;
        this.userIdentity = userIdentity;
        this.userService = userService;
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

        if(!productService.isBidReasonable(request.getBid(), request.getProductID())) {
            return ResponseEntity.badRequest().body(failMessage);
        }
        productService.bid(request.getBid(), request.getProductID());
        return ResponseEntity.ok(successMessage);
    }

}
