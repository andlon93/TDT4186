import java.util.ArrayList;


/**
 * This class implements a queue of customers as a circular buffer.
 */
public class CustomerQueue {
	/**
	 * Creates a new customer queue.
	 * @param queueLength	The maximum length of the queue.
	 * @param gui			A reference to the GUI interface.
	 */



	ArrayList<Customer> queue;
	public int nextSeat = 0; //first place where someone sits
	public int lastSeat = 0; //last place where someone sits
	int queueLength;
	Gui gui;


	public CustomerQueue(int queueLength, Gui gui) {
		this.queue = new ArrayList<Customer>();
		this.queueLength = queueLength;
		this.gui = gui;
	}

	public int getNextSeatPlace(){
		int tempSeat = nextSeat;
		boolean done = false;
		while(!done){
			if (tempSeat>=18){
				tempSeat=tempSeat-18;
			}
			else done=true;
		}
		//gui.println("NextSeat: "+nextSeat+", tempSeat: "+tempSeat);
		return tempSeat;
	}

	public int getLastSeatPlace(){
		int tempSeat = lastSeat;
		boolean done = false;
		while(!done){
			if (tempSeat>=18){
				tempSeat=tempSeat-18;
			}
			else done=true;
		}
		//gui.println("LastSeat: "+lastSeat+", tempSeat: "+tempSeat);
		return tempSeat;
	}

	public synchronized Customer getCustomer(){
		if (lastSeat>nextSeat){
			Customer ret = queue.get(nextSeat);
			int tempSeat = getNextSeatPlace();
			if(tempSeat>=18)tempSeat=tempSeat-18;
			gui.emptyLoungeChair(tempSeat);
			nextSeat++;
			gui.println("Customer removed from seat: "+getNextSeatPlace());
			return ret;
		}
		return null;

	}

	public synchronized void addCustomer(Customer c) {
		if (queue.size() - nextSeat < queueLength) {
			queue.add(c);
			gui.println("Customer added placed in "+getLastSeatPlace());
			int tempSeat = getLastSeatPlace();
			boolean done = false;
			while(!done){
				if (tempSeat>=18)tempSeat=tempSeat-18;
				else done= true;
			}
			gui.fillLoungeChair(tempSeat,c);
			lastSeat++;
			//notifyAll();
		}
	}

	public synchronized boolean customerExists(){
		if(queue.size()>nextSeat)return true;
		/*else {
			synchronized (queue) {
				try {
					queue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}*/

		return false;
	}

}
