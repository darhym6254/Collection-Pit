// ManageCollection.java
// Interface that defines the contract for managing a card collection.
// Any class that implements this interface must provide all five operations.
// This meets the course project requirement for an interface.

public interface ManageCollection {

    // Add a card to the collection
    void addCard(Card card);

    // Remove a card from the collection by name
    void removeCard(String name);

    // Update the quantity of a card identified by name
    void updateCard(String name, int quantity);

    // Search for a card by name and return it (returns null if not found)
    Card searchCard(String name);

    // Print every card in the collection to the terminal
    void displayAllCards();
}
