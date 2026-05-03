// DatabaseManager.java
// Handles all SQLite database operations for Collection Pit.
// Responsible for connecting to the local database file and
// performing all four CRUD operations: Create, Read, Update, and Delete.
// Uses the SQLite JDBC driver (sqlite-jdbc-3.46.0.0.jar in the lib/ folder).

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseManager {

    // JDBC connection string pointing to the local SQLite database file
    private String databaseUrl;

    // Constructor - receives the full JDBC URL (ex: "jdbc:sqlite:collection.db")
    public DatabaseManager(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    // Opens and returns a connection to the database.
    // Called internally by each CRUD method.
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    // Creates the cards table if it does not already exist.
    // This runs once at startup so the app works on a fresh machine.
    // Table structure matches the design: card_id, name, type, color, quantity, card_text.
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS cards ("
                + "card_id   INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name      TEXT    NOT NULL, "
                + "type      TEXT    NOT NULL, "
                + "color     TEXT, "
                + "quantity  INTEGER, "
                + "card_text TEXT"
                + ")";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.printf("Error creating table: %s\n", e.getMessage());
        }
    }

    // CREATE - inserts a new card row into the database.
    // After the insert, the auto-generated card_id is written back to the Card object.
    public void createCard(Card card) {
        String sql = "INSERT INTO cards (name, type, color, quantity, card_text) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, card.getName());
            pstmt.setString(2, card.getCardType());
            pstmt.setString(3, card.getColor());
            pstmt.setInt(4, card.getQuantity());
            pstmt.setString(5, card.getCardText());
            pstmt.executeUpdate();

            // Grab the ID SQLite assigned and store it back on the object
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                card.setCardId(keys.getInt(1));
            }

        } catch (SQLException e) {
            System.out.printf("Error saving card to database: %s\n", e.getMessage());
        }
    }

    // READ - loads every row from the cards table and returns them as an ArrayList.
    // The type column is used to rebuild the correct Card subclass for each row,
    // which means polymorphism carries over from the database back into the app.
    public ArrayList<Card> getAllCards() {
        ArrayList<Card> cards = new ArrayList<Card>();
        String sql = "SELECT * FROM cards ORDER BY name";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Card card = buildCard(rs);
                if (card != null) {
                    cards.add(card);
                }
            }

        } catch (SQLException e) {
            System.out.printf("Error loading collection from database: %s\n", e.getMessage());
        }

        return cards;
    }

    // READ - searches for a single card by name (case-insensitive).
    // Returns null if nothing is found.
    public Card searchCardByName(String name) {
        String sql = "SELECT * FROM cards WHERE LOWER(name) = LOWER(?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return buildCard(rs);
            }

        } catch (SQLException e) {
            System.out.printf("Error searching database: %s\n", e.getMessage());
        }

        return null;
    }

    // UPDATE - changes the quantity field for a card identified by name.
    public void updateCardQuantity(String name, int quantity) {
        String sql = "UPDATE cards SET quantity = ? WHERE LOWER(name) = LOWER(?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setString(2, name);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.printf("Error updating card in database: %s\n", e.getMessage());
        }
    }

    // DELETE - removes a card row from the database by name (case-insensitive).
    public void deleteCard(String name) {
        String sql = "DELETE FROM cards WHERE LOWER(name) = LOWER(?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.printf("Error deleting card from database: %s\n", e.getMessage());
        }
    }

    // Helper method - reads one ResultSet row and constructs the correct Card subclass.
    // The type field stored in the database tells us which subclass to create.
    // This keeps the CRUD methods clean and demonstrates polymorphism on load.
    private Card buildCard(ResultSet rs) throws SQLException {
        int id       = rs.getInt("card_id");
        String name  = rs.getString("name");
        String type  = rs.getString("type");
        String color = rs.getString("color");
        int qty      = rs.getInt("quantity");
        String text  = rs.getString("card_text");

        switch (type) {
            case "Creature":
                // Power and toughness are not stored in the DB schema,
                // so they default to 0 on load. Quantity and text are preserved.
                return new CreatureCard(id, name, color, qty, text, 0, 0);
            case "Instant":
                return new InstantCard(id, name, color, qty, text, "");
            case "Sorcery":
                return new SorceryCard(id, name, color, qty, text);
            case "Land":
                return new LandCard(id, name, color, qty, text, "");
            case "Artifact":
                return new ArtifactCard(id, name, color, qty, text);
            case "Enchantment":
                return new EnchantmentCard(id, name, color, qty, text);
            default:
                // Unknown type - build a generic SorceryCard so the row isn't lost
                return new SorceryCard(id, name, color, qty, text);
        }
    }
}
