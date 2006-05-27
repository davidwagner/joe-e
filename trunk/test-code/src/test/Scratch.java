package test;

import org.joe_e.*;

public class Scratch {
	public static void main(String[] args) {
		PowerlessArray<StrawMan> foof = new PowerlessArray<StrawMan>(
				new StrawMan[]{
					new StrawMan("hello"), new StrawMan("good-bye"), new StrawMan("you're still here?")
				});
		
		for (StrawMan s : foof) {
			System.out.println(s);
		}	
	}
}
