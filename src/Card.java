// Card.java
// Abstract base class for all Magic: The Gathering card types.
// All card subclasses inherit these shared fields and methods.
// getCardType() is abstract so each subclass must provide its own implementation,
// which is how this app demonstrates polymorphism.
// Extra fields (setName, rarity, foil, price, manaCost) support bulk collection imports.

public abstract class Card {

    // Core private fields - accessible only through getters and setters
    private int cardId;
    private String name;
    private String color;
    private int quantity;
    private String cardText;

    // Extended fields for real collection tracking - default to empty so existing
    // constructors do not need to change
    private String setName  = "";
    private String rarity   = "";
    private String foil     = "normal";
    private double price    = 0.0;
    private String manaCost = "";

    // Constructor with all core fields (used when loading from database)
    public Card(int cardId, String name, String color, int quantity, String cardText) {
        this.cardId   = cardId;
        this.name     = name;
        this.color    = color;
        this.quantity = quantity;
        this.cardText = cardText;
    }

    // Overloaded constructor without cardId (used when adding a new card before DB assigns an ID)
    public Card(String name, String color, int quantity, String cardText) {
        this.cardId   = 0;
        this.name     = name;
        this.color    = color;
        this.quantity = quantity;
        this.cardText = cardText;
    }

    // --- Core Getters ---

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

    // --- Extended Getters ---

    public String getSetName() {
        return setName;
    }

    public String getRarity() {
        return rarity;
    }

    public String getFoil() {
        return foil;
    }

    public double getPrice() {
        return price;
    }

    public String getManaCost() {
        return manaCost;
    }

    // --- Core Setters ---

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

    // --- Extended Setters ---

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setFoil(String foil) {
        this.foil = foil;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    // Abstract method - every subclass must override this to return its card type string
    public abstract String getCardType();

    // toString uses String.format with % and \n for readable terminal output.
    // Extended fields are shown only when they have data so manually added cards
    // stay clean and imported cards show full collection details.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Name:     %s\n", name));
        sb.append(String.format("Type:     %s\n", getCardType()));
        sb.append(String.format("Color:    %s\n", color));
        sb.append(String.format("Quantity: %d\n", quantity));

        if (!manaCost.isEmpty()) {
            sb.append(String.format("Mana:     %s\n", manaCost));
        }
        if (!setName.isEmpty()) {
            sb.append(String.format("Set:      %s\n", setName));
        }
        if (!rarity.isEmpty()) {
            sb.append(String.format("Rarity:   %s\n", rarity));
        }
        if (!foil.isEmpty() && !foil.equals("normal")) {
            sb.append(String.format("Foil:     %s\n", foil));
        }
        if (price > 0.0) {
            sb.append(String.format("Price:    $%.2f\n", price));
        }
        if (!cardText.isEmpty()) {
            sb.append(String.format("Text:     %s\n", cardText));
        }
        return sb.toString();
    }
}
