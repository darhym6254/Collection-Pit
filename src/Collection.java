// Collection.java
// Manages a list of Card objects in memory while the program is running.
// Implements the ManageCollection interface, so it must provide all five operations.
// Demonstrates composition: a Collection HAS-A list of Card objects.
// Also demonstrates polymorphism: cards are stored as Card references, but each
// one calls its own version of getCardType() and toString() at runtime.

import java.util.ArrayList;

public class Collection implements ManageCollection {

    // This ArrayList is the composition - Collection is built from Card objects
    private ArrayList<Card> cards;

    // Constructor - starts with an empty list
    public Collection() {
        cards = new ArrayList<Card>();
    }

    // Constructor that accepts an existing list (useful when loading cards from the database)
    public Collection(ArrayList<Card> cards) {
        this.cards = cards;
    }

    // Adds a card to the in-memory list
    @Override
    public void addCard(Card card) {
        cards.add(card);
        System.out.printf("'%s' has been added to your collection.\n", card.getName());
    }

    // Removes a card by name - case-insensitive so the user doesn't have to be exact
    @Override
    public void removeCard(String name) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getName().equalsIgnoreCase(name)) {
                System.out.printf("'%s' has been removed from your collection.\n", cards.get(i).getName());
                cards.remove(i);
                return;
            }
        }
        System.out.printf("Card '%s' was not found in your collection.\n", name);
    }

    // Updates the quantity of a card found by name
    @Override
    public void updateCard(String name, int quantity) {
        for (Card card : cards) {
            if (card.getName().equalsIgnoreCase(name)) {
                card.setQuantity(quantity);
                System.out.printf("'%s' quantity updated to %d.\n", card.getName(), quantity);
                return;
            }
        }
        System.out.printf("Card '%s' was not found in your collection.\n", name);
    }

    // Searches for a card by name and returns it, or null if not found
    @Override
    public Card searchCard(String name) {
        for (Card card : cards) {
            if (card.getName().equalsIgnoreCase(name)) {
                return card;
            }
        }
        return null;
    }

    // Prints every card in the collection using each card's toString method.
    // Because cards are stored as Card references, calling toString() here
    // automatically uses the correct subclass version - that is polymorphism.
    @Override
    public void displayAllCards() {
        if (cards.isEmpty()) {
            System.out.println("Your collection is empty.");
            return;
        }

        System.out.printf("\n--- Your Collection (%d cards) ---\n\n", cards.size());
        for (Card card : cards) {
            System.out.println(card.toString());
            System.out.println("----------------------------------");
        }
    }

    // Returns the full list of cards
    public ArrayList<Card> getCards() {
        return cards;
    }

    // Returns how many cards are in the collection
    public int getSize() {
        return cards.size();
    }
}
