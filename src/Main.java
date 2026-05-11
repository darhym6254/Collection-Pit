// Main.java
// Entry point for Collection Pit - a Magic: The Gathering collection manager.
// Handles all terminal interaction: displays a menu and routes the user's
// input to the correct operation.
// On startup, the collection is loaded from the SQLite database so previous
// sessions are always available. Every add, update, and delete is also written
// to the database immediately so nothing is lost when the program closes.

import java.util.Scanner;
import java.util.ArrayList;

public class Main {

    // Shared Scanner - one instance for the whole session
    private static Scanner scanner = new Scanner(System.in);

    // In-memory collection that mirrors what is in the database
    private static Collection collection = new Collection();

    // DatabaseManager handles all SQLite read/write operations
    // The database file (collection.db) is created in the project folder on first run
    private static DatabaseManager db = new DatabaseManager("jdbc:sqlite:collection.db");

    public static void main(String[] args) {

        // Create the cards table if this is the first time running the app
        db.createTable();

        // Load all previously saved cards from the database into memory silently
        ArrayList<Card> saved = db.getAllCards();
        for (Card card : saved) {
            collection.loadCard(card);
        }

        System.out.println("\nWelcome to Collection Pit - Your MTG Collection Manager");
        System.out.println("=========================================================");
        System.out.printf("Collection loaded: %d card(s) found.\n", collection.getSize());

        // Keep showing the menu until the user chooses to exit
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    handleAddCard();
                    break;
                case 2:
                    collection.displayAllCards();
                    break;
                case 3:
                    handleSearchCard();
                    break;
                case 4:
                    handleUpdateCard();
                    break;
                case 5:
                    handleRemoveCard();
                    break;
                case 6:
                    handleImportCsv();
                    break;
                case 7:
                    running = false;
                    System.out.println("\nGoodbye! Happy collecting.\n");
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number from 1 to 7.");
            }
        }

        scanner.close();
    }

    // Prints the main menu to the terminal
    public static void displayMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Add a card");
        System.out.println("2. View full collection");
        System.out.println("3. Search for a card");
        System.out.println("4. Update card quantity");
        System.out.println("5. Remove a card");
        System.out.println("6. Import collection from CSV");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    // Reads the user's menu selection and returns -1 for any non-numeric input
    public static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Prompts the user to choose a card type, collects the card's details,
    // adds it to the in-memory collection, and saves it to the database.
    public static void handleAddCard() {
        System.out.println("\n--- Add a Card ---");
        System.out.println("Card types:");
        System.out.println("1. Creature");
        System.out.println("2. Instant");
        System.out.println("3. Sorcery");
        System.out.println("4. Land");
        System.out.println("5. Artifact");
        System.out.println("6. Enchantment");
        System.out.print("Select card type: ");

        int typeChoice;
        try {
            typeChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid type selection.");
            return;
        }

        // Collect fields shared by all card types
        System.out.print("Card name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Color (ex: Red, Blue, Green, Black, White, Colorless, Multi): ");
        String color = scanner.nextLine().trim();

        System.out.print("Quantity owned: ");
        int quantity;
        try {
            quantity = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity. Defaulting to 1.");
            quantity = 1;
        }

        System.out.print("Card text: ");
        String cardText = scanner.nextLine().trim();

        // Build the correct subclass based on the user's type choice
        Card newCard = null;

        switch (typeChoice) {
            case 1: // Creature - also needs power and toughness
                System.out.print("Power: ");
                int power = 0;
                try { power = Integer.parseInt(scanner.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("Invalid power. Defaulting to 0."); }

                System.out.print("Toughness: ");
                int toughness = 0;
                try { toughness = Integer.parseInt(scanner.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("Invalid toughness. Defaulting to 0."); }

                newCard = new CreatureCard(name, color, quantity, cardText, power, toughness);
                break;

            case 2: // Instant - also needs an effect description
                System.out.print("Effect summary: ");
                String effect = scanner.nextLine().trim();
                newCard = new InstantCard(name, color, quantity, cardText, effect);
                break;

            case 3: // Sorcery
                newCard = new SorceryCard(name, color, quantity, cardText);
                break;

            case 4: // Land - also needs mana type
                System.out.print("Mana type produced (ex: Green, Blue, Any): ");
                String manaType = scanner.nextLine().trim();
                newCard = new LandCard(name, color, quantity, cardText, manaType);
                break;

            case 5: // Artifact
                newCard = new ArtifactCard(name, color, quantity, cardText);
                break;

            case 6: // Enchantment
                newCard = new EnchantmentCard(name, color, quantity, cardText);
                break;

            default:
                System.out.println("Invalid card type.");
                return;
        }

        // Save to database first so the card gets its auto-generated card_id,
        // then add to the in-memory collection
        db.createCard(newCard);
        collection.addCard(newCard);
    }

    // Prompts for a card name and prints the matching card if found
    public static void handleSearchCard() {
        System.out.println("\n--- Search for a Card ---");
        System.out.print("Enter card name: ");
        String name = scanner.nextLine().trim();

        Card result = collection.searchCard(name);

        if (result != null) {
            System.out.printf("\nFound:\n%s\n", result.toString());
        } else {
            System.out.printf("'%s' was not found in your collection.\n", name);
        }
    }

    // Prompts for a card name and a new quantity, then updates both the
    // in-memory collection and the database record
    public static void handleUpdateCard() {
        System.out.println("\n--- Update Card Quantity ---");
        System.out.print("Enter card name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter new quantity: ");
        int quantity;
        try {
            quantity = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }

        // Update in memory and in the database
        collection.updateCard(name, quantity);
        db.updateCardQuantity(name, quantity);
    }

    // Prompts for a card name and removes it from both the in-memory collection
    // and the database
    public static void handleRemoveCard() {
        System.out.println("\n--- Remove a Card ---");
        System.out.print("Enter card name to remove: ");
        String name = scanner.nextLine().trim();

        // Remove from memory and from the database
        collection.removeCard(name);
        db.deleteCard(name);
    }

    // Imports a CSV file exported from Excel into the database and in-memory collection.
    // The CSV must be placed in the Collection_Pit project folder.
    // Expected columns: Name, Set, Rarity, Foil, Qty, Price, Mana Cost, Card Text
    public static void handleImportCsv() {
        System.out.println("\n--- Import Collection from CSV ---");
        System.out.println("Export your Excel file as CSV and place it in the Collection_Pit folder.");
        System.out.print("Enter the CSV filename (ex: collection.csv): ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("No filename entered. Import cancelled.");
            return;
        }

        System.out.printf("\nImporting '%s' - this may take a moment for large collections...\n", filename);

        int count = db.importFromCsv(filename);

        if (count > 0) {
            System.out.printf("\nImport complete! %d cards added to the database.\n", count);
            System.out.println("Reloading collection into memory...");

            // Refresh the in-memory collection from the database so it reflects the import
            collection = new Collection();
            ArrayList<Card> all = db.getAllCards();
            for (Card card : all) {
                collection.loadCard(card);
            }

            System.out.printf("Collection now contains %d card(s).\n", collection.getSize());
        } else {
            System.out.println("No cards were imported. Check the filename and CSV format.");
        }
    }
}
