package turntabl.io.trade_engine.model;



import com.fasterxml.jackson.annotation.JsonIgnore;
import turntabl.io.trade_engine.model.order.Order;

import javax.persistence.*;

@Entity
@Table(name="Trade")
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private double price;
    @Column(name = "exchange_order_id", nullable = false)
    private String exchange_order_id;
    private double quantity;
    private String status;

    @JsonIgnore
    @ManyToOne
    private Order order;

    public Trade(int id, double price, double quantity, Order order, String status) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.order = order;
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getExchange_order_id() {
        return exchange_order_id;
    }

    public void setExchange_order_id(String exchange_order_id) {
        this.exchange_order_id = exchange_order_id;
    }
}
