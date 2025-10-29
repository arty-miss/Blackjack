import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Blackjack 
{
    public static void main(String[] args) 
    {
        // --- set up players ---
        Scanner playerInput = new Scanner(System.in);
        System.out.println("=======================================\n");
        System.out.println("\nStarting a new game of blackjack:");
        Player player1 = new Player("Player 1", 100);
        Player botDumb = new Player("Kenny", 100);
        botDumb.isBot = true;
        Player[] players = new Player[] {player1, botDumb};
        Dealer dealer = new Dealer();
        boolean playAgain = false;

        // --- prompt start of game ---
        System.out.println("Ready to play? (Y/N)");
        if (playerInput.nextLine().trim().toUpperCase().equals("Y")) {
            playAgain = true;
        }

        while (playAgain) {
            resetRound(players, dealer);
            // --- determine player bets ---
            printCash(players);
            for (Player player : players) {
                if (!player.isBroke) {
                    if (player.isBot) {
                        // bot automatically bets a random amount
                        player.lastBet = (int)(Math.random() * player.cash) + 1; // bet between 1 and all cash
                        System.out.println("\n" + player.getName() + " bets $" + player.lastBet);
                        player.cash -= player.lastBet;
                    } else {
                        // human player input
                        System.out.println("\n" + player.getName() + ", how much would you like to bet?");
                        player.lastBet = playerInput.nextInt();
                        playerInput.nextLine();// consume newline
                        if (player.lastBet > player.cash) {
                            System.out.println("You don't have that much cash! Going all in with your balance of $" + player.cash + ".");
                            player.lastBet = player.cash;
                        } else if (player.lastBet <= 0) {
                            System.out.println("You must bet at least $1.");
                            player.lastBet = 1;
                        }
                        player.cash -= player.lastBet;
                    }
                }
            }
            System.out.println("\nUpdated cash:");
            printCash(players);



            // --- shuffle deck ---
            Deck deck = new Deck();
            System.out.println("\nDealer is shuffling the deck...");

            // --- deal out cards to all players ---
            System.out.println("Dealing out cards...");
            deck.dealStartingCards(players, dealer);
            System.out.println("\n=======================================\n");

            // print out all player's starting hands and values
            for (Player player : players) {
                if (!player.isBroke) {
                    for (Hand hand : player.getHands()) {
                        System.out.println("--- " +player.getName() + "'s hand ---");
                        printHandSideBySide(hand, false);
                        System.out.println();
                        System.out.println("  Value: " + hand.getValue());
                        evaluateHand(player, 0);
                        System.out.println("\n---------------------------------------\n");
                    }
                player.isStartOfRound = false;
                }
            }

            // --- print dealer's hidden hand ---
            System.out.println("--- Dealer's hand ---");
            printHandSideBySide(dealer.getHand(), true);
            System.out.println();
            System.out.println("  Value: " + dealer.getPartialValue() + "+");
            System.out.println("\n=======================================\n");

            //  --- players' turn to choose action ---
            for (Player player : players) {
                if (!player.isBroke) {
                    System.out.println("--- " + player.getName() + "'s turn ---\n");

                    // get all hands of player
                    for (int handIndex = 0; handIndex < player.getHands().size(); handIndex++) {
                        Hand currentHand = player.getHands().get(handIndex);
                        System.out.println("Playing Hand " + (handIndex + 1) + ":");
                        printHandSideBySide(currentHand, false);
                        System.out.println("  Value: " + currentHand.computeValue());
                        System.out.println();

                        // end turn automatically if THIS HAND has blackjack
                        String input = "";
                        while (true) {
                            int handValue = currentHand.computeValue();
                        
                             // auto-end if this specific hand has 21
                            if (handValue == 21) {
                                if (player.isStartOfRound && currentHand.getCards().size() == 2) {
                                    System.out.println(player.getName() + " has natural blackjack! Ending turn...");
                                } else {
                                    System.out.println(player.getName() + " has 21! Ending turn...");
                                }
                                break;
                            }

                            // determine available moves
                            String options = evaluateOptionsToDisplay(player, handIndex);
                            System.out.println(options);
                            input = getPlayerMove(player, playerInput, options);

                            if (!"HEDS".contains(input) || input.length() != 1) {
                                System.out.println("Invalid input!");
                                continue;
                            }

                            switch (input) {
                                case "H":
                                    handleHit(player, deck, handIndex);
                                    break;
                                case "E":
                                    System.out.println(player.getName() + " ends Hand " + (handIndex + 1));
                                    break;
                                case "D":
                                    if (!player.canDoubleDown) {
                                        System.out.println("Invalid input!");
                                        break;
                                    }
                                    handleDoubleDown(player, deck, handIndex);
                                    break;
                                case "S":
                                    if (!player.canSplit) {
                                        System.out.println("Invalid input!");
                                        break;
                                    }
                                    handleSplit(player, deck);
                                    break;
                            }

                            // after split, restart this loop since there are now two hands
                            if (input.equals("S")) {
                                handIndex = -1; // will become 0 on next iteration
                                break;
                            }

                            // check if THIS HAND is done (busted or player ended turn)
                            if (currentHand.isBusted || input.equals("E") || player.isBroke || player.hasEndedTurn) {
                                break;
                            }

                        }
                        System.out.println("\n---------------------------------------\n");

                    }
                System.out.println("\n=======================================\n");
                }
            }


            // --- handle dealer's move ---
            System.out.println("--- Dealer's turn ---\n");

            // first check if automatic win (if all have busted, no need to pick up cards)
            boolean allBusted = true;
            int highestValue = 0;
            for (Player player : players) {
                boolean allHandsBusted = true;
                for (Hand hand : player.getHands()) {
                    if (!hand.isBusted) {
                        allHandsBusted = false;
                        if (hand.computeValue() > highestValue) {
                            highestValue = hand.computeValue();
                        }
                        break;
                    }
                }
                if (!allHandsBusted && !player.isBroke) {
                    allBusted = false;
                    break;
                }
            }

            if (allBusted) {
                System.out.println("Dealer wins! All players have busted.");
            } else {
                dealer.play(deck, highestValue);
                System.out.println("--- Dealer's hand ---");
                printHandSideBySide(dealer.getHand(), false);
                System.out.println();
                System.out.println("  Value: " + dealer.getHand().computeValue());
                evaluateDealerHand(dealer);
            }
            // --- determine winners ---
            handleWinners(players, dealer);

            // --- ask if user wants to play another round ---
            System.out.println("\n=======================================\n");
            printCash(players);
            System.out.println("Do you want to play another round? (Y/N)");
            String input = playerInput.nextLine().trim().toUpperCase();
            if (input.equals("Y")) {
                System.out.println("\n=======================================");
                System.out.println("=======================================\n");
                System.out.println("\n --- Starting a new round ---\n");
            } else {
                playAgain = false;
                System.out.println("\n --- Game has ended --- \n");
                System.out.println("Final standings:");
                printCash(players);
                playerInput.close();
            }

        } // end of game loop

    }

    /**
     * handles the hit move by a player
     * adds a new card to the player's hand
     * disables double down and split
     * checks if player busts
     * 
     * @param player
     * @param deck
     */
    public static void handleHit(Player player, Deck deck, int handIndex)
    {
        // draw one more card
        System.out.println(player.getName() + " has chosen to hit.");
        player.addCardToHand(handIndex, deck.drawCard());
        System.out.println("--- " + player.getName() + "'s hand ---");

        Hand hand = player.getHands().get(handIndex);
        printHandSideBySide(hand, false);
        System.out.println("  Value: " + hand.computeValue() + "\n");

        // evaluate hand
        evaluateHand(player, handIndex);
        player.canDoubleDown = false;
        player.canSplit = false;
        return;
    }

    /**
     * handles the double down move by a player
     * only available if start of turn and if player has enough cash to warrant a doubling of bet
     * adds one new card to the player's hand
     * subtracts bet from player for a second time
     * doulbes last bet value
     * checks if player busts
     * ends turn
     * 
     * @param player
     * @param deck
     */
    public static void handleDoubleDown(Player player, Deck deck, int handIndex)
    {
        System.out.println(player.getName() + " has chosen to double down.");
        player.addCardToHand(handIndex, deck.drawCard());
        System.out.println("--- " + player.getName() + "'s hand ---");

        Hand hand = player.getHands().get(handIndex);
        printHandSideBySide(hand, false);
        System.out.println("  Value: " + hand.computeValue() + "\n");

        // update bets
        player.cash -= player.lastBet;
        player.lastBet *= 2;
        System.out.println(player.getName() + " has doubled their bet and now has $" + player.getCash());

        // evaluate hand
        evaluateHand(player, handIndex);
        player.canDoubleDown = false;
        player.canSplit = false;
        player.hasEndedTurn = true;
    }

    /**
     * handles the split move by a player
     * only available if start of round, player's cards are of the same rank,
     * and player can afford a second bet
     * splits the cards of player's first hand into two separate hands
     * picks up one new card for each hand (total of 2 new cards)
     * player can now play both hands separately (hit, stand)
     * @param player
     * @param deck
     */
    public static void handleSplit(Player player, Deck deck)
    {
        System.out.println(player.getName() + " has chosen to split their hand.");

        // extract 2 cards from original hand
        Hand original = player.getHands().get(0);
        if (original.getCards().size() != 2) {
            System.out.println("Error: cannot split — hand doesn't have exactly 2 cards!");
            return;
        }

        Card card1 = original.getCards().remove(0);
        Card card2 = original.getCards().remove(0);

        // clear old hands and create two new ones
        player.getHands().clear();
        Hand hand1 = new Hand();
        Hand hand2 = new Hand();
        player.getHands().add(hand1);
        player.getHands().add(hand2);

        // distribute the original pair across both hands
        hand1.addCard(card1);
        hand2.addCard(card2);

        // draw one new card for each split hand
        hand1.addCard(deck.drawCard());
        hand2.addCard(deck.drawCard());

        // deduct additional bet
        player.cash -= player.lastBet;
        System.out.println(player.getName() + " has paid an extra bet of $" + player.lastBet);
        System.out.println("Remaining cash: $" + player.cash);

        // display the two hands
        for (int i = 0; i < player.getHands().size(); i++) {
            System.out.println("--- " + player.getName() + "'s Hand " + (i + 1) + " ---");
            printHandSideBySide(player.getHands().get(i), false);
            System.out.println("  Value: " + player.getHands().get(i).computeValue());
        }

        // disable other moves after split
        player.canDoubleDown = false;
        player.canSplit = false;
    }


    // helper function to print out a player's hand
    public static void printHandSideBySide(Hand hand, boolean hideFirstCard)
    {
        // convert each card to array of lines
        List<String[]> cardLines = new ArrayList<>();
        for (Card card : hand.getCards()) {
            cardLines.add(card.printCard(hideFirstCard).split("\n"));
            if (hideFirstCard) {
                hideFirstCard = false; // only first card
            }
        }

        // each card has 4 lines (___, rank, suit, ___)
        for (int line = 0; line < 4; line++) {
            for (String[] card : cardLines) {
                System.out.print(card[line] + " "); // print cards on the same line
            }
            System.out.println(); // move to next row
        }
    }

    // checks if player's hand wins or busts
    public static void evaluateHand(Player player, int handIndex)
    {
        Hand hand = player.getHands().get(handIndex);
        int value = hand.computeValue();

        if (value > 21) {
            System.out.println(player.getName() + " has busted!");
            hand.isBusted = true;  // Mark THIS hand as busted
        } else if (value == 21) {
            if (player.isStartOfRound && hand.getCards().size() == 2) {
                System.out.println(player.getName() + " has natural blackjack!");
                player.hasNaturalBlackjack = true;
            } else {
                System.out.println("Blackjack!");
            }
        }
    }

    // checks dealer's value and compares to all other players
    public static void evaluateDealerHand(Dealer dealer)
    {
        if (dealer.getHand().computeValue() > 21) {
            System.out.println("Dealer has busted!");
            dealer.hasBusted = true;
        } else if (dealer.getHand().computeValue() == 21) {
            System.out.println("Dealer has blackjack!");
            dealer.hasBlackjack = true;
        }
    }

    // checks player's available options and only displays those
    public static String evaluateOptionsToDisplay(Player player, int handIndex)
    {
        Hand currentHand = player.getHands().get(handIndex);

        String options = "Options: ";
        if (!currentHand.isBusted) {
            options += "[H]it, [E]nd turn";
        }

        // Double down only available on first hand, at start, with enough cash
        if (handIndex == 0 && player.canDoubleDown && player.getCash() >= player.lastBet) {
            options += ", [D]ouble down";
        }

        // Split only on first hand with 2 matching cards
        if (handIndex == 0 && currentHand.getCards().size() == 2) {
            Card card1 = currentHand.getCards().get(0);
            Card card2 = currentHand.getCards().get(1);
            if (card1.getRank() == card2.getRank() && player.getCash() >= player.lastBet) {
                player.canSplit = true;  // Enable split if conditions are met
                options += ", [S]plit";
            } else {
                player.canSplit = false;
            }
        }

        return options;
    }

    // resets player states for new round
    public static void resetRound(Player[] players, Dealer dealer)
    {
        for (Player player : players) {
            player.isStartOfRound = true;
            player.hasBusted = false;
            player.hasEndedTurn = false;
            player.hasBlackjack = false;
            player.hasNaturalBlackjack = false;
            player.canDoubleDown = true;
            player.canSplit = false;
            player.hands.clear();
            player.hands.add(new Hand());
            player.lastBet = 0;
        }
        dealer.reset();
    }



    /**
     * compares all players' hands with dealer and determines who wins
     * @param players - all players in the game
     * @param dealer
     */
    public static void handleWinners(Player[] players, Dealer dealer)
    {
        System.out.println("\n=======================================\n");
        System.out.println("Evaluating hands:\n");

        int dealerVal = dealer.getHand().computeValue();
        System.out.println("Dealer has value:\t" + dealerVal + "\n");

        // --- case 1: dealer busted ---
        if (dealer.hasBusted) {
            for (Player player : players) {
                if (!player.isBroke) {
                    // loop through multiple hands (in case of split)
                    for (int h = 0; h < player.getHands().size(); h++) {
                        Hand hand = player.getHands().get(h);
                        int handVal = hand.computeValue();
                        if (handVal <= 21) {
                            System.out.println(player.getName() + "'s Hand " + (h + 1) + " wins! Dealer busted.");
                            double winnings = player.hasNaturalBlackjack ? player.lastBet * 2.5 : player.lastBet * 2;
                            System.out.println(player.getName() + " wins $" + winnings);
                            player.cash += winnings;
                        } else {
                            System.out.println(player.getName() + "'s Hand " + (h + 1) + " has busted — no winnings.");
                        }
                    }
                }
            }
            return;
        }

        // --- dealer did not bust, compare each player's hand ---
        for (Player player : players) {
            if (player.isBroke) continue;

            for (int h = 0; h < player.getHands().size(); h++) {
                Hand hand = player.getHands().get(h);
                int playerVal = hand.computeValue();

                System.out.println(player.getName() + " - Hand " + (h + 1) + " value: " + playerVal);

                // bust check
                if (playerVal > 21) {
                    System.out.println("  -> Busted! Loses bet of $" + player.lastBet);
                    continue;
                }

                // win conditions
                if (playerVal > dealerVal) {
                    double winnings = player.hasNaturalBlackjack ? player.lastBet * 2.5 : player.lastBet * 2;
                    System.out.println("  -> Wins against dealer! +" + winnings);
                    player.cash += winnings;
                } 
                // tie
                else if (playerVal == dealerVal) {
                    System.out.println("  -> Push! Bet of $" + player.lastBet + " returned.");
                    player.cash += player.lastBet;
                } 
                // loss
                else {
                    System.out.println("  -> Dealer wins this hand.");
                }
            }
            System.out.println();
        }
    }


    // prints out all users' cash
    public static void printCash(Player[] players)
    {
        for (Player player : players) {
            if (player.getCash() == 0 && !player.isStartOfRound) {
                System.out.println(player.getName() + " is broke!");
                player.isBroke = true;
            } else {
                System.out.println(player.getName() + " has\t$" + player.getCash());
            }
        }
    }

    /**
     * helper function that handles player input from both real players and bots
     * @param player
     * @param scanner
     * @param options
     * @return
     */
    public static String getPlayerMove(Player player, Scanner scanner, String options) {
        if (!player.isBot) {
            // human: read from scanner
            return scanner.nextLine().trim().toUpperCase();
        } else {
            // bot: pick random from available moves
            List<Character> legalMoves = new ArrayList<>();
            if (options.contains("[H]")) legalMoves.add('H');
            if (options.contains("[E]")) legalMoves.add('E');
            if (options.contains("[D]")) legalMoves.add('D');
            if (options.contains("[S]")) legalMoves.add('S');

            // pick a random one, simulate thinking

            System.out.println(player.getName() + " is thinking really hard...");
            try { Thread.sleep(4500); } catch (InterruptedException e) {}
            char choice = legalMoves.get((int)(Math.random() * legalMoves.size()));
            return String.valueOf(choice);
        }
    }


}
