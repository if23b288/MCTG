package MCTG.core.models.cards;

public abstract class Card {
    private String name;
    private int damage = 0;
    private Element elementType;

    public Card(String name, int damage, Element elementType) {
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }

    @Override
    public String toString() {
        return "\nCard: " + name + "\nDamage: " + damage + "\nElement: " + elementType;
    }
}
