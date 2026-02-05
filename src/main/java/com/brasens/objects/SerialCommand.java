package com.brasens.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SerialCommand {

    private String command;
    private String answer;
    private final long timestamp;
    private long timeout;
    private Runnable callback;

    public SerialCommand(String command) {
        this(command, "OK", null, 100);
    }

    public SerialCommand(String command, long timeout) {
        this(command, "OK", null, timeout);
    }

    public SerialCommand(String command, String answer) {
        this(command, answer, null, 100);
    }

    public SerialCommand(String command, String answer, long timeout) {
        this(command, answer, null, timeout);
    }

    public SerialCommand(String command, String answer, Runnable callback, long timeout) {
        this.command = command;
        this.answer = answer;
        this.callback = callback;
        this.timeout = timeout;
        this.timestamp = System.currentTimeMillis();
    }

}