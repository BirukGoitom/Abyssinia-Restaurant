
public abstract class User {
    private int id;
    private String name;
    private String username;
    private String password;

    public User(int id, String name, String username, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public User(String name, String username, String password) {
        this.id = 0;
        this.name = name;
        this.username = username;
        this.password = password;
    }


    public int getId() {
        return id;
    }

    
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Each subclass must define its own role name (e.g. "Customer", "RestaurantOwner").
     */
    public abstract String getRole();

    /**
     * Prints basic user information to the console.
     */
    public void displayInfo() {
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Username: " + username);
        System.out.println("Role: " + getRole());
    }
}