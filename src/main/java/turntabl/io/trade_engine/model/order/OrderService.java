package turntabl.io.trade_engine.model.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turntabl.io.trade_engine.TradeEngineLogic;
import turntabl.io.trade_engine.publish.TradeEngineRabbitMqSender;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    TradeEngineRabbitMqSender tradeEngineRabbitMqSender;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getOrders() { return orderRepository.findAll();}

    public void addNewOrder(Order order) {
        orderRepository.save(order);
    }

    public void deleteOrder(Integer id) {
        boolean exists = orderRepository.existsById(id);
        if (!exists) {
            throw new IllegalStateException("order with id " + id + " doesn't exist");
        }
        orderRepository.deleteById(id);
    }

    @Transactional
    public void updateOrder(Integer orderId, Double price, int quantity, String order_status, int fulfilled_quantity) {
        Order order =  orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException(
                        "Order with id "+ orderId + " does not exist"
                ));
        System.out.println(order.getId());
        if (price != null &&  !Objects.equals(order.getPrice() , price)) {
            order.setPrice(price);
        }

        if (quantity != 0 &&  !Objects.equals(order.getQuantity() , quantity)) {
            order.setQuantity(quantity);
        }

        if (order_status != null &&  !Objects.equals(order.getOrder_status() , order_status)) {
            order.setOrder_status(order_status);
        }
        if (fulfilled_quantity != 0 &&  !Objects.equals(order.getQtyFulfilled() , fulfilled_quantity)) {
            order.setQtyFulfilled(fulfilled_quantity);
        }
    }

    public Order findOrder(int order_id) {
        boolean exists = orderRepository.existsById(order_id);
        if (!exists) {
            throw new IllegalStateException("order with id " + order_id + " doesn't exist");
        }

        Optional<Order> order = orderRepository.findById(order_id);
        return order.get();
    }

    public List<Order> findIncompleteOrders() {
        List<Order> orderList = orderRepository.findAll();
        return orderList.stream().filter(order -> !order.getOrder_status().equals("completed") && !order.getOrder_status().equals("cancelled")).collect(Collectors.toList());
    }

        public void testTradeEngineController(int id) {
        Order order = findOrder(id);
        TradeEngineLogic tradeEngineLogic = new TradeEngineLogic();
        tradeEngineLogic.tradeEngineLogic(order);
        tradeEngineLogic.setTradeEngineRabbitMqSender(tradeEngineRabbitMqSender);
        System.out.println("im working");
    }
}
