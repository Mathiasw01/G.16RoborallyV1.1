package dk.dtu.compute.se.pisd.roborally.model;

import java.util.Collections;
import java.util.List;

public class ProgrammingDeck {
    private List<CommandCard> deck;

    public ProgrammingDeck(List<CommandCard> deck) {
        this.deck = deck;
    }

    public List<CommandCard> shuffleDeck(){
        Collections.shuffle(deck);
        return deck;
    }

}
