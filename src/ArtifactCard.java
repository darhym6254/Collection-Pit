// ArtifactCard.java
// Represents an Artifact card in Magic: The Gathering.
// Extends Card and overrides getCardType() to return "Artifact".
// Artifact cards share the same fields as the base Card class.

public class ArtifactCard extends Card {

    // Constructor with cardId (used when loading from database)
    public ArtifactCard(int cardId, String name, String color, int quantity, String cardText) {
        super(cardId, name, color, quantity, cardText);
    }

    // Overloaded constructor without cardId (used when creating a new card)
    public ArtifactCard(String name, String color, int quantity, String cardText) {
        super(name, color, quantity, cardText);
    }

    @Override
    public String getCardType() {
        return "Artifact";
    }

    @Override
    public String toString() {
        return String.format(
            "Name:     %s\nType:     %s\nColor:    %s\nQuantity: %d\nText:     %s\n",
            getName(), getCardType(), getColor(), getQuantity(), getCardText()
        );
    }
}
