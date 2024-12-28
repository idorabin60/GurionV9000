package bgu.spl.mics.application.objects;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private List<StampedCloudPoints> cloudPoints;
    public LiDarData(String filePath) {
        // NEED TO PRASE THE CONFIG FILE TO List<StampedCloudPoints>
    }
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    private static class  SingletonHolder(String filePath){
        private static final LiDarDataBase instance = new LiDarDataBase(filePath);
    }
    public static LiDarDataBase getInstance(String filePath) {
        return SingletonHolder(filePath).instance;
    }

    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }

    @Override
    public String toString() {
        return "LiDarData{" +
                "cloudPoints=" + cloudPoints +
                '}';
    }

}
