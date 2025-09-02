// src/main/java/com/example/ioproject/repair/dto/RepairItemDto.java
package com.example.ioproject.repair.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RepairItemDto {
    private String name;
    private String note;
    private boolean done;

    public RepairItemDto() {}

    public RepairItemDto(String name, String note, boolean done) {
        this.name = name;
        this.note = note;
        this.done = done;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
}
