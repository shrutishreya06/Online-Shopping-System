// Order.java - represents a placed order
import java.util.Map;

public class Order {
    int id, userId;
    Map<Integer, Integer> items; // productId -> quantity
    double total;
    String status;

    Order(int id, int userId, Map<Integer, Integer> items, double total) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.total = total;
        this.status = "PLACED";
    }

    public String toString() {
        return "Order #" + id + " | Rs." + total + " | " + status;
    }
}
