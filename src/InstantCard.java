// InstantCard.java
// Represents an Instant card in Magic: The Gathering.
// Extends Card and adds an effect field to describe what the instant does.
// Overrides getCardType() to return "Instant".

public class InstantCard extends Card {

    // Instant-specific field - a short description of the card's effect
    private String effect;

    // Constructor with cardId (used when loading from database)
    public InstantCard(int cardId, String name, String color, int quantity, String cardText, String effect) {
        super(cardId, name, color, quantity, cardText);
        this.effect = effect;
    }

    // Overloaded constructor without cardId (used when creating a new card)
    public InstantCard(String name, String color, int quantity, String cardText, String effect) {
        super(name, color, quantity, cardText);
        this.effect = effect;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    @Override
    public String getCardType() {
        return "Instant";
    }

    @Override
    public String toString() {
        return String.format(
            "Name:     %s\nType:     %s\nColor:    %s\nQuantity: %d\nEffect:   %s\nText:     %s\n",
            getName(), getCardType(), getColor(), getQuantity(), effect, getCardText()
        );
    }
}
