/**
 * Represents a single card with a suit and rank
 */
public class Card
{
    private final Suit suit;
    private final Rank rank;

    /**
     * constructor
     * @param suit - card family
     * @param rank - card value
     */
    public Card(Suit suit, Rank rank)
    {
        this.suit = suit;
        this.rank = rank;
    }

    /***************************************************************************
     * GETTERS
     **************************************************************************/
    public Suit getSuit()
    {
        return suit;
    }

    public Rank getRank()
    {
        return rank;
    }

    @Override
    public String toString()
    {
        return rank + " of " + suit;
    }

    // helper function
    private String _getRank()
    {
        switch(getRank()) {
        case TWO:
            return "2 ";
        case THREE:
            return "3 ";
        case FOUR:
            return "4 ";
        case FIVE:
            return "5 ";
        case SIX:
            return "6 ";
        case SEVEN:
            return "7 ";
        case EIGHT:
            return "8 ";
        case NINE:
            return "9 ";
        case TEN:
            return "10";
        case JACK:
            return "J ";
        case QUEEN:
            return "Q ";
        case KING:
            return "K ";
        default:
            return "A ";
        }
    }


    /**
     * Prints out an ASCII representation of the card
     * @return
     */
    public String printCard(boolean hidden)
    {
        if (hidden) {
            return  " ___ \n" + "|? " + " |\n" + "| ?" + " |\n" + "|__?|\n";
        }
        String suit; 
        switch(getSuit()) {
            case DIAMONDS:
                suit = "♦";
                break;
            case HEARTS:   
                suit = "♥";
                break;
            case CLUBS:
                suit = "♣";
                break;
            default:      
                suit = "♠";
                break;
        }

        String card;
        card = " ___ \n";
        card += "|" + _getRank() + " |\n";
        card += "| " + suit + " |\n";
        card += "|___|\n";
        return card;
    }

}