
public class RestaurantOwner extends User {

    public RestaurantOwner(int id, String name, String username, String password) {
        super(id, name, username, password);
    }

    public RestaurantOwner(String name, String username, String password) {
        super(name, username, password);
    }

    @Override
    public String getRole() {
        return "RestaurantOwner";
    }
}