package bgu.spl.mics.application.objects;


import java.util.List;

public class Components {
    private final List<Camera> cameras;
    private final List<LiDarWorkerTracker> lidarWorkers;
    private final LiDarDataBase lidarDataBase;
    private final GPSIMU gps;

    public Components(List<Camera> cameras, List<LiDarWorkerTracker> lidarWorkers, LiDarDataBase lidarDataBase, GPSIMU gps) {
        this.cameras = cameras;
        this.lidarWorkers = lidarWorkers;
        this.lidarDataBase = lidarDataBase;
        this.gps = gps;
    }

    public List<Camera> getCameras() {
        return cameras;
    }

    public List<LiDarWorkerTracker> getLidarWorkers() {
        return lidarWorkers;
    }

    public LiDarDataBase getLidarDataBase() {
        return lidarDataBase;
    }

    public GPSIMU getGps() {
        return gps;
    }
}

