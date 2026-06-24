import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class FileManager {

    private static final String DATA_DIR = "data";

    public static final String USERS_FILE          = DATA_DIR + "/users.txt";
    public static final String FOOD_MENU_FILE      = DATA_DIR + "/food_menu.txt";
    public static final String DRINKS_MENU_FILE    = DATA_DIR + "/drinks_menu.txt";
    public static final String ORDERS_FILE         = DATA_DIR + "/orders.txt";
    public static final String LOGIN_HISTORY_FILE  = DATA_DIR + "/login_history.txt";

    public FileManager() {
        initializeStorage();
    }

    private void initializeStorage() {
        try {
            Path dir = Paths.get(DATA_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            createIfMissing(USERS_FILE);
            createIfMissing(FOOD_MENU_FILE);
            createIfMissing(DRINKS_MENU_FILE);
            createIfMissing(ORDERS_FILE);
            createIfMissing(LOGIN_HISTORY_FILE);
        } catch (IOException e) {
            System.out.println("Error initializing file storage: " + e.getMessage());
        }
    }

    private void createIfMissing(String path) throws IOException {
        Path p = Paths.get(path);
        if (!Files.exists(p)) Files.createFile(p);
    }

    public List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) return lines;
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading " + path + ": " + e.getMessage());
        }
        return lines;
    }

    public void writeLines(String path, List<String> lines) {
        try (FileWriter writer = new FileWriter(path, false)) {
            for (String line : lines) {
                writer.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error writing " + path + ": " + e.getMessage());
        }
    }

    public void appendLine(String path, String line) {
        try (FileWriter writer = new FileWriter(path, true)) {
            writer.write(line + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error appending to " + path + ": " + e.getMessage());
        }
    }
}
