// DatabaseManager.java
// Handles all SQLite database operations for Collection Pit.
// Responsible for connecting to the local database file and
// performing all four CRUD operations: Create, Read, Update, and Delete.
// Also handles bulk CSV import for loading a real card collection.
// Uses the SQLite JDBC driver (sqlite-jdbc-3.46.0.0.jar in the lib/ folder).

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
    // Class.forName() explicitly loads the SQLite JDBC driver at runtime.
    private Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite driver not found. Make sure sqlite-jdbc jar is in the lib/ folder.");
        }
        return DriverManager.getConnection(databaseUrl);
    }

    // Creates the cards table if it does not already exist, then upgrades
    // any older table by adding new columns if they are missing.
    // Table fields match the design doc plus extended collection fields.
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS cards ("
                + "card_id   INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name      TEXT    NOT NULL, "
                + "type      TEXT    NOT NULL, "
                + "color     TEXT, "
                + "quantity  INTEGER, "
                + "card_text TEXT, "
                + "set_name  TEXT, "
                + "rarity    TEXT, "
                + "foil      TEXT, "
                + "price     REAL, "
                + "mana_cost TEXT"
                + ")";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            // Add extended columns to older databases that were created before these fields existed
            addColumnIfMissing(conn, "set_name",  "TEXT");
            addColumnIfMissing(conn, "rarity",    "TEXT");
            addColumnIfMissing(conn, "foil",      "TEXT");
            addColumnIfMissing(conn, "price",     "REAL");
            addColumnIfMissing(conn, "mana_cost", "TEXT");
        } catch (SQLException e) {
            System.out.printf("Error creating table: %s\n", e.getMessage());
        }
    }

    // Tries to add a column and silently ignores the error if it already exists.
    // SQLite does not support IF NOT EXISTS on ALTER TABLE so this is the standard workaround.
    private void addColumnIfMissing(Connection conn, String column, String type) {
        try {
            conn.createStatement().execute("ALTER TABLE cards ADD COLUMN " + column + " " + type);
        } catch (SQLException e) {
            // Column already exists - nothing to do
        }
    }

    // CREATE - inserts a new card row into the database.
    // After the insert the auto-generated card_id is written back to the Card object.
    public void createCard(Card card) {
        String sql = "INSERT INTO cards (name, type, color, quantity, card_text, set_name, rarity, foil, price, mana_cost) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1,  card.getName());
            pstmt.setString(2,  card.getCardType());
            pstmt.setString(3,  card.getColor());
            pstmt.setInt(4,     card.getQuantity());
            pstmt.setString(5,  card.getCardText());
            pstmt.setString(6,  card.getSetName());
            pstmt.setString(7,  card.getRarity());
            pstmt.setString(8,  card.getFoil());
            pstmt.setDouble(9,  card.getPrice());
            pstmt.setString(10, card.getManaCost());
            pstmt.executeUpdate();

            // Store the database-assigned ID back on the object
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                card.setCardId(keys.getInt(1));
            }

        } catch (SQLException e) {
            System.out.printf("Error saving card to database: %s\n", e.getMessage());
        }
    }

    // READ - loads every row from the cards table and returns them as an ArrayList.
    // The type column is used to rebuild the correct Card subclass for each row.
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

    // Returns the total number of cards stored in the database.
    public int getCardCount() {
        String sql = "SELECT COUNT(*) FROM cards";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.printf("Error counting cards: %s\n", e.getMessage());
        }
        return 0;
    }

    // IMPORT - reads a CSV file exported from Excel and bulk loads cards into the database.
    // Expected column order: Name, Set, Rarity, Foil, Qty, Price, Mana Cost, Card Text
    // The CSV must have a header row as the first line (it is skipped automatically).
    // Place the CSV file in the Collection_Pit project folder and enter the filename when prompted.
    public int importFromCsv(String filePath) {
        List<String[]> rows;

        try {
            rows = parseCsv(filePath);
        } catch (IOException e) {
            System.out.printf("Could not read file: %s\n", e.getMessage());
            return 0;
        }

        if (rows.isEmpty()) {
            System.out.println("No data found in file.");
            return 0;
        }

        int imported = 0;
        int skipped  = 0;

        // Row 0 is the header - start at index 1
        for (int i = 1; i < rows.size(); i++) {
            String[] cols = rows.get(i);

            // Need at least Name and Qty to make a usable record
            if (cols.length < 5 || cols[0].isEmpty()) {
                skipped++;
                continue;
            }

            try {
                String name     = cols[0].trim();
                String setName  = cols.length > 1 ? cols[1].trim() : "";
                String rarity   = cols.length > 2 ? cols[2].trim() : "";
                String foil     = cols.length > 3 ? cols[3].trim() : "normal";
                int    quantity = cols.length > 4 ? parseIntSafe(cols[4]) : 1;
                double price    = cols.length > 5 ? parseDoubleSafe(cols[5]) : 0.0;
                String manaCost = cols.length > 6 ? cols[6].trim() : "";
                String cardText = cols.length > 7 ? cols[7].trim() : "";

                // Derive color from the mana cost symbols
                String color = deriveColor(manaCost);

                // Use SorceryCard as the base type for imported cards since
                // the CSV does not include a card type column
                SorceryCard card = new SorceryCard(name, color, quantity, cardText);
                card.setSetName(setName);
                card.setRarity(rarity);
                card.setFoil(foil);
                card.setPrice(price);
                card.setManaCost(manaCost);

                createCard(card);
                imported++;

                // Show progress every 500 cards so the user knows it is working
                if (imported % 500 == 0) {
                    System.out.printf("  Imported %d cards so far...\n", imported);
                }

            } catch (Exception e) {
                skipped++;
            }
        }

        return imported;
    }

    // Reads the entire CSV file and splits it into rows and fields.
    // Handles quoted fields that may contain commas or embedded newlines,
    // which Excel produces when a cell contains multi-line card text.
    private List<String[]> parseCsv(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        List<String[]>  rows   = new ArrayList<>();
        List<String>    fields = new ArrayList<>();
        StringBuilder   field  = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '"') {
                // Handle escaped double-quote ("") inside a quoted field
                if (inQuotes && i + 1 < content.length() && content.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString());
                field.setLength(0);
            } else if ((c == '\n' || c == '\r') && !inQuotes) {
                // Skip \r in \r\n Windows line endings
                if (c == '\r' && i + 1 < content.length() && content.charAt(i + 1) == '\n') {
                    i++;
                }
                // End of a row - save the last field and store the row
                fields.add(field.toString());
                field.setLength(0);
                if (!fields.isEmpty()) {
                    rows.add(fields.toArray(new String[0]));
                    fields.clear();
                }
            } else {
                field.append(c);
            }
        }

        // Catch the last row if the file does not end with a newline
        if (field.length() > 0 || !fields.isEmpty()) {
            fields.add(field.toString());
            rows.add(fields.toArray(new String[0]));
        }

        return rows;
    }

    // Derives a color name from the mana cost string (ex: "{2}{R}" -> "Red").
    // Multi-color cards return "Multi" and cost-free or generic-only cards return "Colorless".
    private String deriveColor(String manaCost) {
        boolean white = manaCost.contains("{W}");
        boolean blue  = manaCost.contains("{U}");
        boolean black = manaCost.contains("{B}");
        boolean red   = manaCost.contains("{R}");
        boolean green = manaCost.contains("{G}");

        int count = (white ? 1 : 0) + (blue ? 1 : 0) + (black ? 1 : 0)
                  + (red   ? 1 : 0) + (green ? 1 : 0);

        if (count == 0) return "Colorless";
        if (count > 1)  return "Multi";
        if (white) return "White";
        if (blue)  return "Blue";
        if (black) return "Black";
        if (red)   return "Red";
        return "Green";
    }

    // Safe integer parse - returns 1 if the string is not a valid number
    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    // Safe double parse - returns 0.0 if the string is not a valid number
    private double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Helper - reads one ResultSet row and constructs the correct Card subclass.
    // Extended fields are applied via setters after construction.
    private Card buildCard(ResultSet rs) throws SQLException {
        int    id    = rs.getInt("card_id");
        String name  = rs.getString("name");
        String type  = rs.getString("type");
        String color = rs.getString("color");
        int    qty   = rs.getInt("quantity");
        String text  = rs.getString("card_text");

        // Read extended fields - default to empty string if the column is null
        String setName  = rs.getString("set_name")  != null ? rs.getString("set_name")  : "";
        String rarity   = rs.getString("rarity")    != null ? rs.getString("rarity")    : "";
        String foil     = rs.getString("foil")      != null ? rs.getString("foil")      : "normal";
        double price    = rs.getDouble("price");
        String manaCost = rs.getString("mana_cost") != null ? rs.getString("mana_cost") : "";

        Card card;
        switch (type) {
            case "Creature":
                card = new CreatureCard(id, name, color, qty, text, 0, 0);
                break;
            case "Instant":
                card = new InstantCard(id, name, color, qty, text, "");
                break;
            case "Land":
                card = new LandCard(id, name, color, qty, text, "");
                break;
            case "Artifact":
                card = new ArtifactCard(id, name, color, qty, text);
                break;
            case "Enchantment":
                card = new EnchantmentCard(id, name, color, qty, text);
                break;
            default:
                // Covers "Sorcery", imported cards, and any unknown types
                card = new SorceryCard(id, name, color, qty, text);
                break;
        }

        // Apply extended fields
        card.setSetName(setName);
        card.setRarity(rarity);
        card.setFoil(foil);
        card.setPrice(price);
        card.setManaCost(manaCost);

        return card;
    }
}
