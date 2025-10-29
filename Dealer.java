/**
 * Represents the dealer player, who behaves a little different from the regular player
 * One card is hidden at the start
 * Picks up until hand value is >= 17 (uses soft hands)
 */
public class Dealer
{
    public Hand hand;
    public boolean hasBusted = false;
    public boolean hasBlackjack = false;

    public Dealer()
    {
        hand = new Hand();
    }

    public void addCard(Card card)
    {
        hand.addCard(card);
    }

    public Hand getHand()
    {
        return hand;
    }

    // pick up cards until hand value is >= 17
    public void play(Deck deck, int highestValue)
    {
        while (hand.computeValue() < 17 && hand.computeValue() < highestValue) {
            hand.addCard(deck.drawCard());
        }
    }

    public int getPartialValue()
    {
        return hand.getPartialValue();
    }

    public void reset()
    {
        hand = new Hand();     // create a fresh empty hand
        hasBusted = false;
        hasBlackjack = false;
    }
}
