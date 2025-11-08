/**
 * Represents a player by their username, their cash and their hand(s)
 */
import java.util.ArrayList;
import java.util.List;

public class Player 
{
    private String name;
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

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * determines the best possible hand for a bot to play based on the dealer's card
     * and a strategy graph
     * @param dealerCard
     * @return String - the move (H/D/S/E)
     */
    public String computeMove(Card dealerCard, int handIndex) 
    {
        Hand currentHand = getHands().get(handIndex);
        Rank rank1 = currentHand.getCards().get(0).getRank();
        Rank rank2 = currentHand.getCards().get(1).getRank();
        Rank notAce = rank2;
        Rank dealerRank = dealerCard.getRank();
        int score = getHands().get(0).computeValue();

        // section 1 - player has double cards
        if (hasDoubleCard()) {
            // player: 10-10, J-J, Q-Q, K-K
            if (rank1.ordinal() >= Rank.TEN.ordinal() && rank1.ordinal() <= Rank.KING.ordinal()) {
                return "E";
            // player: 9-9
            } else if (rank1 == Rank.NINE) {
                if (dealerRank == Rank.SEVEN || dealerRank.ordinal() >= Rank.TEN.ordinal()) {
                    return "E";
                } else {
                    return canSplit ? "S" : "E";
                }
            // player: A-A, 8-8
            } else if (rank1 == Rank.ACE || rank1 == Rank.EIGHT) {
                return canSplit ? "S" : "E";
            // player: 6-6
            } else if (rank1 == Rank.SIX) {
                if (rank1.ordinal() >= Rank.SEVEN.ordinal()) {
                    return "H";
                } else return canSplit ? "S" : "H";
            // player: 5-5
            } else if (rank1 == Rank.FIVE) {
                if (rank1.ordinal() >= Rank.TEN.ordinal()) {
                    return "H";
                } else return "D";
            // player: 4-4
            } else if (rank1 == Rank.FOUR) {
                if (rank1 == Rank.FIVE || rank1 == Rank.SIX) {
                    return canSplit ? "S" : "H";
                } else return "H";
            // player: 7-7, 3-3, 2-2
            } else if (dealerRank == Rank.THREE || dealerRank == Rank.TWO || dealerRank == Rank.SEVEN) {
                if (rank1.ordinal() >= Rank.EIGHT.ordinal()) {
                    return "H";
                } else return canSplit ? "S" : "H";
            }

        // section 2 - player has an Ace
        } else if (rank1 == Rank.ACE || rank2 == Rank.ACE) {
            if (rank2 == Rank.ACE) {
                notAce = rank1;
            }
            // player: A-8+
            if (notAce.ordinal() >= Rank.EIGHT.ordinal()) {
                return "E";
            // player: A-7
            } else if (notAce == Rank.SEVEN) {
                if (dealerRank == Rank.TWO || dealerRank == Rank.SEVEN || dealerRank == Rank.EIGHT) {
                    return canSplit ? "S" : "H";
                } else if (dealerRank.ordinal() >= Rank.THREE.ordinal() && dealerRank.ordinal() <= Rank.SIX.ordinal()) {
                    return canDoubleDown ? "D" : "H";
                }
            // player: A-6
            } else if (notAce == Rank.SIX) {
                if (dealerRank.ordinal() >= Rank.THREE.ordinal() && dealerRank.ordinal() <= Rank.SIX.ordinal()) {
                    return canDoubleDown ? "D" : "H";
                } else return "H";
            // player: A-5, A-4
            } else if (notAce == Rank.FIVE || notAce == Rank.FOUR) {
                if (dealerRank.ordinal() >= Rank.FOUR.ordinal() && dealerRank.ordinal() <= Rank.SIX.ordinal()) {
                    return canDoubleDown ? "D" : "H";
                } else return "H";
            // player: A-3, A-2
            } else if (notAce == Rank.THREE || notAce == Rank.TWO) {
                if (dealerRank.ordinal() >= Rank.FIVE.ordinal() && dealerRank.ordinal() <= Rank.SIX.ordinal()) {
                    return canDoubleDown ? "D" : "H";
                } else return "H";
            }

        // section 3 - look at player score
        } else if (score >= 17) {
            return "E";
        } else if (score >= 13 && score <= 16) {
            if (dealerRank.ordinal() <= Rank.SEVEN.ordinal()) {
                return "E";
            } else return "H";
        } else if (score == 12) {
            if (dealerRank.ordinal() >= Rank.FOUR.ordinal() && dealerRank.ordinal() <= Rank.SIX.ordinal()) {
                return "E";
            } else return "H";
        } else if (score == 11) {
            if (dealerRank == Rank.ACE) {
                return "H";
            } else return canDoubleDown ? "D" : "H";
        } else if (score == 10) {
            if (dealerRank.ordinal() >= Rank.TEN.ordinal()) {
                return "H";
            } else return canDoubleDown ? "D" : "H";
        } else if (score == 9) {
            if (dealerRank.ordinal() >= Rank.THREE.ordinal() && dealerRank.ordinal() <= Rank.SIX.ordinal()) {
                return canDoubleDown ? "D" : "H";
            } return "H";
        } 
        return "H";
    }

    // helper function used to compute best move (by bot)
    private boolean hasDoubleCard()
    {
        // if player has 2 cards of same rank
        if (getHands().get(0).getCards().get(0).getRank() == 
            getHands().get(0).getCards().get(1).getRank()) {
            return true;
        }
        return false;
    }

}
