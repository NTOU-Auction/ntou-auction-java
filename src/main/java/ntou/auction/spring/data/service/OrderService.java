package ntou.auction.spring.data.service;

import ntou.auction.spring.data.entity.Order;
import ntou.auction.spring.data.entity.OrderWithProductDetail;
import ntou.auction.spring.data.entity.ProductAddAmount;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {
    private final OrderRepository repository;

    private final ProductService productService;

    private final ShoppingcartService shoppingcartService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public OrderService(OrderRepository repository, ProductService productService, ShoppingcartService shoppingcartService) {
        this.repository = repository;
        this.productService = productService;
        this.shoppingcartService = shoppingcartService;
    }
    public Order findOrderById(Long Id) {
        return repository.findById(Id).orElse(null);
    }
    public List<Order> findAllByBuyerId(Long buyerId) {
        return repository.findAllByBuyerid(buyerId);
    }

    public List<Order> findRejectByBuyerId(Long buyerId) {
        return repository.findRejectByBuyerid(buyerId);
    }

    public List<Order> findWaitingByBuyerId(Long buyerId) {
        return repository.findWaitingByBuyerid(buyerId);
    }

    public List<Order> findSubmittedByBuyerId(Long buyerId) {
        return repository.findSubmittedByBuyerid(buyerId);
    }

    public List<Order> findDoneByBuyerId(Long buyerId) {
        return repository.findDoneByBuyerid(buyerId);
    }

    public List<Order> findWaitingBySellerId(Long sellerId) {
        return repository.findWaitingBySellerid(sellerId);
    }

    public Long submitOrder(Long orderId, Long userId) {
        // for status -> 0: reject, 1: waiting for submit, 2: submitted but not paid, 3: order done
        // for return -> 0: orderNotFound, 1: statusError, 2: idError, 3: success, -1: expired
        Order getorder = repository.findById(orderId).orElse(null);
        if(getorder == null) return 0L;
        if(!getorder.getStatus().equals(1L)) return 1L;
        if(!Objects.equals(findOrderById(orderId).getSellerid(), userId)) return 2L;
        getorder.setStatus(2L);
        repository.save(getorder);
        return 3L;
    }

    public Long rejectOrder(Long orderId, Long userId) {
        // 0: reject, 1: waiting for submit, 2: submitted but not paid, 3: order done
        // for return -> 0: orderNotFound, 1: statusError, 2: idError, 3: success, -1: expired
        Order getorder = repository.findById(orderId).orElse(null);
        if(getorder == null) return 0L;
        if(!getorder.getStatus().equals(1L)) return 1L;
        if(!Objects.equals(findOrderById(orderId).getSellerid(), userId)) return 2L;
        getorder.setStatus(0L);
        repository.save(getorder);
        return 3L;
    }

    public Long cancelOrder(Long orderId, Long userId) {
        // 0: reject, 1: waiting for submit, 2: submitted but not paid, 3: order done
        // for return -> 0: orderNotFound, 1: statusError, 2: idError, 3: success, -1: expired
        Order getorder = repository.findById(orderId).orElse(null);
        if(getorder == null) return 0L;
        if(getorder.getStatus().equals(3L) || getorder.getStatus().equals(0L)) return 1L;
        if(!Objects.equals(findOrderById(orderId).getBuyerid(), userId)) return 2L;
        if(Duration.between(getorder.getUpdateTime(), LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter)).toSeconds()>(86400*7L)) return -1L;
        getorder.setStatus(0L);
        repository.save(getorder);
        return 3L;
    }
    // make order be done
    public Long doneOrder(Long orderId, Long userId) {
        // 0: reject, 1: waiting for submit, 2: submitted but not paid, 3: order done
        // for return -> 0: orderNotFound, 1: statusError, 2: idError, 3: success, -1: expired
        Order getorder = repository.findById(orderId).orElse(null);
        if(getorder == null) return 0L;
        if(!getorder.getStatus().equals(2L)) return 1L;
        if(!Objects.equals(findOrderById(orderId).getSellerid(), userId)) return 2L;
        getorder.setStatus(3L);
        repository.save(getorder);
        return 3L;
    }

    public boolean addOrder(Order order) {
        boolean check = checkIsSameSeller(order.getProductAddAmountList());
        if(!check) return false;
        repository.save(order);
        return true;
    }

    private boolean checkIsSameSeller(List<List<Long>> list) {
        Set<Long> check = new HashSet<>();
        for(List<Long> productAddAmount: list) {
            check.add(productService.getID(productAddAmount.get(0)).getSellerID());
        }
        return check.size()==1;
    }

    public List<OrderWithProductDetail> orderToOrderWithProductDetail(List<Order> getOrder) {
        List<OrderWithProductDetail> result = new ArrayList<>();
        for(Order order: getOrder) {
            OrderWithProductDetail addOrder = new OrderWithProductDetail();
            addOrder.setSellerid(order.getSellerid());
            addOrder.setBuyerid(order.getBuyerid());
            addOrder.setUpdateTime(order.getUpdateTime());
            addOrder.setStatus(order.getStatus());
            addOrder.setOrderid(order.getId());
            List<ProductAddAmount> temp = new ArrayList<>();
            for (List<Long> product : order.getProductAddAmountList()) {
                temp.add(new ProductAddAmount(productService.getID(product.get(0)), product.get(1)));
            }
            addOrder.setProductAddAmountList(temp);
            result.add(addOrder);
        }
        return result;
    }

    public boolean addAmountToProduct(Order order) {
        // (order == null) this may not be happened
        if(order==null) return false;
        // add product amount with amount
        for(List<Long> eachProduct: order.getProductAddAmountList()) {
            productService.productAmountIncrease(eachProduct.get(0), eachProduct.get(1));
        }
        return true;
    }

}
