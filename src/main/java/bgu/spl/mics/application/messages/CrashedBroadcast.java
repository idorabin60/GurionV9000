package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;

public class CrashedBroadcast implements Broadcast {
    private final String faultyServiceName;
    private final String errorMessage;


    public CrashedBroadcast(String faultyServiceName, String errorMessage) {
        this.faultyServiceName = faultyServiceName;
        this.errorMessage = errorMessage;
    }

    public String getFaultyServiceName() {
        return faultyServiceName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}


