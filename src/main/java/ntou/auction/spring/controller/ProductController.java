package ntou.auction.spring.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.data.entity.Product;
import ntou.auction.spring.data.entity.ProductRequest;
import ntou.auction.spring.data.service.ProductService;
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



    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/product")
    @ResponseBody
    public List<Product>getProductName(@Valid @RequestBody ProductRequest request) {

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
        product.setProductDescription("123");
        product.setPrice(request.getPrice());
        product.setSeller("wei");
        product.setIsFixedPrice(request.getIsFixedPrice());
        product.setUpsetPrice(1000L);
        product.setProductImage("123");
        product.setProductType(request.getProductType());
        product.setCurrentPrice(123L);

        productService.store(product);
        return ResponseEntity.ok(successMessage);
    }

}
