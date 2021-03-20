package turntabl.io.trade_engine.model.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import turntabl.io.client_connectivity.portfolio.Portfolio;
import turntabl.io.client_connectivity.portfolio.PortfolioService;
import turntabl.io.client_connectivity.product.Product;
import turntabl.io.client_connectivity.product.ProductService;
import turntabl.io.client_connectivity.user.User;
import turntabl.io.client_connectivity.user.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final PortfolioService portfolioService;
    private final ProductService productService;

    @Autowired
    public OrderController(OrderService orderService,
                           PortfolioService portfolioService,
                            UserService userService,
                           ProductService productService
                           ) {
        this.orderService = orderService;
        this.userService = userService;
        this.portfolioService = portfolioService;
        this.productService = productService;
    }

    @GetMapping
    public List<Order> getOrders() { return orderService.getOrders() ;}

    @PostMapping
    public void registerNewOrder(
            @RequestParam(name="price") Double price,
            @RequestParam(name="quantity") int quantity,
            @RequestParam(name="order_type") String order_type,
            @RequestParam(name="order_status") String order_status,
            @RequestParam(name="user_id") int user_id,
            @RequestParam(name="portfolio_id") int portfolio_id,
            @RequestParam(name="product_id") int product_id
    ) {
        User user = userService.findUserById(user_id);
        Portfolio portfolio = portfolioService.findPortfolioById(portfolio_id);
        Product product = productService.findProductById(product_id);
        Order order = new Order(price, quantity, order_type, order_status, user, portfolio, product);
        orderService.addNewOrder(order);
    }

    @DeleteMapping(path = "{orderId}")
    public void deleteOrder(@PathVariable("orderId") Integer orderId) {orderService.deleteOrder(orderId);}


    public void updateOrder(
            @PathVariable("orderId") Integer orderId,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) int quantity,
            @RequestParam(required = false) String orderStatus
    ) {
        orderService.updateOrder(orderId, price, quantity, orderStatus);
    }
}
