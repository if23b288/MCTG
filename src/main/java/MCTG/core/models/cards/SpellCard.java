package MCTG.core.models.cards;

public class SpellCard extends Card {
    public SpellCard(String cId, String name, double damage, Element elementType) {
        super(cId, name, damage, elementType);
    }

    @Override
    public String toString() {
        return "SpellCard\n------------" + super.toString();
    }
}
