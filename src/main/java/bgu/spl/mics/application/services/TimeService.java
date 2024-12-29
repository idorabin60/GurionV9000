//package bgu.spl.mics.application.services;
//
//import bgu.spl.mics.MicroService;
//import bgu.spl.mics.application.messages.TerminatedBroadcast;
//import bgu.spl.mics.application.messages.TickBroadcast;
//import bgu.spl.mics.application.messages.TerminatedBroadcast;
//
///**
// * TimeService acts as the global timer for the system.
// * It broadcasts TickBroadcast messages at regular intervals and sends TerminateBroadcast at the end.
// */
//public class TimeService extends MicroService {
//    private final int tickInterval; // Time interval (in milliseconds) between ticks
//    private final int duration; // Total duration of the simulation in ticks
//    private int currentTick; // Keeps track of the current tick
//
//    /**
//     * Constructor for TimeService.
//     *
//     * @param tickInterval The duration of each tick in milliseconds.
//     * @param duration     The total number of ticks before the service terminates.
//     */
//    public TimeService(int tickInterval, int duration) {
//        super("TimeService");
//        this.tickInterval = tickInterval;
//        this.duration = duration;
//        this.currentTick = 0;
//    }
//
//    /**
//     * Initializes the TimeService.
//     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
//     */
//    @Override
//    protected void initialize() {
//        System.out.println(getName() + " started");
//
//        try {
//            // Main tick loop
//            while (currentTick < duration) {
//                currentTick++;
//
//                // Broadcast the current tick
//                sendBroadcast(new TickBroadcast(currentTick));
//                System.out.println(getName() + " sent TickBroadcast: Tick " + currentTick);
//
//                // Sleep for the next tick
//                Thread.sleep(tickInterval);
//            }
//
//            // Send TerminateBroadcast after all ticks
//            sendBroadcast(new TerminatedBroadcast());
//            terminate();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            System.out.println(getName() + " interrupted and terminating");
//        }
//    }
//}
