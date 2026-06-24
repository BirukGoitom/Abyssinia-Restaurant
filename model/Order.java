
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private String customerUsername;
    private List<MenuItem> items;
    private double total;
    private String orderTime;

    public Order(int id, String customerUsername, List<MenuItem> items, double total, String orderTime) {
        this.id = id;
        this.customerUsername = customerUsername;
        this.items = items;
        this.total = total;
        this.orderTime = orderTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public void displayOrder() {
        System.out.println("Order #" + id + " for " + customerUsername + " (" + orderTime + ")");
        for (MenuItem item : items) {
            item.displayItem();
        }
        System.out.printf("Total: $%.2f%n", total);
        System.out.println("Order confirmed!");
    }


    public String toFileLine() {
        StringBuilder itemIds = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            itemIds.append(items.get(i).getId());
            if (i < items.size() - 1) {
                itemIds.append(":");
            }
        }
        return id + "|" + customerUsername + "|" + orderTime + "|" + total + "|" + itemIds;
    }

    
    public static Order fromFileLine(String line, java.util.function.Function<Integer, MenuItem> itemLookup) {
        String[] parts = line.split("\\|");
        int id = Integer.parseInt(parts[0]);
        String customerUsername = parts[1];
        String orderTime = parts[2];
        double total = Double.parseDouble(parts[3]);

        List<MenuItem> items = new ArrayList<>();
        if (parts.length > 4 && !parts[4].isEmpty()) {
            String[] itemIdStrs = parts[4].split(":");
            for (String idStr : itemIdStrs) {
                MenuItem item = itemLookup.apply(Integer.parseInt(idStr));
                if (item != null) {
                    items.add(item);
                }
            }
        }

        return new Order(id, customerUsername, items, total, orderTime);
    }
}