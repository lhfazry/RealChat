package com.fazrilabs.realchat.event;

import com.fazrilabs.realchat.model.Topic;

/**
 * Created by blastocode on 5/9/16.
 */
public class TopicEvent {
    public Topic topic;

    public TopicEvent(Topic topic) {
        this.topic = topic;
    }
}
