// LandCard.java
// Represents a Land card in Magic: The Gathering.
// Extends Card and adds a manaType field to indicate what color mana it produces.
// Overrides getCardType() to return "Land".

public class LandCard extends Card {

    // Land-specific field - the color of mana this land produces (ex: "Green", "Blue", "Colorless")
    private String manaType;

    // Constructor with cardId (used when loading from database)
    public LandCard(int cardId, String name, String color, int quantity, String cardText, String manaType) {
        super(cardId, name, color, quantity, cardText);
        this.manaType = manaType;
    }

    // Overloaded constructor without cardId (used when creating a new card)
    public LandCard(String name, String color, int quantity, String cardText, String manaType) {
        super(name, color, quantity, cardText);
        this.manaType = manaType;
    }

    public String getManaType() {
        return manaType;
    }

    public void setManaType(String manaType) {
        this.manaType = manaType;
    }

    @Override
    public String getCardType() {
        return "Land";
    }

    @Override
    public String toString() {
        return String.format(
            "Name:     %s\nType:     %s\nColor:    %s\nQuantity: %d\nMana:     %s\nText:     %s\n",
            getName(), getCardType(), getColor(), getQuantity(), manaType, getCardText()
        );
    }
}
