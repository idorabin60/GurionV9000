package bgu.spl.mics.application.objects;

import java.util.concurrent.CountDownLatch;

//Singleton class to share a latch between all sevices
public class SystemServicesCountDownLatch {
    private static CountDownLatch countDownLatch;
    private volatile static SystemServicesCountDownLatch instance = null;
    private SystemServicesCountDownLatch(int numberOfServices) {
       SystemServicesCountDownLatch.countDownLatch = new CountDownLatch(numberOfServices);
    }
    public static void init(int numberOfServices) {
        if (instance == null) {
            synchronized (SystemServicesCountDownLatch.class) {
                if (instance == null) {
                    instance = new SystemServicesCountDownLatch(numberOfServices);
                }
            }
        }
    }
    public static SystemServicesCountDownLatch getInstance() {
        return instance;
    }
    public CountDownLatch getCountDownLatch() {return countDownLatch;}
}

