# Blackjack
coded by Art

### How to run:
1) open terminal in this directory
2) enter javac Blackjack.java
3) enter java Blackjack

## Overview:
-  Features gorgeous, state-of-the-16k-art computer graphics and riveting gamplay!!!!1!!
-  Play blackjack alongside two bots, Kenny and Elle
-  Pick up cards to get a value as close as possible to (but not over) 21. 
-  Face cards(Jack, Queen, King) have values of 10. 
-  Ace card can have a value of either 1 or 11, depending on circumstance
-  Bust: if you accidentally go over 21, you lose the round
-  If you score a 21, or a higher value than the dealer by the end of the round, or the dealer busts, you win back twice your bet (eg. if you bet $10, you win back $20)
-  Push: if you end up with the same value as the dealer, you win back your bet (eg. if you bet $$10, you win back exactly $10)
-  If you bust, or the dealer has a higher value than you, you win nothing.
-  Natural: if you get a blackjack (21) on your starting 2 cards, you automatically win a 3:2 payout (eg. if you bet $10, you win $25), unless the dealer also ends with 21
-  Hit: add another card to your hand
-  End turn: end turn with your current hand and move on to your next hand, or to the next player
-  Double down: Double your bet and pick up exactly one more card. Ends your turn. Only available at the start of a turn
-  Split: If your starting cards have the same rank, double your bet and split your cards into 2 independent hands. Hit/End turn for both hands separately, and earn winnings separately. Only available at the start of a turn, with cards that have matching ranks

## Gameplay
-  Press Y to start a game, or N to terminate
-  Enter your name
-  Enter a starting bet between 1 and your current cash (starts at $100)
-  You and the bot players receive your respective 2 starting cards; dealer only show 1 of their cards
-  Choose one of your available moves: "[H]it, [E]nd turn, [D]ouble down, [S]plit" until you bust, get a 21, or choose [E]nd turn
-  Wait for Kenny and Elle to finish their turns
-  Wait for the dealer to reveal their cards
-  Win (or lose) cash based on the outcome
-  Press Y to shuffle the deck and bet on a new round, or N to end the game
-  If you lose all of your money, you can no longer participate in the game. Ctrl+C to end game

