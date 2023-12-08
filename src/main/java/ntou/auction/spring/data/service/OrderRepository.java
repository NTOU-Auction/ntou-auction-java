package ntou.auction.spring.data.service;

import jakarta.transaction.Transactional;
import ntou.auction.spring.data.entity.Order;
import ntou.auction.spring.data.entity.Shoppingcart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findById(Long id);
    @Query(value = "select * from orders o where o.buyerid = ?1", nativeQuery = true)
    List<Order> findAllByBuyerid(Long buyer);

    @Query(value = "select * from orders o where o.buyerid = ?1 and o.status = 0", nativeQuery = true)
    List<Order> findRejectByBuyerid(Long buyer);

    @Query(value = "select * from orders o where o.buyerid = ?1 and o.status = 1", nativeQuery = true)
    List<Order> findWaitingByBuyerid(Long buyer);

    @Query(value = "select * from orders o where o.buyerid = ?1 and o.status = 2", nativeQuery = true)
    List<Order> findSubmittedByBuyerid(Long buyer);

    @Query(value = "select * from orders o where o.buyerid = ?1 and o.status = 3", nativeQuery = true)
    List<Order> findDoneByBuyerid(Long buyer);

    @Query(value = "select * from orders o where o.sellerid = ?1 and o.status = 1", nativeQuery = true)
    List<Order> findWaitingBySellerid(Long seller);
    @Modifying
    @Query(value = "insert into shoppingcart(userId, productId) values (?1, ?2)", nativeQuery = true)
    public void addShoppingCart(Long userId, List<Long> productId);
}
