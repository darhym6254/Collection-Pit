// CreatureCard.java
// Represents a Creature card in Magic: The Gathering.
// Extends Card and adds power and toughness stats.
// Overrides getCardType() to return "Creature", demonstrating polymorphism.

public class CreatureCard extends Card {

    // Creature-specific fields
    private int power;
    private int toughness;

    // Constructor with all fields including cardId (used when loading from database)
    public CreatureCard(int cardId, String name, String color, int quantity, String cardText, int power, int toughness) {
        super(cardId, name, color, quantity, cardText);
        this.power = power;
        this.toughness = toughness;
    }

    // Overloaded constructor without cardId (used when creating a new card)
    public CreatureCard(String name, String color, int quantity, String cardText, int power, int toughness) {
        super(name, color, quantity, cardText);
        this.power = power;
        this.toughness = toughness;
    }

    // Getters and setters for creature-specific fields
    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getToughness() {
        return toughness;
    }

    public void setToughness(int toughness) {
        this.toughness = toughness;
    }

    // Returns the card type string - this is what makes polymorphism work
    @Override
    public String getCardType() {
        return "Creature";
    }

    // Creature cards show power/toughness on top of the base card info
    @Override
    public String toString() {
        return String.format(
            "Name:     %s\nType:     %s\nColor:    %s\nQuantity: %d\nP/T:      %d/%d\nText:     %s\n",
            getName(), getCardType(), getColor(), getQuantity(), power, toughness, getCardText()
        );
    }
}
