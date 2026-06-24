# Abyssinia Restaurant System

A simple Java console-based restaurant management system for customers and restaurant owners.  
The project supports both **text file storage** and **MySQL database storage**.

## Features


- Customer and restaurant owner sign up
- Login system
- View food and drink menus
- Place orders
- View personal order history
- View all orders as an owner
- Add, edit, and delete menu items
- Edit user profile
- Login history tracking
- File-based storage or MySQL storage

## Storage Modes

The project is built to work in two modes:

- **FILE** mode: stores data in text files inside the `data/` folder
- **MYSQL** mode: stores data in a MySQL database named `abyssinia_market`

In `Main.java`, the current mode is set to:

```java
private static final StorageMode MODE = StorageMode.FILE;
```

Change it to `StorageMode.MYSQL` if you want to use the database version.

## Technologies Used

- Java
- JDBC
- MySQL
- File I/O
- Console-based user interface

## Project Structure

```text
Abyssinia Restaurant/
├── Main.java
├── Abyssinia Local.session.sql
├── lib/
│   └── mysql-connector-j-9.7.0.jar
├── model/
│   ├── User.java
│   ├── Customer.java
│   ├── RestaurantOwner.java
│   ├── MenuItem.java
│   └── Order.java
└── service/
    ├── StorageMode.java
    ├── FileManager.java
    ├── DatabaseManager.java
    ├── UserService.java
    ├── MenuService.java
    └── OrderService.java
```

## Database Setup

If you want to use MySQL mode:

1. Start MySQL on your local machine.
2. Create the database and tables using the SQL file:
   - `Abyssinia Local.session.sql`
3. Make sure the database name is:
   - `abyssinia_market`
4. Check the connection settings in `DatabaseManager.java`:
   - username: `root`
   - password: ``
   - host: `localhost:3306`

## File Storage Setup

If you are using file mode, the app automatically creates a `data/` folder and these files:

- `users.txt`
- `food_menu.txt`
- `drinks_menu.txt`
- `orders.txt`
- `login_history.txt`

## How to Run

### Using an IDE
1. Open the project in IntelliJ IDEA, Eclipse, or VS Code.
2. Make sure the MySQL connector JAR is included in the build path if you use MySQL mode.
3. Run `Main.java`.

### Using Terminal
```bash
javac -cp ".;lib/mysql-connector-j-9.7.0.jar" Main.java model/*.java service/*.java
java -cp ".;lib/mysql-connector-j-9.7.0.jar" Main
```

> On macOS/Linux, replace `;` with `:` in the classpath.

## Main Menu

When the program starts, users can:

- Log in
- Sign up
- Exit

After login:

### Customer
- View menu
- Place order
- View past orders
- Edit profile

### Restaurant Owner
- View menu
- Add menu items
- Edit menu items
- Delete menu items
- View all orders
- Edit profile

## Notes

- Menu items are divided into **Food** and **Drinks**
- Orders store selected item IDs and totals
- Login history is recorded automatically
- Passwords are stored in plain text in this version of the project

## License

This project is for educational use.
