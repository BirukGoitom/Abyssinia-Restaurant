import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final StorageMode MODE = StorageMode.FILE;

 

    private static final Scanner scanner = new Scanner(System.in);

    private static DatabaseManager dbManager;
    private static UserService     userService;
    private static MenuService     menuService;
    private static OrderService    orderService;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  Welcome to Abyssinia Restaurant System  ");
        System.out.println("==========================================");

        if (MODE == StorageMode.MYSQL) {
            dbManager    = new DatabaseManager();
            menuService  = new MenuService(dbManager);
            userService  = new UserService(dbManager.getConnection());
            orderService = new OrderService(dbManager, menuService);
            System.out.println("Storage: MySQL (localhost)");
        } else {
            try {
                Class<?> fileManagerClass = Class.forName("FileManager");
                Object fileManager = fileManagerClass.getDeclaredConstructor().newInstance();
                menuService  = MenuService.class.getConstructor(fileManagerClass).newInstance(fileManager);
                userService  = UserService.class.getConstructor(fileManagerClass).newInstance(fileManager);
                orderService = OrderService.class.getConstructor(fileManagerClass, MenuService.class)
                                           .newInstance(fileManager, menuService);
                System.out.println("Storage: Text files (data/)");
            } catch (Exception e) {
                System.out.println("Error initializing file storage: " + e.getMessage());
                return;
            }
        }

        mainMenu();

        if (dbManager != null) dbManager.close();
    }

    private static void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n1. Login");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit");
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1: handleLogin();  break;
                case 2: handleSignUp(); break;
                case 3: running = false; System.out.println("Goodbye!"); break;
                default: System.out.println("Invalid option. Try again.");
            }
        }
    }


    private static void handleLogin() {
        String username = readLine("Username: ");
        String password = readLine("Password: ");
        User user = userService.login(username, password);
        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }
        System.out.println("Welcome back, " + user.getName() + "!");
        if (user instanceof RestaurantOwner) ownerMenu((RestaurantOwner) user);
        else if (user instanceof Customer)   customerMenu((Customer) user);
    }

    private static void handleSignUp() {
        System.out.println("1. Sign up as Customer");
        System.out.println("2. Sign up as Restaurant Owner");
        int choice = readInt("Choose an option: ");

        String name     = readLine("Full name: ");
        String username = readLine("Choose a username: ");
        String password = readLine("Choose a password: ");

        if (choice == 1) {
            Customer c = userService.signUpCustomer(name, username, password);
            System.out.println(c != null ? "Account created! You can now log in."
                                         : "Username already taken. Try a different one.");
        } else if (choice == 2) {
            RestaurantOwner o = userService.signUpRestaurantOwner(name, username, password);
            System.out.println(o != null ? "Owner account created! You can now log in."
                                         : "Username already taken. Try a different one.");
        } else {
            System.out.println("Invalid option.");
        }
    }



    private static void customerMenu(Customer customer) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Customer Menu (" + customer.getUsername() + ") ---");
            System.out.println("1. View Menu");
            System.out.println("2. Place Order");
            System.out.println("3. View My Orders");
            System.out.println("4. Edit Profile");
            System.out.println("5. Logout");
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1: viewMenu();             break;
                case 2: placeOrder(customer);   break;
                case 3: viewMyOrders(customer); break;
                case 4: editProfile(customer);  break;
                case 5: inMenu = false;         break;
                default: System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void viewMenu() {
        System.out.println("\n===== FOOD MENU =====");
        List<MenuItem> food = menuService.getFoodMenu();
        if (food.isEmpty()) System.out.println("No food items available.");
        else for (MenuItem item : food) item.displayItem();

        System.out.println("\n===== DRINK MENU =====");
        List<MenuItem> drinks = menuService.getDrinkMenu();
        if (drinks.isEmpty()) System.out.println("No drink items available.");
        else for (MenuItem item : drinks) item.displayItem();
    }

    private static void placeOrder(Customer customer) {
        viewMenu();
        List<MenuItem> selected = new ArrayList<>();
        System.out.println("\nAdd items to your order (enter 0 when done):");
        while (true) {
            int id = readInt("Enter item ID: ");
            if (id == 0) break;
            MenuItem item = menuService.findById(id);
            if (item == null) System.out.println("Item not found. Try again.");
            else { selected.add(item); System.out.println(item.getName() + " added."); }
        }
        if (selected.isEmpty()) { System.out.println("No items selected. Order cancelled."); return; }
        Order order = orderService.createOrder(customer.getUsername(), selected);
        if (order != null) { System.out.println("\nOrder placed successfully!"); order.displayOrder(); }
        else System.out.println("Something went wrong. Please try again.");
    }

    private static void viewMyOrders(Customer customer) {
        List<Order> myOrders = orderService.getOrdersByCustomer(customer.getUsername());
        if (myOrders.isEmpty()) { System.out.println("You have no past orders."); return; }
        System.out.println("\n===== YOUR ORDERS =====");
        for (Order o : myOrders) { o.displayOrder(); System.out.println("---"); }
    }

    private static void editProfile(Customer customer) {
        System.out.println("Leave blank to keep current value.");
        String newName     = readLine("New name: ");
        String newPassword = readLine("New password: ");
        userService.updateProfile(customer.getUsername(), newName, newPassword);
        System.out.println("Profile updated successfully.");
    }



    private static void ownerMenu(RestaurantOwner owner) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Owner Menu (" + owner.getUsername() + ") ---");
            System.out.println("1. View Menu");
            System.out.println("2. Add Menu Item");
            System.out.println("3. Edit Menu Item");
            System.out.println("4. Delete Menu Item");
            System.out.println("5. View All Orders");
            System.out.println("6. Edit Profile");
            System.out.println("7. Logout");
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1: viewMenu();              break;
                case 2: addMenuItem();           break;
                case 3: editMenuItem();          break;
                case 4: deleteMenuItem();        break;
                case 5: viewAllOrders();         break;
                case 6: editOwnerProfile(owner); break;
                case 7: inMenu = false;          break;
                default: System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addMenuItem() {
        System.out.println("1. Add Food Item");
        System.out.println("2. Add Drink Item");
        int choice  = readInt("Choose an option: ");
        String name = readLine("Item name: ");
        double price = readDouble("Price (ETB): ");
        if (choice == 1) { menuService.addFoodItem(name, price);  System.out.println("Food item added."); }
        else if (choice == 2) { menuService.addDrinkItem(name, price); System.out.println("Drink item added."); }
        else System.out.println("Invalid option.");
    }

    private static void editMenuItem() {
        viewMenu();
        int id       = readInt("Enter item ID to edit: ");
        String name  = readLine("New name (leave blank to keep): ");
        double price = readDouble("New price (-1 to keep): ");
        System.out.println(menuService.editItem(id, name, price) ? "Item updated." : "Item not found.");
    }

    private static void deleteMenuItem() {
        viewMenu();
        int id = readInt("Enter item ID to delete: ");
        System.out.println(menuService.deleteItem(id) ? "Item deleted." : "Item not found.");
    }

    private static void viewAllOrders() {
        List<Order> all = orderService.getAllOrders();
        if (all.isEmpty()) { System.out.println("No orders placed yet."); return; }
        System.out.println("\n===== ALL ORDERS =====");
        for (Order o : all) { o.displayOrder(); System.out.println("---"); }
    }

    private static void editOwnerProfile(RestaurantOwner owner) {
        System.out.println("Leave blank to keep current value.");
        String newName     = readLine("New name: ");
        String newPassword = readLine("New password: ");
        userService.updateProfile(owner.getUsername(), newName, newPassword);
        System.out.println("Profile updated successfully.");
    }


    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number."); }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Double.parseDouble(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number."); }
        }
    }
}


