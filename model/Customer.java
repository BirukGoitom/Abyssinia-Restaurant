public class Customer extends User {

    public Customer(String name, String username) {
        super(name, username, "");
    }

    public Customer(String name, String username, String password) {
        super(name, username, password);
    }

    
    public String getRole() {
        return "Customer";
    }
}