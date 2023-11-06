package ntou.auction.spring.data.service;
import ntou.auction.spring.data.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;


    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product get(String productName) {
        return repository.findByProductName(productName);
    }
/*
    public Product update(Product entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        Optional<Product> maybeUser = repository.findById(id);
        if (maybeUser.isPresent()) {
            Product product = maybeUser.get();
            repository.deleteById(id);
        }
    }
*/
    public List<Product> list() {
        return repository.findAll();
    }



    public int count() {
        return (int) repository.count();
    }

    public void store(Product product) {
        repository.save(product);
    }


    public boolean isProductNameNonExist(String productName) {
        return repository.findByProductName(productName) == null;
    }
/*
    public boolean isEmailNonExist(String email) {
        return repository.findAllByEmail(email).isEmpty();
    }
*/
    public Product findByProductName(String productName) {
        return repository.findByProductName(productName);
    }

}
