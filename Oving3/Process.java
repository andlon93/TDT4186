package Oving3;

import java.awt.*;

/**
 * This class contains data associated with processes,
 * and methods for manipulating this data as well as
 * methods for displaying a process in the GUI.
 *
 * You will probably want to add more methods to this class.
 */
public class Process implements Constants
{
	/** The ID of the next process to be created */
	private static long nextProcessId = 1;
	/** The font used by all processes */
	private static Font font = new Font("Arial", Font.PLAIN, 10);
	/** The ID of this process */
	private long processId;
	/** The color of this process */
	private Color color;
	/** The amount of memory needed by this process */
        private long memoryNeeded;
        /** The amount of cpu time still needed by this process */
        private long cpuTimeNeeded;
        /** The average time between the need for I/O operations for this process */
        private long avgIoInterval;
        /** The time left until the next time this process needs I/O */
        private long timeToNextIoOperation = 0;
        
        /** The time that this process has spent waiting in the memory queue */
        private long timeSpentWaitingForMemory = 0;
        /** The time that this process has spent waiting in the CPU queue */
        private long timeSpentInReadyQueue = 0;
        /** The time that this process has spent processing */
        private long timeSpentProcessing = 0;
        private long timeSpentInCpu = 0;
        /** The time that this process has spent waiting in the I/O queue */
        private long timeSpentIo = 0;
        private long timeSpentWaitingForIo = 0;
        /** The time that this process has spent performing I/O */
        private long timeSpentInIo = 0;
        /** The global time of the last event involving this process */
        private long timeOfLastEvent;
        
        /**
         * Creates a new process with given parameters. Other parameters are randomly
         * determined.
         * @param memorySize	The size of the memory unit.
         * @param creationTime	The global time when this process is created.
         */
        public Process(long memorySize, long creationTime) {
            // HogaBoga.Memory need varies from 100 kB to 25% of memory size
            memoryNeeded = 100 + (long)(Math.random()*(memorySize/4-100));
            // CPU time needed varies from 100 to 10000 milliseconds
            cpuTimeNeeded = 100 + (long)(Math.random()*10000);
            // Average interval between I/O requests varies from 1% to 25% of CPU time needed
            avgIoInterval = (1 + (long)(Math.random()*25))*cpuTimeNeeded/100;
            // The first and latest event involving this process is its creation
            timeOfLastEvent = creationTime;
            // Assign a process ID
            processId = nextProcessId++;
            // Assign a pseudo-random color used by the GUI
            int red = 64+(int)((processId*101)%128);
            int green = 64+(int)((processId*47)%128);
            int blue = 64+(int)((processId*53)%128);
            color = new Color(red, green, blue);
            
            timeToNextIoOperation = avgIoInterval;
            if (Simulator.info) System.out.print(this);
        }
        
        /**
         * Draws this process as a colored box with a process ID inside.
         * @param g	The graphics context.
         * @param x	The leftmost x-coordinate of the box.
         * @param y	The topmost y-coordinate of the box.
         * @param w	The width of the box.
         * @param h	The height of the box.
         */
        public void draw(Graphics g, int x, int y, int w, int h) {
            g.setColor(color);
            g.fillRect(x, y, w, h);
            g.setColor(Color.black);
            g.drawRect(x, y, w, h);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics(font);
            g.drawString(""+processId, x+w/2-fm.stringWidth(""+processId)/2, y+h/2+fm.getHeight()/2);
        }
        
        /**
         * This method is called when the process leaves the memory queue (and
         * enters the cpu queue).
         * @param clock The time when the process leaves the memory queue.
         */
        public void leftMemoryQueue(long clock) {
		  timeSpentWaitingForMemory += clock - timeOfLastEvent;
		  timeOfLastEvent = clock;
    }

    public void leftReadyQueue(long clock) {
        timeSpentInReadyQueue += clock - timeOfLastEvent;
        timeOfLastEvent = clock;
    }

    public void leftIoQueue(long clock) {
        timeSpentWaitingForIo += clock - timeOfLastEvent;
        timeOfLastEvent = clock;
    }

    public void spentinCpu(long clock) {
        timeSpentProcessing += clock - timeOfLastEvent;
        timeOfLastEvent = clock;
    }

    public void spentInIo(long clock) {
        timeSpentIo += clock - timeOfLastEvent;
        timeOfLastEvent = clock;
    }




    /**
	 * Returns the amount of memory needed by this process.
     * @return	The amount of memory needed by this process.
     */
	public long getMemoryNeeded() {
		return memoryNeeded;
	}

    /**
	 * Updates the statistics collected by the given Statistic object, adding
	 * data collected by this process. This method is called when the process
	 * leaves the system.
     * @param statistics	The HogaBoga.Statistics object to be updated.
     */
	public void updateStatistics(Statistics statistics) {
		statistics.totalTimeSpentWaitingForMemory += timeSpentWaitingForMemory;
        statistics.avgTimeSpentWaitingCpu += timeSpentInReadyQueue;
        statistics.avgTimeProcessing += timeSpentProcessing;
        statistics.avgTimeSpentWaitingIo += timeSpentWaitingForIo;
        statistics.avgTimeSpentIo += timeSpentIo;
        statistics.avgTimeSpentInSystem += getTimeSpentInSystem ();
        statistics.nofCompletedProcesses++;

	}

        
	
	// Add more methods as needed
	public long getCPUTime ()
	{
		return cpuTimeNeeded;
	}

        public long getAvgIoInterval() {
        return avgIoInterval;
    }
	
        


        
        
        
    public void setTimeToNextIoOperation(long timeToNextIoOperation) {
        this.timeToNextIoOperation = timeToNextIoOperation;
    }

    public void decreaseTimeToNextIoOperation(long time) {
        timeToNextIoOperation -= time;
    }

    
    
    
    public void addTimeSpentInIO(long timeSpentInIO){
        timeSpentInIo += timeSpentInIO;
    }
    
    public void addTimeSpentInCpu(long timeSpentInCpu){
        this.timeSpentInCpu += timeSpentInCpu;
    }

    void decreaseTimeNeededForCpu(long cpuTimeNeeded) {
        this.cpuTimeNeeded -= cpuTimeNeeded;
    }


    

    public long getCpuTimeNeeded() {
        return cpuTimeNeeded;
    }
    
    
    
    @Override
    public String toString(){
        return "pid:" + processId 
                + ", cpuTimeNeeded: " + cpuTimeNeeded
                + ", time spent in cpu: " + timeSpentInCpu 
                + ", timeToNextIo: " +timeToNextIoOperation 
                + ", time spent in io. " + timeSpentInIo;
        
    }


   

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Process other = (Process) obj;
        if (this.processId != other.processId) {
            return false;
        }
        return true;
    }



    private long cpuTimeSpent = 0;
    void setCpuTimeSpent(long cpuTimeSpent) {
        this.cpuTimeSpent = cpuTimeSpent;
    }

    public long getCpuTimeSpent() {
        return cpuTimeSpent;
    }

    public long getTimeSpentInSystem ()
    {
         return timeSpentWaitingForMemory + timeSpentInReadyQueue + timeSpentWaitingForIo + timeSpentInCpu + timeSpentInIo;
    }

    
	
    
    
}