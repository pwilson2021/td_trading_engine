package turntabl.io.trade_engine.model.order;

import turntabl.io.client_connectivity.DateAudit;
import turntabl.io.client_connectivity.portfolio.Portfolio;
import turntabl.io.client_connectivity.product.Product;
import turntabl.io.client_connectivity.user.User;

import javax.persistence.*;

@Entity(name = "Order")
@Table(name="porders")
public class Order extends DateAudit {
    @Id
    @SequenceGenerator(
            name = "order_sequence",
            sequenceName = "order_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_sequence"
    )

    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    private double price;
    private int quantity;
    private String order_type;

    private String order_status;


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", referencedColumnName = "id")
    private Portfolio portfolio;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    public Order(double price, int quantity, String order_type, String order_status, User user, Portfolio portfolio, Product product) {
        this.price = price;
        this.quantity = quantity;
        this.order_type = order_type;
        this.order_status = order_status;
        this.user = user;
        this.portfolio = portfolio;
        this.product = product;
    }

    public Order() {

    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }
}