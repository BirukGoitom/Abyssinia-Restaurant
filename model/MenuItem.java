
public class MenuItem {
    private int id;
    private String name;
    private String category; 
    private double price;

    public MenuItem(int id, String name, String category, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

 
  
    public void displayItem() {
        System.out.printf("[%d] %-20s %-10s $%.2f%n", id, name, category, price);
    }


    public String toFileLine() {
        return id + "|" + name + "|" + category + "|" + price;
    }

    public static MenuItem fromFileLine(String line) {
        String[] parts = line.split("\\|");
        int id = Integer.parseInt(parts[0]);
        String name = parts[1];
        String category = parts[2];
        double price = Double.parseDouble(parts[3]);
        return new MenuItem(id, name, category, price);
    }
}