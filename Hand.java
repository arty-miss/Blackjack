import java.util.ArrayList;
import java.util.List;

/**
 * Represents the cards that a player has (can have multiple hands if they split their cards)
 */
public class Hand 
{
    private final List<Card> cards = new ArrayList<>();
    public int value;
    public boolean isBusted = false;

    public void addCard(Card card) 
    {
        cards.add(card);
        this.value = computeValue();
    }

    public List<Card> getCards() 
    {
        return cards;
    }

    public int getValue()
    {
        return this.value;
    }

    public int size()
    {
        return cards.size();
    }

    @Override
    public String toString() 
    {
        return cards.toString();
    }

    public String printHands()
    {
        String result = "";
        for (Card card : cards) {
            result += card.printCard(false);
        }
        return result;
    }

    // counts the values of a hand, accounts for ace as 1 or 11
    public int computeValue() 
    {
        int value = 0;
        for (Card card : cards) {
            value += card.getRank().getValue();

            // if ace, find appropriate value
            if (card.getRank() == Rank.ACE) {
                if (value + 11 > 21) {
                    value += 1;
                } else {
                    value += 11;
                }
            }
        }
        return value;
    }

    // for dealer's whose cards are hidden, only show one value
    public int getPartialValue()
    {
        int value = 0;
        for (int i = 1; i < cards.size(); i++) {
            value += cards.get(i).getRank().getValue();

            // if ace, find appropriate value
            if (cards.get(i).getRank() == Rank.ACE) {
                    value += 11;
            }
        }
        return value;
    }

}
