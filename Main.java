// Main.java - Online Shopping System (entry point)
import java.util.*;

public class Main {
    static Scanner sc = new Scanner(System.in);

    // Data structures
    static Map<Integer, User> users = new HashMap<>();          // fast user lookup
    static Map<Integer, Product> products = new HashMap<>();    // fast product lookup
    static Map<Integer, Integer> cart = new HashMap<>();        // productId -> quantity
    static List<Order> allOrders = new ArrayList<>();
    static Queue<Order> pendingOrders = new LinkedList<>();     // FIFO order processing

    static int userIdCounter = 1, productIdCounter = 1, orderIdCounter = 1;
    static User currentUser = null;

    public static void main(String[] args) {
        printBanner();
        seedProducts();
        registerUser("Demo User", "demo@email.com", "123");
        System.out.println("Login with: demo@email.com / 123");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt();
            switch (choice) {
                case 1 -> registerFlow();
                case 2 -> loginFlow();
                case 3 -> displayProducts();
                case 4 -> searchFlow();
                case 5 -> viewCart();
                case 6 -> addToCartFlow();
                case 7 -> removeFromCartFlow();
                case 8 -> placeOrder();
                case 9 -> viewOrders();
                case 10 -> processPendingOrders();
                case 0 -> { running = false; System.out.println("Thank you for shopping!"); }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void printBanner() {
        System.out.println("==================================");
        System.out.println("     ONLINE SHOPPING SYSTEM       ");
        System.out.println("==================================");
    }

    // Har choice apni alag line mein - easy to read
    static void printMenu() {
        System.out.println("\n---------- MENU ----------");
        System.out.println(" 1. Register");
        System.out.println(" 2. Login");
        System.out.println(" 3. View Products");
        System.out.println(" 4. Search Products");
        System.out.println(" 5. View Cart");
        System.out.println(" 6. Add to Cart");
        System.out.println(" 7. Remove from Cart");
        System.out.println(" 8. Place Order");
        System.out.println(" 9. Order History");
        System.out.println("10. Process Pending Orders (Admin)");
        System.out.println(" 0. Exit");
        System.out.println("---------------------------");
        System.out.print("Enter choice: ");
    }

    // ---------- USER ----------
    static void registerFlow() {
        System.out.print("Name: "); String name = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();
        registerUser(name, email, pass);
    }

    static void registerUser(String name, String email, String password) {
        for (User u : users.values()) {
            if (u.email.equals(email)) { System.out.println("Email already registered!"); return; }
        }
        User u = new User(userIdCounter++, name, email, password);
        users.put(u.id, u);
        System.out.println("Registered: " + u.name);
    }

    static void loginFlow() {
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();
        for (User u : users.values()) {
            if (u.email.equals(email) && u.password.equals(pass)) {
                currentUser = u;
                System.out.println("Welcome back, " + u.name + "!");
                return;
            }
        }
        System.out.println("Invalid credentials!");
    }

    // ---------- PRODUCTS ----------
    static void seedProducts() {
        products.put(productIdCounter, new Product(productIdCounter++, "Laptop", 50000, 10, "Electronics"));
        products.put(productIdCounter, new Product(productIdCounter++, "Phone", 25000, 15, "Electronics"));
        products.put(productIdCounter, new Product(productIdCounter++, "Headphones", 2000, 30, "Electronics"));
        products.put(productIdCounter, new Product(productIdCounter++, "T-Shirt", 500, 50, "Fashion"));
        products.put(productIdCounter, new Product(productIdCounter++, "Java Book", 400, 20, "Books"));
    }

    static void displayProducts() {
        System.out.println("\n----- Available Products -----");
        for (Product p : products.values()) System.out.println(p);
    }

    static void searchFlow() {
        System.out.print("Enter keyword: ");
        String kw = sc.nextLine().toLowerCase();
        boolean found = false;
        for (Product p : products.values()) {
            if (p.name.toLowerCase().contains(kw) || p.category.toLowerCase().contains(kw)) {
                System.out.println(p);
                found = true;
            }
        }
        if (!found) System.out.println("No products found!");
    }

    // ---------- CART ----------
    static void addToCartFlow() {
        if (!requireLogin()) return;
        System.out.print("Product ID: "); int id = readInt();
        System.out.print("Quantity: "); int qty = readInt();

        Product p = products.get(id);
        if (p == null) { System.out.println("Product not found!"); return; }
        if (p.stock < qty) { System.out.println("Insufficient stock! Available: " + p.stock); return; }

        cart.put(id, cart.getOrDefault(id, 0) + qty);
        System.out.println("Added " + qty + "x " + p.name + " to cart");
    }

    static void removeFromCartFlow() {
        if (!requireLogin()) return;
        System.out.print("Product ID to remove: "); int id = readInt();
        if (cart.remove(id) != null) System.out.println("Removed from cart");
        else System.out.println("Product not in cart");
    }

    static void viewCart() {
        if (!requireLogin()) return;
        if (cart.isEmpty()) { System.out.println("Cart is empty!"); return; }

        System.out.println("\n----- Your Cart -----");
        double total = 0;
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            Product p = products.get(e.getKey());
            double lineTotal = p.price * e.getValue();
            total += lineTotal;
            System.out.printf("%-15s x%-3d = Rs.%.2f%n", p.name, e.getValue(), lineTotal);
        }
        System.out.printf("Total: Rs.%.2f%n", total);
    }

    // ---------- ORDERS ----------
    static void placeOrder() {
        if (!requireLogin()) return;
        if (cart.isEmpty()) { System.out.println("Cart is empty!"); return; }

        double total = 0;
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            Product p = products.get(e.getKey());
            if (p.stock < e.getValue()) { System.out.println(p.name + " out of stock!"); return; }
            total += p.price * e.getValue();
        }

        Order order = new Order(orderIdCounter++, currentUser.id, new HashMap<>(cart), total);
        allOrders.add(order);
        pendingOrders.offer(order);

        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            Product p = products.get(e.getKey());
            p.stock -= e.getValue();
        }

        cart.clear();
        System.out.println("Order placed! " + order);
    }

    static void viewOrders() {
        if (!requireLogin()) return;
        boolean found = false;
        for (Order o : allOrders) {
            if (o.userId == currentUser.id) { System.out.println(o); found = true; }
        }
        if (!found) System.out.println("No orders yet!");
    }

    // Process orders in FIFO order (Queue)
    static void processPendingOrders() {
        if (pendingOrders.isEmpty()) { System.out.println("No pending orders!"); return; }
        while (!pendingOrders.isEmpty()) {
            Order o = pendingOrders.poll();
            o.status = "DELIVERED";
            System.out.println("Delivered: " + o);
        }
    }

    // ---------- HELPERS ----------
    static boolean requireLogin() {
        if (currentUser == null) { System.out.println("Please login first!"); return false; }
        return true;
    }

    static int readInt() {
        while (true) {
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.print("Enter a valid number: "); }
        }
    }
}
