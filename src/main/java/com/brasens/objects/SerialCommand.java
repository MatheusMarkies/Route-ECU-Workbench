package com.brasens.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SerialCommand {

    private String command;
    private String answer;
    private final long timestamp;

    public SerialCommand(String command){
        this.command = command;
        this.timestamp = System.currentTimeMillis();
    }

    public SerialCommand(String command, String answer) {
        this.command = command;
        this.answer = answer;
        this.timestamp = System.currentTimeMillis();
    }

}
