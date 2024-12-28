package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
        // Fields
        private int systemRuntime;       // The total runtime of the system, measured in ticks
        private int numDetectedObjects; // The cumulative count of objects detected by cameras
        private int numTrackedObjects;  // The cumulative count of objects tracked by LiDAR workers
        private int numLandmarks;       // The total number of unique landmarks identified

        // Constructor
        public StatisticalFolder() {
            this.systemRuntime = 0;
            this.numDetectedObjects = 0;
            this.numTrackedObjects = 0;
            this.numLandmarks = 0;
        }

        // Methods to update metrics

        /**
         * Updates the system runtime.
         * @param ticks The number of ticks to add to the system runtime.
         */
        public void updateSystemRuntime(int ticks) {
            systemRuntime += ticks;
        }

        /**
         * Increments the number of detected objects.
         * @param count The number of detected objects to add.
         */
        public void incrementDetectedObjects(int count) {
            numDetectedObjects += count;
        }

        /**
         * Increments the number of tracked objects.
         * @param count The number of tracked objects to add.
         */
        public void incrementTrackedObjects(int count) {
            numTrackedObjects += count;
        }

        /**
         * Updates the number of landmarks if new ones are added.
         * @param count The number of new landmarks to add.
         */
        public void incrementLandmarks(int count) {
            numLandmarks += count;
        }

        // Getters

        public int getSystemRuntime() {
            return systemRuntime;
        }

        public int getNumDetectedObjects() {
            return numDetectedObjects;
        }

        public int getNumTrackedObjects() {
            return numTrackedObjects;
        }

        public int getNumLandmarks() {
            return numLandmarks;
        }

        @Override
        public String toString() {
            return "StatisticalFolder{" +
                    "systemRuntime=" + systemRuntime +
                    ", numDetectedObjects=" + numDetectedObjects +
                    ", numTrackedObjects=" + numTrackedObjects +
                    ", numLandmarks=" + numLandmarks +
                    '}';
        }
}
