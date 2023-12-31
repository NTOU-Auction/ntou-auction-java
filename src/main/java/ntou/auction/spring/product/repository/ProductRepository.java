package ntou.auction.spring.product.repository;

import ntou.auction.spring.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    Product findByProductName(String productName);

    List <Product> findAllByIsFixedPriceFalseAndIsAuctionFalse();

    List <Product> findAllByVisibleTrue();
    Product findById(long id);

    @Query("select p from Product p " +
            "where p.productName like %?1% and p.visible = true") //string-like
    List<Product> findAllByFuzzyProductName(@Param("productName") String productName);
    // ?1:productName

    List<Product> findBySellerIDAndVisibleTrue(long ID);

    List<Product> findAllByProductTypeAndVisibleTrue(String productType);


}
