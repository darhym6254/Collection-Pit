// EnchantmentCard.java
// Represents an Enchantment card in Magic: The Gathering.
// Extends Card and overrides getCardType() to return "Enchantment".
// Enchantment cards share the same fields as the base Card class.

public class EnchantmentCard extends Card {

    // Constructor with cardId (used when loading from database)
    public EnchantmentCard(int cardId, String name, String color, int quantity, String cardText) {
        super(cardId, name, color, quantity, cardText);
    }

    // Overloaded constructor without cardId (used when creating a new card)
    public EnchantmentCard(String name, String color, int quantity, String cardText) {
        super(name, color, quantity, cardText);
    }

    @Override
    public String getCardType() {
        return "Enchantment";
    }

    @Override
    public String toString() {
        return String.format(
            "Name:     %s\nType:     %s\nColor:    %s\nQuantity: %d\nText:     %s\n",
            getName(), getCardType(), getColor(), getQuantity(), getCardText()
        );
    }
}
