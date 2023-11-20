package ntou.auction.spring.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.data.entity.Product;
import ntou.auction.spring.data.entity.ProductRequest;
import ntou.auction.spring.data.entity.ProductRequestGet;
import ntou.auction.spring.data.service.ProductService;
import ntou.auction.spring.data.service.UserIdentity;
import ntou.auction.spring.data.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/product")
    ResponseEntity<Map<String,String>> postProduct(@Valid @RequestBody ProductRequest request){   //productrequest的限制

        Map<String,String> successMessage = Collections.singletonMap("message","成功上架");


        Product product = new Product();

        product.setProductName(request.getProductName());
        product.setProductDescription(request.getProductDescription());
        product.setPrice(request.getPrice());
        product.setIsFixedPrice(request.getIsFixedPrice());
        product.setProductImage(request.getProductImage());
        product.setProductType(request.getProductType());

        product.setSellerID(20231120L);
        if(request.getIsFixedPrice()){
            product.setCurrentPrice(null);
            product.setUpsetPrice(null);
        }
        else if(!request.getIsFixedPrice()){
            product.setCurrentPrice(request.getCurrentPrice());
            product.setUpsetPrice(request.getUpsetPrice());
        }
        product.setSellerID(userService.findByUsername(userIdentity.getUsername()).getId());


        productService.store(product);
        return ResponseEntity.ok(successMessage);
    }

}
