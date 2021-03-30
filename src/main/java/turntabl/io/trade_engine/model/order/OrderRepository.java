package turntabl.io.trade_engine.model.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
   // Optional<Order> findOrderByOrderStatus(String status);
//    @Query("SELECT o FROM porders o WHERE o.order_status != 'done'")
//    List<Order> getAllIncompleteOrders();
}
