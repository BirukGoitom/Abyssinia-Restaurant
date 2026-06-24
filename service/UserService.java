import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class UserService {

    private final StorageMode   mode;
    @SuppressWarnings("unused")
    private final FileManager   fileManager;
    private final Connection    connection;
    private final List<User>    users;


public UserService(FileManager fileManager) {
        this.mode        = StorageMode.FILE;
        this.fileManager = fileManager;
        this.connection  = null;
        this.users       = new ArrayList<>();
        loadUsers();
    }


 public UserService(Connection connection) {
        this.mode        = StorageMode.MYSQL;
        this.fileManager = null;
        this.connection  = connection;
        this.users       = new ArrayList<>();
        loadUsers();
    }

  private void loadUsers() {
        if (mode == StorageMode.FILE) {
            try {
                for (String line : Files.readAllLines(Paths.get(FileManager.USERS_FILE))) {
                    String[] p = line.split("\\|");
                    if (p.length < 5) continue;
                    users.add(buildUser(Integer.parseInt(p[0]), p[1], p[2], p[3], p[4]));
                }
            } catch (IOException e) {
                System.out.println("Error loading users from file: " + e.getMessage());
            }
        } else {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs   = stmt.executeQuery("SELECT id, name, username, password, role FROM users")) {
                while (rs.next()) {
                    users.add(buildUser(rs.getInt("id"), rs.getString("name"),
                            rs.getString("username"), rs.getString("password"), rs.getString("role")));
                }
            } catch (SQLException e) {
                System.out.println("Error loading users: " + e.getMessage());
            }
        }
    }


  private User buildUser(int id, String name, String username, String password, String role) {
        User user;
        if (role.equalsIgnoreCase("RestaurantOwner")) {
            user = new RestaurantOwner(name, username, password);
        } else {
            user = new Customer(name, username, password);
        }
        setUserId(user, id);
        return user;
    }



  private void setUserId(User user, int id) {
        try {
            Class<?> cls = user.getClass();
            java.lang.reflect.Field idField = null;
            while (cls != null) {
                try {
                    idField = cls.getDeclaredField("id");
                    break;
                } catch (NoSuchFieldException ignored) {
                    cls = cls.getSuperclass();
                }
            }
            if (idField == null) throw new NoSuchFieldException("id");
            idField.setAccessible(true);
            idField.setInt(user, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to set user id", e);
        }
    }

