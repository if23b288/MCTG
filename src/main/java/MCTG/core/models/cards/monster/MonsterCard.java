package MCTG.core.models.cards.monster;

import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Element;
import lombok.Getter;

@Getter
public class MonsterCard extends Card {
    private final Monster monsterType;

    public MonsterCard(String cId, String name, double damage, Element elementType, Monster monsterType) {
        super(cId, name, damage, elementType);
        this.monsterType = monsterType;
    }

    @Override
    public String toString() {
        return "\nMONSTERCARD || MonsterType: " + monsterType + " | " + super.toString();
    }
}
