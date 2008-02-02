package unittest.pass;

import org.joe_e.*;

public class EnumStringConversion {
	enum Suits implements Powerless, Equatable 
		{CLUBS {public String toString() { return "♣";};
				public String printCard(String rank) {
                    return rank + this;
				}},
		 DIAMONDS {public String toString() { return "♦";}
				   public String printCard(String rank) {
				       return rank + this;
			       }},
		 HEARTS {public String toString() { return "♥";}
			     public String printCard(String rank) {
                     return rank + this;
			     }},
		 SPADES {public String toString() { return "♠";}
		   		 public String printCard(String rank) {
                     return rank + this;
		   		 }};
		
		 public abstract String printCard(String rank);}
	
	String f() {
		assert true : Suits.DIAMONDS;
		
		String q = Suits.CLUBS + " " + Suits.DIAMONDS + " ";
		q += Suits.HEARTS;
		return q + " " + Suits.SPADES;
	}
}
