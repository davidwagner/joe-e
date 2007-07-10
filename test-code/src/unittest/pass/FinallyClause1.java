package unittest.pass;

public class FinallyClause1 {
	{
		RuntimeException re = null;
		try {
			new String();
		} catch (RuntimeException rex) {
			re = rex;
		}
		
		if (re != null) throw re;
	}
}
