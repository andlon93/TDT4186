/**Vi vil eksperimentere på round-robin ved å endre på de forskjellige variablene som vi tar inn via konsollvinduet.
 * Vi vil da se på hva som endres i resultatene ut ifra det vi endrer på av variabler.
 * Slik at vi kan se hva slag innvirkningskraft variablene har på systemet og round-robin.*/

package Oving3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The main class of the P3 exercise. This class is only partially complete.
 */
public class Simulator implements Constants
{
    boolean doDebug = false;
    public static boolean info = false;
    private boolean logEvents = false;
    
    /** The queue of events to come */
    private EventQueue eventQueue;
    /** Reference to the memory unit */
    private Memory memory;
    /** Reference to the GUI interface */
    private Gui gui;
    /** Reference to the statistics collector */
    private Statistics statistics;
    /** The global clock */
    private long clock;
    /** The length of the simulation */
    private long simulationLength;
    /** The average length between process arrivals */
    private long avgArrivalInterval;
    private Cpu cpu;
    private IODevice iODevice;
    private BufferedReader reader;
    private float totalMemoryQueueLength;
    // Add member variables as needed
    /**
     * Constructs a scheduling simulator with the given parameters.
     * @param memoryQueue			The memory queue to be used.
     * @param cpuQueue				The CPU queue to be used.
     * @param ioQueue				The I/O queue to be used.
     * @param memorySize			The size of the memory.
     * @param maxCpuTime			The maximum time quant used by the RR algorithm.
     * @param avgIoTime				The average length of an I/O operation.
     * @param simulationLength		The length of the simulation.
     * @param avgArrivalInterval	The average time between process arrivals.
     * @param gui					Reference to the GUI interface.
     */
    public Simulator(Queue memoryQueue, Queue cpuQueue, Queue ioQueue, long memorySize,
            long maxCpuTime, long avgIoTime, long simulationLength, long avgArrivalInterval, Gui gui) {
        this.simulationLength = simulationLength;
        this.avgArrivalInterval = avgArrivalInterval;
        this.gui = gui;
        statistics = new Statistics();
        eventQueue = new EventQueue();
        memory = new Memory(memoryQueue, memorySize, statistics);
        clock = 0;
        iODevice = new IODevice(ioQueue, statistics, avgIoTime, gui, eventQueue);
        cpu = new Cpu(cpuQueue, statistics, maxCpuTime, eventQueue, gui, clock);
        reader = new BufferedReader(new InputStreamReader(System.in));
        // Add code as needed
    }

    /**
     * Starts the simulation. Contains the main loop, processing events.
     * This method is called when the "Start simulation" button in the
     * GUI is clicked.
     */
    public void simulate() {
        // TODO: You may want to extend this method somewhat.

        System.out.print("Simulating...");
        // Genererate the first process arrival event
        eventQueue.insertEvent(new Event(NEW_PROCESS, 0));
        // HogaBoga.Process events until the simulation length is exceeded:
        while (clock < simulationLength && !eventQueue.isEmpty()) {
            // Find the next event
            Event event = eventQueue.getNextEvent();

            if (info) System.out.println("clock: " + clock + ", type: " + event + "\t\t\t\tpress something. ");
            if (doDebug) {
                long space = readLong(reader);
                if (space == 1) doDebug = false;
            }
            // Find out how much time that passed...
            long timeDifference = event.getTime()-clock;
            // ...and update the clock.
            clock = event.getTime();
            cpu.updateClock(clock);
            iODevice.updateClock(clock);
            // Let the memory unit and the GUI know that time has passed
            if (timeDifference > 0)
            {
                gui.timePassed(timeDifference);
                memory.timePassed(timeDifference);
                cpu.timePassed(timeDifference);
                iODevice.timePassed(timeDifference);
            }
            // Deal with the event
            if (clock < simulationLength) {
                    processEvent(event);
            }

            // Note that the processing of most events should lead to new
            // events being added to the event queue!

        }
        statistics.totalCpuWaitTime = clock - statistics.totalTimeProcessing;
        statistics.fractionTimeProcessing = (statistics.totalTimeProcessing/clock)*100;
        statistics.fractionTimeWaiting = (statistics.totalCpuWaitTime/clock)*100;
        System.out.println("..done.");
        // End the simulation by printing out the required statistics
        statistics.printReport(simulationLength);
    }

    /**
     * Processes an event by inspecting its type and delegating
     * the work to the appropriate method.
     * @param event	The event to be processed.
     */
    private void processEvent(Event event) {
        switch (event.getType()) {
            case NEW_PROCESS:
                createProcess();
                break;
            case SWITCH_PROCESS:
                switchProcess();
                break;
            case END_PROCESS:
                endProcess();
                break;
            case IO_REQUEST:
                processIoRequest();
                break;
            case END_IO:
                    endIoOperation();
                break;
        }
    }

    /**
     * Simulates a process arrival/creation.
     */
    private void createProcess() {
        // Create a new process
        Process newProcess = new Process(memory.getMemorySize(), clock);
        flushMemoryQueue();
        memory.insertProcess(newProcess);
        // Add an event for the next process arrival
        long nextArrivalTime = clock + 1 + (long)(2*Math.random()*avgArrivalInterval);
        Event event = new Event(NEW_PROCESS, nextArrivalTime);
        if (info) System.out.println("\n\n\new process to eventQueue at: " + nextArrivalTime + "\t" + event+ "\n\n");
        eventQueue.insertEvent(event);
        // Update statistics
        statistics.nofCreatedProcesses++;
    }

    /**
     * Transfers processes from the memory queue to the ready queue as long as there is enough
     * memory for the processes.
     */
    private void flushMemoryQueue() {
        Process p = memory.checkMemory(clock);
        // As long as there is enough memory, processes are moved from the memory queue to the cpu queue
        while(p != null) {
            if (info) System.out.println("flush memory");
            // TODO: Add this process to the CPU queue!
            cpu.insertProcess(p);



            // Also add new events to the event queue if needed

            // Since we haven't implemented the CPU and I/O device yet,
            // we let the process leave the system immediately, for now
            // Try to use the freed memory:
            flushMemoryQueue();

            // Check for more free memory
            p = memory.checkMemory(clock);
        }
    }

    /**
     * Simulates a process switch.
     */
    private void switchProcess() {
        Process oldProcess = cpu.extractActiveCpuProcess();
        if (logEvents) System.out.println("\nswitch process" + oldProcess);
        if (oldProcess != null){
            cpu.insertProcess(oldProcess);
            oldProcess.spentinCpu(clock);
        }
        else
        {
            cpu.runCpu();
        }


    }

    /**
     * Ends the active process, and deallocates any resources allocated to it.
     */
    private void endProcess() {
        Process processToIO = cpu.extractActiveCpuProcess();
        if (logEvents) System.out.println("\nEnd HogaBoga.Process: \t" + processToIO);
        if (processToIO != null) {
            processToIO.spentinCpu (clock);
            eventQueue.insertEvent(new Event(SWITCH_PROCESS, clock));
            memory.processCompleted(processToIO);
            processToIO.updateStatistics(statistics);
            if (logEvents) System.out.println("\n\n\n\n\nFINISH   " + processToIO);
        }

    }

    /**
     * Processes an event signifying that the active process needs to
     * perform an I/O operation.
     */
    private void processIoRequest() {
        Process processToIO = cpu.extractActiveCpuProcess();
        if (logEvents) System.out.println("\nIO REQUEST: \t" + processToIO);
        iODevice.insertProcessIO(processToIO);
        cpu.runCpu();

    }

    /**
     * Processes an event signifying that the process currently doing I/O
     * is done with its I/O operation.
     */
    private void endIoOperation() {
        Process p = iODevice.extractActiveProcess();
        p.spentInIo (clock);
        statistics.numberOfProcessedIoOperations++;
        if (logEvents) System.out.println("\nEnd IO: \t" + p);
        cpu.insertProcess(p);
        //let io handle next process in queue if there is any.
        Process ioNext = iODevice.removeProcess();
        if (ioNext != null){
            iODevice.setNextIO(ioNext);
        }
    }

    /**
     * Reads a number from the an input reader.
     * @param reader	The input reader from which to read a number.
     * @return			The number that was inputted.
     */
    public static long readLong(BufferedReader reader) {
        try {
            return Long.parseLong(reader.readLine());
        } catch (IOException ioe) {
            return 100;
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * The startup method. Reads relevant parameters from the standard input,
     * and starts up the GUI. The GUI will then start the simulation when
     * the user clicks the "Start simulation" button.
     * @param args	Parameters from the command line, they are ignored.
     */
    public static void main(String args[]) {
        boolean autoGeneratedValues = true;

   		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
          System.out.println("Please input system parameters: ");

        long memorySize;
          System.out.print("HogaBoga.Memory size (KB): ");
        if (autoGeneratedValues){memorySize = 2048;}
        else {
            memorySize = readLong(reader);
            while (memorySize < 400) {
                System.out.println("HogaBoga.Memory size must be at least 400 KB. Specify memory size (KB): ");
                memorySize = readLong(reader);
            }
        }
        long maxCpuTime;
        if (autoGeneratedValues){maxCpuTime = 500;}
        else {
            System.out.print("Maximum uninterrupted cpu time for a process (ms): ");
            maxCpuTime = readLong(reader);
        }

        long avgIoTime;
        if (autoGeneratedValues){avgIoTime = 225;}
        else {
            System.out.print("Average I/O operation time (ms): ");
            avgIoTime = readLong(reader);
        }

        long simulationLength;
        if (autoGeneratedValues){simulationLength = 100000;}
        else {
            System.out.print("Simulation length (ms): ");
            simulationLength = readLong(reader);
            while (simulationLength < 1) {
                System.out.println("Simulation length must be at least 1 ms. Specify simulation length (ms): ");
                simulationLength = readLong(reader);
            }
        }

        long avgArrivalInterval;
        if (autoGeneratedValues){avgArrivalInterval = 5000;}
        else {
            System.out.print("Average time between process arrivals (ms): ");
            avgArrivalInterval = readLong(reader);
        }



        System.out.println("max cpuTime: " + maxCpuTime);
        System.out.println("Time spent in io: " + avgIoTime);
        SimulationGui gui = new SimulationGui(memorySize, maxCpuTime, avgIoTime, simulationLength, avgArrivalInterval);
    }
}
