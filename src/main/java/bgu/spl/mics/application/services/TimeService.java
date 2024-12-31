package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;

import java.util.Objects;

/**
 * TimeService acts as the global timer for the system.
 * It broadcasts TickBroadcast messages at regular intervals and sends TerminateBroadcast at the end.
 */
public class TimeService extends MicroService {
    private final int tickInterval; // Time interval (in milliseconds) between ticks
    private int duration; // Total duration of the simulation in ticks
    private int currentTick; // Keeps track of the current tick

    /**
     * Constructor for TimeService.
     *
     * @param tickInterval The duration of each tick in milliseconds.
     * @param duration     The total number of ticks before the service terminates.
     */
    public TimeService(int tickInterval, int duration) {
        super("TimeService");
        this.tickInterval = tickInterval;
        this.duration = duration;
        this.currentTick = 0;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        sendBroadcast(new TickBroadcast(0));
        this.currentTick++;

        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            if (currentTick > duration) {
                sendBroadcast(new TerminatedBroadcast("TimeService"));
            } else {
                try {
                    Thread.sleep(tickInterval * 1000);
                    currentTick++;
                    sendBroadcast(new TickBroadcast(currentTick * (tickInterval * 1000)));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        });
        subscribeBroadcast(TerminatedBroadcast.class, terminatedBroadcast -> {
            if (Objects.equals(terminatedBroadcast.getSender(), "FusionSlam")){
                sendBroadcast(new TerminatedBroadcast("TimeService"));
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, terminatedBroadcast -> {
            terminate();
        });
    }
}
