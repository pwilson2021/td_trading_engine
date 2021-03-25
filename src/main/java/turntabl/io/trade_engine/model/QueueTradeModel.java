package turntabl.io.trade_engine.model;

public class QueueTradeModel {
    private String product;
    private Integer quantity;
    private Double price;
    private String side;
    private Integer exchangeId;

    public QueueTradeModel(String product, Integer quantity, Double price, String side, Integer exchangeId) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.exchangeId = exchangeId;
    }

    public QueueTradeModel() {
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

    public Integer getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(Integer exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Override
    public String toString() {
        return "QueueTradeModel{" +
                "product='" + product + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side='" + side + '\'' +
                ", exchangeId=" + exchangeId +
                '}';
    }
}
