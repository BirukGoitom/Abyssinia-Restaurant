package src.service;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import src.model.MenuItem;
import src.model.Order;



public class OrderService {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StorageMode  mode;
    private final FileManager  fileManager;
    private final Connection   connection;
    private final MenuService  menuService;
    private final List<Order>  orders;


    public OrderService(FileManager fileManager, MenuService menuService) {
        this.mode        = StorageMode.FILE;
        this.fileManager = fileManager;
        this.connection  = null;
        this.menuService = menuService;
        this.orders      = new ArrayList<>();
        loadOrders();
    }

    
    public OrderService(DatabaseManager dbManager, MenuService menuService) {
        this.mode        = StorageMode.MYSQL;
        this.fileManager = null;
        this.connection  = dbManager.getConnection();
        this.menuService = menuService;
        this.orders      = new ArrayList<>();
        loadOrders();
    }


    private void loadOrders() {
        if (mode == StorageMode.FILE) {
            for (String line : fileManager.readLines(FileManager.ORDERS_FILE))
                orders.add(Order.fromFileLine(line, menuService::findById));
        } else {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs   = stmt.executeQuery(
                         "SELECT id, customer_username, order_time, total FROM orders")) {
                while (rs.next()) {
                    int    orderId  = rs.getInt("id");
                    String username = rs.getString("customer_username");
                    String time     = rs.getTimestamp("order_time").toLocalDateTime().format(FORMATTER);
                    double total    = rs.getDouble("total");
                    List<MenuItem> items = loadOrderItems(orderId);
                    orders.add(new Order(orderId, username, items, total, time));
                }
            } catch (SQLException e) {
                System.out.println("Error loading orders: " + e.getMessage());
            }
        }
    }

    private List<MenuItem> loadOrderItems(int orderId) {
        List<MenuItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT menu_item_id FROM order_items WHERE order_id = ?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = menuService.findById(rs.getInt("menu_item_id"));
                    if (item != null) items.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading order items: " + e.getMessage());
        }
        return items;
    }

    
    public Order createOrder(String customerUsername, List<MenuItem> items) {
        double total = calculateTotal(items);
        LocalDateTime now = LocalDateTime.now();

        if (mode == StorageMode.FILE) {
            Order order = new Order(nextId(), customerUsername, items, total, now.format(FORMATTER));
            orders.add(order);
            saveToFile();
            return order;
        } else {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO orders (customer_username, order_time, total) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, customerUsername);
                ps.setTimestamp(2, Timestamp.valueOf(now));
                ps.setDouble(3, total);
                ps.executeUpdate();

                int orderId;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) return null;
                    orderId = keys.getInt(1);
                }

                try (PreparedStatement itemPs = connection.prepareStatement(
                        "INSERT INTO order_items (order_id, menu_item_id) VALUES (?, ?)")) {
                    for (MenuItem item : items) {
                        itemPs.setInt(1, orderId);
                        itemPs.setInt(2, item.getId());
                        itemPs.addBatch();
                    }
                    itemPs.executeBatch();
                }

                Order order = new Order(orderId, customerUsername, items, total, now.format(FORMATTER));
                orders.add(order);
                return order;

            } catch (SQLException e) {
                System.out.println("Error creating order: " + e.getMessage());
                return null;
            }
        }
    }

  

    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        for (Order o : orders) lines.add(o.toFileLine());
        fileManager.writeLines(FileManager.ORDERS_FILE, lines);
    }

    private int nextId() {
        int max = 0;
        for (Order o : orders) if (o.getId() > max) max = o.getId();
        return max + 1;
    }

    public double calculateTotal(List<MenuItem> items) {
        double total = 0;
        for (MenuItem item : items) total += item.getPrice();
        return total;
    }

    public List<Order> getOrdersByCustomer(String username) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders)
            if (o.getCustomerUsername().equalsIgnoreCase(username)) result.add(o);
        return result;
    }

    public List<Order> getAllOrders() { return orders; }
}