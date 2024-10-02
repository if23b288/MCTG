package MCTG.core.models.cards.monster;

import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Element;

public class MonsterCard extends Card {
    private Monster monsterType;

    public MonsterCard(String name, int damage, Element elementType, Monster monsterType) {
        super(name, damage, elementType);
        this.monsterType = monsterType;
    }

    @Override
    public String toString() {
        return "MonsterCard\n-----------\nMonsterType: " + monsterType + super.toString();
    }
}
