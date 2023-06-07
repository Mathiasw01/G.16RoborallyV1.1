package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgrammingDeckInit {
    private List<CommandCard> deck;

    public ProgrammingDeckInit(){

    }


    public List<CommandCard> init(){
        deck=new ArrayList<>();
        deck.add(new CommandCard(Command.AGAIN));
        deck.add(new CommandCard(Command.UTURN));
        deck.add(new CommandCard(Command.LEFT));
        deck.add(new CommandCard(Command.LEFT));
        deck.add(new CommandCard(Command.LEFT));
        deck.add(new CommandCard(Command.RIGHT));
        deck.add(new CommandCard(Command.RIGHT));
        deck.add(new CommandCard(Command.RIGHT));
        deck.add(new CommandCard(Command.MOVE_BACK));
        deck.add(new CommandCard(Command.FORWARD));
        deck.add(new CommandCard(Command.FORWARD));
        deck.add(new CommandCard(Command.FORWARD));
        deck.add(new CommandCard(Command.FORWARD));
        deck.add(new CommandCard(Command.FORWARD));
        deck.add(new CommandCard(Command.FAST_FORWARD));
        deck.add(new CommandCard(Command.FAST_FORWARD));
        deck.add(new CommandCard(Command.FAST_FORWARD));
        deck.add(new CommandCard(Command.MOVE_THREE));
        deck.add(new CommandCard(Command.POWERUP));
        deck.add(new CommandCard(Command.CHOOSETURN));
        Collections.shuffle(deck);
        return deck;
    }
}
