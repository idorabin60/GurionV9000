package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TerminatedBroadcast implements Broadcast {

    private String message;

    public TerminatedBroadcast() {
        this.message = "Shtting down";
    }
    public void printMessage() {
        System.out.println(this.message);
    }


}