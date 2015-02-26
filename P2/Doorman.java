/**
 * This class implements the doorman's part of the
 * Barbershop thread synchronization example.
 */
public class Doorman implements Runnable {
	/**
	 * Creates a new doorman.
	 * @param queue		The customer queue.
	 * @param gui		A reference to the GUI interface.
	 */
	CustomerQueue queue;
	Gui gui;
	boolean running = true;
	Thread doorman;

	public Doorman(CustomerQueue queue, Gui gui) {
		this.queue = queue;
		this.gui = gui;
		doorman = new Thread(this,"doorman");
	}

	/**
	 * Starts the doorman running as a separate thread.
	 */


	public void startThread() {
		doorman.start();
	}

	public void run(){

		while (running){
			Customer cu = new Customer();
			queue.addCustomer(cu);

			try {
				Thread.sleep((long)(Math.random()*(Globals.doormanSleep)));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Stops the doorman thread.
	 */
	public void stopThread() {
		running=false;
	}

	// Add more methods as needed
}
