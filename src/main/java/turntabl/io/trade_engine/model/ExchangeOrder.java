package turntabl.io.trade_engine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ExchangeOrder {
    private String product;
    private Integer quantity;
    private Double price;
    private String side;
    private List<ExecutionModel> executions;
    private Integer cumulatitiveQuantity;
    private Integer exchangeId;

    public ExchangeOrder(
            @JsonProperty("product") String product,
            @JsonProperty("quantity") Integer quantity,
            @JsonProperty("price") Double price,
            @JsonProperty("side") String side,
            @JsonProperty("executions") List<ExecutionModel> executions,
            @JsonProperty("cumulatitiveQuantity") Integer cumulatitiveQuantity) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.executions = executions;
        this.cumulatitiveQuantity = cumulatitiveQuantity;
    }

    public ExchangeOrder() {
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public List<ExecutionModel> getExecutions() {
        return executions;
    }

    public void setExecutions(List<ExecutionModel> executions) {
        this.executions = executions;
    }

    public Integer getCumulatitiveQuantity() {
        return cumulatitiveQuantity;
    }

    public void setCumulatitiveQuantity(Integer cumulatitiveQuantity) {
        this.cumulatitiveQuantity = cumulatitiveQuantity;
    }

    public Integer getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(Integer exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Override
    public String toString() {
        return "ExchangeOrder{" +
                "product='" + product + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side='" + side + '\'' +
                ", executions=" + executions +
                ", culmulatitiveQuantity=" + cumulatitiveQuantity +
                '}';
    }
}
