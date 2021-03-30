package turntabl.io.trade_engine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class ExecutionModel {
    private Timestamp timestamp;
    private Double price;
    private Integer quantity;

    public ExecutionModel(
            @JsonProperty("timestamp") Timestamp timestamp,
            @JsonProperty("price") Double price,
            @JsonProperty("quantity") Integer quantity) {
        this.timestamp = timestamp;
        this.price = price;
        this.quantity = quantity;
    }

    public ExecutionModel() {
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ExecutionModel{" +
                "timestamp=" + timestamp +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
