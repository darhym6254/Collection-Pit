// Main.java
// Entry point for Collection Pit - a Magic: The Gathering collection manager.
// Handles all terminal interaction: displays a menu and routes the user's
// input to the correct Collection operation.

import java.util.Scanner;

public class Main {

    // Shared Scanner so we don't open multiple streams from System.in
    private static Scanner scanner = new Scanner(System.in);

    // The in-memory collection that holds all cards during the session
    private static Collection collection = new Collection();

    public static void main(String[] args) {
        System.out.println("\nWelcome to Collection Pit - Your MTG Collection Manager");
        System.out.println("=========================================================");

        // Keep showing the menu until the user chooses to exit
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getUserChoice();

            // Route the user's choice to the right handler
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
                    running = false;
                    System.out.println("\nGoodbye! Happy collecting.\n");
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number from 1 to 6.");
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
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    // Reads the user's menu selection and handles non-numeric input gracefully
    public static int getUserChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return choice;
        } catch (NumberFormatException e) {
            return -1; // Will fall through to the default case in the switch
        }
    }

    // Prompts the user to pick a card type, then collects the card's details
    // and adds the correct Card subclass to the collection.
    // This is where polymorphism is visible: the collection stores all cards
    // as Card references, but each is actually a specific subclass.
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

        // Collect the fields shared by all card types
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

        // Build the correct subclass based on the user's type selection
        Card newCard = null;

        switch (typeChoice) {
            case 1: // Creature - needs power and toughness
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

            case 2: // Instant - needs an effect description
                System.out.print("Effect summary: ");
                String effect = scanner.nextLine().trim();
                newCard = new InstantCard(name, color, quantity, cardText, effect);
                break;

            case 3: // Sorcery - uses base Card fields only
                newCard = new SorceryCard(name, color, quantity, cardText);
                break;

            case 4: // Land - needs mana type
                System.out.print("Mana type produced (ex: Green, Blue, Any): ");
                String manaType = scanner.nextLine().trim();
                newCard = new LandCard(name, color, quantity, cardText, manaType);
                break;

            case 5: // Artifact - uses base Card fields only
                newCard = new ArtifactCard(name, color, quantity, cardText);
                break;

            case 6: // Enchantment - uses base Card fields only
                newCard = new EnchantmentCard(name, color, quantity, cardText);
                break;

            default:
                System.out.println("Invalid card type.");
                return;
        }

        collection.addCard(newCard);
    }

    // Prompts for a card name and prints the result if found
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

    // Prompts for a card name and a new quantity, then updates the record
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

        collection.updateCard(name, quantity);
    }

    // Prompts for a card name and removes it from the collection
    public static void handleRemoveCard() {
        System.out.println("\n--- Remove a Card ---");
        System.out.print("Enter card name to remove: ");
        String name = scanner.nextLine().trim();

        collection.removeCard(name);
    }
}
