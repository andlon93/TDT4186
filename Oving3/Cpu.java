package Oving3;

public class Cpu
{
    private Queue queue;
    private EventQueue eventQueue;
    private Statistics statistics;
    private long maxCpuTime;
    private Process activeCpuProcess;
    private Gui gui;
    private long clock;


    public Cpu (Queue cpuQueue, Statistics statistics, long maxCpuTime, EventQueue event, Gui gui, long clock)
    {
        this.queue = cpuQueue;
        this.statistics = statistics;
        this.maxCpuTime = maxCpuTime;
        this.eventQueue = event;
        this.gui = gui;
        this.clock = clock;
    }


    public void updateClock (long clock)
    {
        this.clock = clock;
    }




    public void insertProcess (Process p){
        if (p == null) throw new UnsupportedOperationException("insert to cpu");
        else
        {
            queue.insert(p);
        }
        statistics.avgPlacedInCpu++;
        if (activeCpuProcess == null)
        {
            runCpu();
        }
    }

    void runCpu(){
        if (activeCpuProcess == null && !queue.isEmpty()) activeCpuProcess = pop();
        if (activeCpuProcess != null) process(activeCpuProcess);
    }




    private void process(Process active){
        gui.setCpuActive(active);
        if (active.getCpuTimeNeeded() <= active.getAvgIoInterval()){
            active.setCpuTimeSpent(active.getCpuTimeNeeded());
            eventQueue.insertEvent(new Event(Constants.END_PROCESS, clock + active.getCpuTimeNeeded()));
        }
        else if (active.getAvgIoInterval() <= maxCpuTime){
            active.setCpuTimeSpent(active.getAvgIoInterval());
            eventQueue.insertEvent(new Event(Constants.IO_REQUEST, clock + active.getAvgIoInterval()));
        }
        else{
            active.setCpuTimeSpent(maxCpuTime);
            eventQueue.insertEvent(new Event(Constants.SWITCH_PROCESS, clock + maxCpuTime));
        }

    }







    public Process extractActiveCpuProcess(){
        Process retProcess = activeCpuProcess;
        if (retProcess != null)
        {
            if (!queue.isEmpty() && retProcess.getCpuTimeSpent() == maxCpuTime)statistics.forcedProcessSwitches++;
            statistics.totalTimeProcessing += retProcess.getCpuTimeSpent();
            retProcess.addTimeSpentInCpu(retProcess.getCpuTimeSpent());
            retProcess.decreaseTimeNeededForCpu(retProcess.getCpuTimeSpent());
            retProcess.decreaseTimeToNextIoOperation(retProcess.getCpuTimeSpent());
        }
        activeCpuProcess = null;
        gui.setCpuActive(null);
        return retProcess;
    }



     private Process pop(){
        if (queue.isEmpty()) return null;
         Process p = (Process) queue.removeNext();
         p.leftReadyQueue(clock);
        return p;
    }

    public void timePassed(long timePassed) {
        statistics.avgCpuQueue += queue.getQueueLength()*timePassed;
        if (queue.getQueueLength() > statistics.largestCpuQueue) {
            statistics.largestCpuQueue = queue.getQueueLength();
        }
    }





}
