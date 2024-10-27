package MCTG.core.models.cards;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Package extends CardCollection {
    private final int pId;

    public Package(int pId, List<Card> cards) {
        super(cards);
        this.pId = pId;
    }
}
