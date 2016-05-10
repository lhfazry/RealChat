package com.fazrilabs.realchat.event;

import com.fazrilabs.realchat.model.Message;

/**
 * Created by blastocode on 5/9/16.
 */
public class MessageEvent {
    public Message message;


    public MessageEvent(Message message) {
        this.message = message;
    }
}
