package ntou.auction.spring.data.service;

import jakarta.transaction.Transactional;
import ntou.auction.spring.data.entity.Shoppingcart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ShoppingcartRepository extends JpaRepository<Shoppingcart, Long>, JpaSpecificationExecutor<Shoppingcart> {
    @Modifying
    @Query(value = "insert into shoppingcart(userId, productId) values (?1, ?2)", nativeQuery = true)
    public void addShoppingCart(Long userId, List<Long> productId);

    Shoppingcart findById(long id);
    //@Query(value = "select s from Shoppingcart s where s.id = ?1")
    Optional<Shoppingcart> findByUserid(Long id);
    @Transactional
    public List<Shoppingcart> deleteByUserid(Long UserId);
}
