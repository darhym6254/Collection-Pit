// Card.java
// Abstract base class for all Magic: The Gathering card types.
// All card subclasses inherit these shared fields and methods.
// getCardType() is abstract so each subclass must provide its own implementation,
// which is how this app demonstrates polymorphism.

public abstract class Card {

    // Private fields - data is only accessible through getters and setters
    private int cardId;
    private String name;
    private String color;
    private int quantity;
    private String cardText;

    // Constructor with all fields
    public Card(int cardId, String name, String color, int quantity, String cardText) {
        this.cardId = cardId;
        this.name = name;
        this.color = color;
        this.quantity = quantity;
        this.cardText = cardText;
    }

    // Overloaded constructor without cardId (used when adding a new card before DB assigns an ID)
    public Card(String name, String color, int quantity, String cardText) {
        this.cardId = 0;
        this.name = name;
        this.color = color;
        this.quantity = quantity;
        this.cardText = cardText;
    }

    // --- Getters ---

    public int getCardId() {
        return cardId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCardText() {
        return cardText;
    }

    // --- Setters ---

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCardText(String cardText) {
        this.cardText = cardText;
    }

    // Abstract method - every subclass must override this to return its card type string
    public abstract String getCardType();

    // toString uses String.format with % and \n for readable terminal output
    @Override
    public String toString() {
        return String.format(
            "Name:     %s\nType:     %s\nColor:    %s\nQuantity: %d\nText:     %s\n",
            name, getCardType(), color, quantity, cardText
        );
    }
}
