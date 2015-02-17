package com.gamefactory.utils.events;

import java.util.EventObject;

/**
 *
 * @author scalpa
 */
public class Event extends EventObject {

    private final String event;
    private final Object message;
    
    
    public Event(Object source, String event) {
        super(source);
        this.event = event;
        this.message = null;
    }
    
    public Event(Object source, String event, Object message) {
        super(source);
        this.event = event;
        this.message = message;
    }
    
    
    public String getEvent() {
        return this.event;
    }
    
    public Object getMessage() {
        return this.message;
    }
    
    public boolean hasMessage() {
        return this.message != null;
    }
    
    public boolean isExpected(String expectedEvent) {
        return event.equals(expectedEvent);
    }
}