package ntou.auction.spring.controller;

import ntou.auction.spring.data.entity.Product;
import ntou.auction.spring.data.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/product", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    private final ProductService productService;



    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/products/{productName}")
    @ResponseBody
    Product getProductByProductName(@PathVariable String productName) {
        return productService.findByProductName(productName);
    }

    @GetMapping("/products")
    @ResponseBody
    List<Product> getProductProfile() {
        return productService.list();
    }

    @GetMapping("admin")
    public String helloAdmin() {
        return "Hello Admin";
    }
}
