# Online Shopping System (Java)

A console-based Online Shopping System built in Java, following a well-structured multi-file project architecture where each class is organized into its own dedicated file for better modularity, maintainability, and code organization.

## Project Structure
```
src/
├── User.java      # User (customer) entity
├── Product.java   # Product entity
├── Order.java     # Order entity
└── Main.java      # Menu, business logic, entry point
```

## Data Structures Used
| Concept | Used In | Purpose |
|---|---|---|
| HashMap | users, products, cart | O(1) fast lookup by ID |
| Queue (LinkedList) | pendingOrders | FIFO order processing |
| ArrayList | allOrders | Sequential order history |

## How to Run
```bash
cd src
javac *.java
java Main
```

Demo Login Credentials (Pre-Registered User):
```
Email: demo@email.com
Password: 123
```

## Menu Options
```
 1. Register
 2. Login
 3. View Products
 4. Search Products
 5. View Cart
 6. Add to Cart
 7. Remove from Cart
 8. Place Order
 9. Order History
10. Process Pending Orders (Admin)
 0. Exit
```
