package com.codurance.allaboard.core.acceptance;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.codurance.allaboard.core.actions.survey.SaveSurvey;
import com.codurance.allaboard.core.model.survey.Survey;
import com.codurance.allaboard.core.model.survey.Surveys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SaveSurveyFeature {

  @Mock
  private Surveys surveys;

  @InjectMocks
  private SaveSurvey saveSurvey;

  @Test
  void should_save_a_survey() {
    final String email = "user@codurance.com";
    final Survey survey = new Survey(email, "I prefer Udacity.");

    saveSurvey.save(survey);

    verify(surveys, atLeastOnce()).save(survey);
  }
}