
/**
 * This class implements the barber's part of the
 * Barbershop thread synchronization example.
 */
public class Barber implements Runnable {
	/**
	 * Creates a new barber.
	 * @param queue		The customer queue.
	 * @param gui		The GUI.
	 * @param pos		The position of this barber's chair
	 */
	CustomerQueue queue;
	Gui gui;
	int pos;
	boolean running = true;
	Customer customer;
	Thread barber;

	public Barber(CustomerQueue queue, Gui gui, int pos) {
		this.queue = queue;
		this.gui = gui;
		this.pos = pos;
		this.barber = new Thread(this,"barber");
	}

	/**
	 * Starts the barber running as a separate thread.
	 */
	public void startThread(){
		barber.start();

	}

	public void stopThread() {
		running=false;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (running){
			if (queue.customerExists()) {
				customer = queue.getCustomer();
				if (customer != null) {
					gui.fillBarberChair(pos, customer);
					try {
						Thread.sleep((long) (Math.random() * (Globals.barberWork)));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					gui.emptyBarberChair(pos);
					gui.barberIsSleeping(pos);
					try {
						Thread.sleep((long) (Math.random() * (Globals.barberSleep)));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					gui.barberIsAwake(pos);
				}
			}
			else {
				try {
					Thread.sleep((long) (Math.random() * 5000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	// Add more methods as needed


}

