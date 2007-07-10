package test.verifier;
import org.joe_e.*;

public class LocalsPowerless1 {
	static void parka() {
		final Token guybrush = new Token();
		
		class Threepwood {
			public Object monkey() {
				return guybrush;
			}
		}
		
		class Elaine implements Powerless {
			public int island() {
				return 3;
			}
		}
		
		class LeChuck implements Immutable {
			{
				new Threepwood().monkey();
			}
		}
		
		class LeChuck2 {
			{
				new Threepwood().monkey();
			}
		}
		
		class Revenge implements Immutable {
			{
				new LeChuck();
			}
		}
		
		class ExtendsRevenge extends Revenge implements Powerless {
			
		}
		
		class Revenge2 implements Immutable {
			{
				new LeChuck2();
			}
		}
		
		class SonOf implements Powerless {
			{
				new Revenge();
			}
		}
		
		class SonOf2 implements Powerless {
			{
				new Revenge2();
				new SonOf();
			}
		}
		
		new Elaine().island();
	}
}
