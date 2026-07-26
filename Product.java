// Product.java - represents an item available in the store
public class Product {
    int id, stock;
    String name, category;
    double price;

    Product(int id, String name, double price, int stock, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    public String toString() {
        return String.format("ID:%-3d %-15s Rs.%-8.2f Stock:%-4d [%s]",
                id, name, price, stock, category);
    }
}
