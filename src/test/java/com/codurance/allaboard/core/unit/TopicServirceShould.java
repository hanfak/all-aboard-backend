package com.codurance.allaboard.core.unit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codurance.allaboard.core.model.topic.Subtopic;
import com.codurance.allaboard.core.model.topic.SubtopicService;
import com.codurance.allaboard.core.model.topic.Subtopics;
import com.codurance.allaboard.core.model.topic.Topic;
import com.codurance.allaboard.core.model.topic.TopicService;
import com.codurance.allaboard.core.model.topic.Topics;
import com.codurance.allaboard.web.views.SubtopicDetailView;
import com.codurance.allaboard.web.views.TopicWithSubtopicsView;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TopicServirceShould {

  private TopicService topicService;
  @Mock
  private Topics topics;
  @Mock
  private Subtopics subtopics;
  @Mock
  private SubtopicService subtopicService;
  private String name;
  private String description;
  private List<SubtopicDetailView> subtopicDetailViews;
  private TopicWithSubtopicsView topicWithSubtopicsView;


  @BeforeEach
  void setUp() {
    topicService = new TopicService(topics, subtopicService);
    name = "name";
    description = "description";
    subtopicDetailViews = List
        .of(new SubtopicDetailView("first subtopic"), new SubtopicDetailView("second subtopic"));
    topicWithSubtopicsView = new TopicWithSubtopicsView();
    topicWithSubtopicsView.setName(name);
    topicWithSubtopicsView.setDescription(description);
    topicWithSubtopicsView.setSubtopics(subtopicDetailViews);
  }

  @Test
  void call_the_repository_to_store_the_topic() {
    topicService.storeTopicWithSubtopics(topicWithSubtopicsView);

    verify(topics, atLeastOnce()).save(any());
  }

  @Disabled
  @Test
  void should_return_a_topic_with_subtopics() {
    Topic expectedTopic = new Topic();
    when(topics.save(any())).thenReturn(expectedTopic);

    Topic topic = topicService.storeTopicWithSubtopics(topicWithSubtopicsView);
    Subtopic firstSubtopic = topic.getSubtopics().get(0);
    Subtopic secondSubtopic = topic.getSubtopics().get(1);

    assertThat(topic.getName(), is(name));
    assertThat(topic.getDescription(), is(description));
    assertThat(firstSubtopic.getName(), is("first subtopic"));
    assertThat(secondSubtopic.getName(), is("second subtopic"));
  }

  @Test
  void call_subtopic_service_to_store_subtopics() {
    topicService.storeTopicWithSubtopics(topicWithSubtopicsView);
    verify(subtopicService, atLeastOnce()).saveSubtopics(any(),any());
  }
}
