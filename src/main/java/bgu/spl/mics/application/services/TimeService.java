package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    private int currentNumberOfTicks;
    private int duration;
    private final int TickTime; // Interval in milliseconds


    /**
     * Constructor for TimeService.
     *
     * @param TickTime The duration of each tick in milliseconds.
     * @param Duration The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("Change_This_Name");
        this.TickTime = TickTime;
        this.Duration = Duration;

    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        System.out.println("TimeService started"); // message for debugging might remove it lat
        try {
            while (currentNumberOfTicks < duration) {
                currentNumberOfTicks++;
                // Send TickBroadcast
                sendBroadcast(new TickBroadcast(currentNumberOfTicks));
                System.out.println(getName() + " sent TickBroadcast: Tick " + currentNumberOfTicks);

                // Sleep for the next interval
                Thread.sleep(TickTime);
            }
            // Send TerminateBroadcast after the last tick
            sendBroadcast(new TerminateBroadcast());
            System.out.println(getName() + " sent TerminateBroadcast");
            terminate();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(getName() + " interrupted and terminating");
        }
    }


}

//NEEDS TO REVISIT DAFNA PLEASE CHECK