package MCTG.core.models.cards.monster;

public enum Monster {
    GOBLIN,
    DRAGON,
    WIZARD,
    KNIGHT,
    KRAKEN,
    ELF,
    ORK;

    public static Monster getMonster(String monster) {
        if (monster.contains("Goblin")) {
            return GOBLIN;
        } else if (monster.contains("Dragon")) {
            return DRAGON;
        } else if (monster.contains("Wizard")) {
            return WIZARD;
        } else if (monster.contains("Knight")) {
            return KNIGHT;
        } else if (monster.contains("Kraken")) {
            return KRAKEN;
        } else if (monster.contains("Elf")) {
            return ELF;
        } else if (monster.contains("Ork")) {
            return ORK;
        } else {
            return null;
        }
    }
}
