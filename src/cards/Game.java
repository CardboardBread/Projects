package cards;

import java.util.ArrayList;

public class Game {

	int players;
	Deck supply;
	Player[] playerlist;

	public Game(int players) {
		this.players = players;
		playerlist = new Player[this.players];
		for (int i = 0; i < playerlist.length; i++) {
			int next = i + 1;
			int last = i - 1;

			if (i == 0) {
				last = playerlist.length - 1;
			}
			if (i == playerlist.length - 1) {
				next = 0;
			}

			playerlist[i] = new Player(playerlist[next], playerlist[last], null);
		}
	}

}

class Player {

	public Hand hand;
	private Player next;
	private Player last;

	public Player(Player next, Player last, Card[] cards) {
		hand = new Hand();
		for (Card c : cards) {
			hand.add(c);
		}
		this.next = next;
		this.last = last;
	}

	public void draw(Deck source) {
		hand.add(source.draw());
	}

	public void draw(Hand source, int index) {
		hand.add(source.pick(index));
	}

	public void draw(Player source, int index) {
		hand.add(source.hand.pick(index));
	}

}
