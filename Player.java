/**
 * Represents a player by their username, their cash and their hand(s)
 */
import java.util.ArrayList;
import java.util.List;

public class Player 
{
    private final String name;
    public boolean isBot = false;
    public int cash;
    public int lastBet; // last amount bet by player
    public final List<Hand> hands; // card hand(s) - cards the player has
    public boolean isStartOfRound = true;
    public boolean hasNaturalBlackjack = false;
    public boolean hasBlackjack = false;
    public boolean canSplit = false;
    public boolean canDoubleDown = true;
    public boolean hasBusted = false;
    public boolean hasEndedTurn = false;
    public boolean isBroke = false;

    public Player(String name, int cash) 
    {
        this.name = name;
        this.cash = cash;
        this.lastBet = 0;
        this.hands = new ArrayList<>();
        this.hands.add(new Hand());
    }

    public String getName() 
    {
        return name;
    }

    public int getCash()
    {
        return cash;
    }

    public void addHand(Hand hand) 
    {
        hands.add(hand);
    }

    public List<Hand> getHands() 
    {
        return hands;
    }

    public void addCardToHand(int handIndex, Card card) 
    {
        hands.get(handIndex).addCard(card);
    }

}
