
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;




public class MenuService {

    private final StorageMode  mode;
    private final FileManager  fileManager;
    private final Connection   connection;
    private final List<MenuItem> foodMenu;
    private final List<MenuItem> drinkMenu;

    public MenuService(FileManager fileManager) {
        this.mode        = StorageMode.FILE;
        this.fileManager = fileManager;
        this.connection  = null;
        this.foodMenu    = new ArrayList<>();
        this.drinkMenu   = new ArrayList<>();
        loadMenus();
    }

    public MenuService(DatabaseManager dbManager) {
        this.mode        = StorageMode.MYSQL;
        this.fileManager = null;
        this.connection  = resolveConnection(dbManager);
        this.foodMenu    = new ArrayList<>();
        this.drinkMenu   = new ArrayList<>();
        loadMenus();
    }

    private Connection resolveConnection(DatabaseManager dbManager) {
        try {
            Method getter = dbManager.getClass().getMethod("getConnection");
            Object conn = getter.invoke(dbManager);
            if (conn instanceof Connection) return (Connection) conn;
        } catch (Exception ignored) {
            // ignore and try direct field access
        }

        try {
            Field field = dbManager.getClass().getDeclaredField("connection");
            field.setAccessible(true);
            Object conn = field.get(dbManager);
            if (conn instanceof Connection) return (Connection) conn;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to obtain database connection from DatabaseManager", e);
        }

        throw new IllegalArgumentException("Unable to obtain database connection from DatabaseManager");
    }


    private void loadMenus() {
        if (mode == StorageMode.FILE) {
            for (String line : fileManager.readLines(FileManager.FOOD_MENU_FILE))
                foodMenu.add(MenuItem.fromFileLine(line));
            for (String line : fileManager.readLines(FileManager.DRINKS_MENU_FILE))
                drinkMenu.add(MenuItem.fromFileLine(line));
        } else {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs   = stmt.executeQuery("SELECT id, name, category, price FROM menu_items")) {
                while (rs.next()) {
                    MenuItem item = new MenuItem(rs.getInt("id"), rs.getString("name"),
                            rs.getString("category"), rs.getDouble("price"));
                    if (item.getCategory().equalsIgnoreCase("Food")) foodMenu.add(item);
                    else drinkMenu.add(item);
                }
            } catch (SQLException e) {
                System.out.println("Error loading menu: " + e.getMessage());
            }
        }
    }


    public MenuItem addFoodItem(String name, double price) {
        return addItem(name, "Food", price);
    }

    public MenuItem addDrinkItem(String name, double price) {
        return addItem(name, "Drink", price);
    }

    private MenuItem addItem(String name, String category, double price) {
        if (mode == StorageMode.FILE) {
            MenuItem item = new MenuItem(nextId(), name, category, price);
            if (category.equalsIgnoreCase("Food")) { foodMenu.add(item);  saveFoodToFile(); }
            else                                   { drinkMenu.add(item); saveDrinksToFile(); }
            return item;
        } else {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO menu_items (name, category, price) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name); ps.setString(2, category); ps.setDouble(3, price);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        MenuItem item = new MenuItem(keys.getInt(1), name, category, price);
                        if (category.equalsIgnoreCase("Food")) foodMenu.add(item);
                        else drinkMenu.add(item);
                        return item;
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error adding menu item: " + e.getMessage());
            }
            return null;
        }
    }


    public boolean editItem(int id, String newName, double newPrice) {
        MenuItem item = findById(id);
        if (item == null) return false;
        if (newName != null && !newName.trim().isEmpty()) item.setName(newName);
        if (newPrice >= 0)                          item.setPrice(newPrice);
        if (mode == StorageMode.FILE) {
            if (item.getCategory().equalsIgnoreCase("Food")) saveFoodToFile();
            else saveDrinksToFile();
        } else {
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE menu_items SET name = ?, price = ? WHERE id = ?")) {
                ps.setString(1, item.getName()); ps.setDouble(2, item.getPrice()); ps.setInt(3, id);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error editing menu item: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public boolean deleteItem(int id) {
        MenuItem item = findById(id);
        if (item == null) return false;
        if (mode == StorageMode.FILE) {
            if (item.getCategory().equalsIgnoreCase("Food")) { foodMenu.remove(item);  saveFoodToFile(); }
            else                                             { drinkMenu.remove(item); saveDrinksToFile(); }
        } else {
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM menu_items WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                if (item.getCategory().equalsIgnoreCase("Food")) foodMenu.remove(item);
                else drinkMenu.remove(item);
            } catch (SQLException e) {
                System.out.println("Error deleting menu item: " + e.getMessage());
                return false;
            }
        }
        return true;
    }


    private void saveFoodToFile() {
        List<String> lines = new ArrayList<>();
        for (MenuItem item : foodMenu) lines.add(item.toFileLine());
        fileManager.writeLines(FileManager.FOOD_MENU_FILE, lines);
    }

    private void saveDrinksToFile() {
        List<String> lines = new ArrayList<>();
        for (MenuItem item : drinkMenu) lines.add(item.toFileLine());
        fileManager.writeLines(FileManager.DRINKS_MENU_FILE, lines);
    }

    private int nextId() {
        int max = 0;
        for (MenuItem i : foodMenu)  if (i.getId() > max) max = i.getId();
        for (MenuItem i : drinkMenu) if (i.getId() > max) max = i.getId();
        return max + 1;
    }

    public MenuItem findById(int id) {
        for (MenuItem i : foodMenu)  if (i.getId() == id) return i;
        for (MenuItem i : drinkMenu) if (i.getId() == id) return i;
        return null;
    }

    public List<MenuItem> getFoodMenu()  { return foodMenu;  }
    public List<MenuItem> getDrinkMenu() { return drinkMenu; }

    public List<MenuItem> getAllItems() {
        List<MenuItem> all = new ArrayList<>();
        all.addAll(foodMenu);
        all.addAll(drinkMenu);
        return all;
    }
}
