package Oving3;

/**
 * This class contains a lot of public variables that can be updated
 * by other classes during a simulation, to collect information about
 * the run.
 */
public class Statistics
{
    //TODO gjøre ferdig statistikk tullet
	/** The number of processes that have exited the system */
	public float nofCompletedProcesses = 0;
	/** The number of processes that have entered the system */
	public long nofCreatedProcesses = 0;
	/** The total time that all completed processes have spent waiting for memory */
	public float totalTimeSpentWaitingForMemory = 0;
	/** The time-weighted length of the memory queue, divide this number by the total time to get average queue length */
	public long memoryQueueLengthTime = 0;
	/** The largest memory queue length that has occured */
	public long memoryQueueLargestLength = 0;
    /**Total CPU time spent processing*/
    public double totalTimeProcessing = 0;
    /**Fraction of totalTimeProcessing*/
    public double fractionTimeProcessing = 0;
    /**Total CPU wait time*/
    public double totalCpuWaitTime = 0;
    /**Fraction of CPU time spent waiting*/
    public double fractionTimeWaiting = 0;
    /**Largest occuring Cpu queue*/
    public long largestCpuQueue = 0;
    /**Average Cpu queue length*/
    public long avgCpuQueue = 0;
    /**Largest I/O queue*/
    public long largestIoQueue = 0;
    /**Average I/O queue*/
    public long avgIoQueue = 0;
    /**Average times placed in cpu queue*/
    public long avgPlacedInCpu = 0;
    /**Average times placed in I/O queue*/
    public long avgPlacedInIo = 0;
    /**Average time spent in system per process*/
    public long avgTimeSpentInSystem = 0;
    /**Average time waiting for memory*/
    public  long avgTimeSpentWaitingMemory = 0;
    /**Average time waiting for CPU*/
    public  long avgTimeSpentWaitingCpu = 0;
    /**Average time spent processing per process*/
    public long avgTimeProcessing = 0;
    /**Average time waiting for I/O*/
    public  long avgTimeSpentWaitingIo = 0;
    /**Average time spent in I/O per process*/
    public long avgTimeSpentIo = 0;
    /**Number of forces process switches*/
    public long forcedProcessSwitches = 0;
    /**Number of Processed I/O operations*/
    public long numberOfProcessedIoOperations = 0;


	/**
	 * Prints out a report summarizing all collected data about the simulation.
	 * @param simulationLength	The number of milliseconds that the simulation covered.
	 */
	public void printReport(long simulationLength) {
		System.out.println();
		System.out.println("Simulation statistics:");
		System.out.println();
		System.out.println("Number of completed processes:                                "+(long)nofCompletedProcesses);
		System.out.println("Number of created processes:                                  "+nofCreatedProcesses);
        System.out.println("Number of (forced) process switches:                          "+forcedProcessSwitches);
        System.out.println("Number of processed I/O operations:                           " + numberOfProcessedIoOperations);
        System.out.println("Average throughput (processes per second):                    "+(float)nofCreatedProcesses/simulationLength*1000);
		System.out.println();
		if(nofCompletedProcesses > 0) {
            System.out.println("Total CPU time spent processing:                              "+(long)totalTimeProcessing);
            System.out.println("Fraction of CPU time spent processing:                        "+fractionTimeProcessing+"%");
            System.out.println("Total CPU time spent waiting:                                 "+(long)totalCpuWaitTime);
            System.out.println("Fraction of CPU time spent waiting:                           "+fractionTimeWaiting+"%");
            System.out.println();
            System.out.println("Largest occuring memory queue length:                         "+memoryQueueLargestLength);
            System.out.println("Average memory queue length:                                  "+(float)memoryQueueLengthTime/simulationLength);
            System.out.println("Largest occuring cpu queue length:                            "+largestCpuQueue);
            System.out.println("Average cpu queue length:                                     "+(float)avgCpuQueue/simulationLength);
            System.out.println("Largest occuring I/O queue length:                            "+largestIoQueue);
            System.out.println("Average I/O queue length:                                     "+(float)avgIoQueue/simulationLength);
            System.out.println("Average # of times a process has been placed in memory queue: "+1);
            System.out.println("Average # of times a process has been placed in cpu queue:    "+(float)avgPlacedInCpu/nofCreatedProcesses);
            System.out.println("Average # of times a process has been placed in I/O queue:    "+(float)avgPlacedInIo/nofCreatedProcesses);
            System.out.println();
            System.out.println("Average time spent in system per process:                     "+avgTimeSpentInSystem/nofCompletedProcesses + "ms");
            System.out.println("Average time spent waiting for memory per process:            "+
                    totalTimeSpentWaitingForMemory/nofCompletedProcesses+" ms");
            System.out.println("Average time spent waiting for cpu:                           "+avgTimeSpentWaitingCpu/nofCompletedProcesses + "ms");
            System.out.println("Average time spent processing per process:                    "+avgTimeProcessing/nofCompletedProcesses + "ms");
            System.out.println("Average time spent waiting for I/O:                           "+avgTimeSpentWaitingIo/nofCompletedProcesses + "ms");
            System.out.println("Average time spent in I/O per process:                        "+avgTimeSpentIo/nofCompletedProcesses + "ms");
		}
	}
}
