package ntou.auction.spring.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.data.entity.Product;
import ntou.auction.spring.data.entity.ProductRequest;
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


}
