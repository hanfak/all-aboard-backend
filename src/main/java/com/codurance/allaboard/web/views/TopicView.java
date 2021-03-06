package com.codurance.allaboard.web.views;

import com.codurance.allaboard.core.model.topic.Topic;

public class TopicView {

    private final Long id;
    private final String name;
    private final String description;

    private TopicView(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static TopicView from(Topic topic) {
        return new TopicView(topic.getId(), topic.getName(), topic.getDescription());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
