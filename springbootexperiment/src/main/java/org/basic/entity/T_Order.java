package org.basic.entity;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/8/5 17:29
 * @Description: 测试关联表的测试订单表
 */
public class T_Order {
    private Long orderId;

    private int price;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "T_Order{" +
                "orderId=" + orderId +
                ", price=" + price +
                '}';
    }
}
