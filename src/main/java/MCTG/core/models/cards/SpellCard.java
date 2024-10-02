package MCTG.core.models.cards;

public class SpellCard extends Card {
    public SpellCard(String name, int damage, Element elementType) {
        super(name, damage, elementType);
    }

    @Override
    public String toString() {
        return "SpellCard\n------------" + super.toString();
    }
}
