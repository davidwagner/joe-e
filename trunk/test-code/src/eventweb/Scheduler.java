package eventweb;

import java.util.Queue;

public class Scheduler {
	private Queue<Event> q;

	Scheduler(Queue<Event> q) {
		this.q = q;
	}
		
	int processEvents() {
		int eventsProcessed = 0;
		
		while(!q.isEmpty()) {
			Event[] newEvents = q.remove().process();
			for (Event i : newEvents) {
				q.add(i);
			}	
			++eventsProcessed;
		}
		
		return eventsProcessed;
	}
}
