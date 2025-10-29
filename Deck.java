import java.util.*;

/**
 * Represents 52 distinct cards in a deck, shuffled
 */
public class Deck 
{
    private final List<Card> cards;

    /**
     * populates a deck with 52 distinct cards and then shuffles them
     */
    public Deck()
    {
        this.cards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                this.cards.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(this.cards);
    }

    /**
     * generic drawing of card, used for dealing at the start of the round, or for hitting
     * @return Card - a random card is removed from the deck and given to a player
     */
    public Card drawCard()
    {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No more cards in the deck!");
        }
        return cards.remove(0);
    }

    public void dealStartingCards(Player[] players, Dealer dealer)
    {
        for (int i = 0; i < 2; i++) { // two cards per player
            for (Player player : players) {
                if (!player.isBroke) {
                    player.getHands().get(0).addCard(drawCard());
                }
            }
            dealer.getHand().addCard(drawCard());
        }
    }

}
