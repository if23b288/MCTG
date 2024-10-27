package MCTG.core.models.cards;

public enum Element {
    FIRE,
    WATER,
    NORMAL;

    public static Element getElement(String element) {
       if (element.contains("Fire")) {
           return FIRE;
       } else if (element.contains("Water")) {
           return WATER;
       } else {
           return NORMAL;
       }
    }
}
