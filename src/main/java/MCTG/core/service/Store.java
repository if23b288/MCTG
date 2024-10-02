package MCTG.core.service;

import java.util.ArrayList;

public class Store {
    private ArrayList<Trade> trades;

    public Store() {
        this.trades = new ArrayList<Trade>();
    }

    public void addTrade(Trade trade) {
        this.trades.add(trade);
    }
}
