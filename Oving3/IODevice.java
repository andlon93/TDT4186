package Oving3;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Christoffer <christofferbuvik@gmail.com>
 */
public class IODevice {
    private long ioOperationTime;
    private Statistics statistics;
    private Queue q;
    private long clock;
    private Process activeIO;
    private Gui gui;
    private EventQueue eventQueue;


    /**
     *
     * @param ioQueue
     * @param statistics
     * @param avgIoTime         The average length of an I/O operation.
     * @param gui
     * @param eventQueue
     */
    IODevice(Queue ioQueue, Statistics statistics, long avgIoTime, Gui gui, EventQueue eventQueue) {
        q = ioQueue;
        this.statistics = statistics;
        ioOperationTime = clock + 1 + (long)(2*Math.random()*avgIoTime);
        this.gui = gui;
        this.eventQueue = eventQueue;
    }

    public void updateClock(long clock){
        this.clock = clock;
    }

    public void insertProcessIO(Process p) {
        if (p == null) return;
        if (activeIO == null){
            activeIO = p;
            gui.setIoActive(p);
            eventQueue.insertEvent(new Event(Constants.END_IO, clock + ioOperationTime));
        }
        else {
            q.insert(p);
        }
        statistics.avgPlacedInIo++;
    }

    public Process removeProcess(){
        if (q.isEmpty()) return null;
        return (Process) q.removeNext();
    }

    public void setNextIO(Process nextIo){
        activeIO = nextIo;
        gui.setIoActive(nextIo);
        nextIo.addTimeSpentInIO(ioOperationTime);
        activeIO.leftIoQueue(clock);
        eventQueue.insertEvent(new Event(Constants.END_IO, clock + ioOperationTime));

    }

    Process extractActiveProcess() {
        Process processToReturn = activeIO;
        processToReturn.addTimeSpentInIO(ioOperationTime);
        
        //reset timeToNextIo
        processToReturn.setTimeToNextIoOperation(processToReturn.getAvgIoInterval());
        activeIO = null;
        gui.setIoActive(null);
        return processToReturn;
    }

    public long getIoOperationTime() {
        return ioOperationTime;
    }

    public void timePassed(long timePassed) {
        statistics.avgIoQueue += q.getQueueLength()*timePassed;
        if (q.getQueueLength() > statistics.largestIoQueue) {
            statistics.largestIoQueue = q.getQueueLength();
        }
    }










}
