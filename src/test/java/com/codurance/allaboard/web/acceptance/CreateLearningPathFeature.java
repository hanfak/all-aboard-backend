package com.codurance.allaboard.web.acceptance;

import com.codurance.allaboard.acceptance.utils.WebAcceptanceTestTemplate;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CreateLearningPathFeature extends WebAcceptanceTestTemplate {

  @LocalServerPort
  private int port;
  private JSONObject requestBody;
  private String name;
  private String description;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
    requestBody = new JSONObject();
    name = "Stub name";
    description = "Stub description";
  }

  @Test
  void save_a_learningpath_given_the_valid_request() {
    requestBody.put("name", name);
    requestBody.put("description", description);
    RequestSpecification httpRequest = httpRequestWithJSONContentType(requestBody);

    Response response = httpRequest.post(apiV1Endpoint("learningpath"));
    JSONObject responseBody = buildResponseBody(response);

    assertThat(response.statusCode(), is(201));
    assertThat(response.contentType(), is(ContentType.JSON.toString()));
    assertThat(responseBody.get("name"), is(name));
    assertThat(responseBody.get("description"), is(description));
  }

  @Test
  void error_on_invalid_request() {
    RequestSpecification httpRequest = httpRequestWithJSONContentType(requestBody);

    Response response = httpRequest.post(apiV1Endpoint("learningpath"));
    JSONObject responseBody = buildResponseBody(response);

    assertThat(response.statusCode(), is(400));
    assertThat(response.contentType(), is(ContentType.JSON.toString()));
    assertThat(responseBody.get("description"), is("Cannot be null or empty"));
    assertThat(responseBody.get("name"), is("Cannot be null or empty"));
  }

  @Test
  void reject_learningpath_with_a_long_description() {
    requestBody.put("name", name);
    requestBody.put("description", StringUtils.repeat("f", 1501));
    RequestSpecification request = httpRequestWithJSONContentType(requestBody);

    Response response = request.post(apiV1Endpoint("learningpath"));

    JSONObject responseBody = buildResponseBody(response);
    assertThat(response.getStatusCode(), is(400));
    assertThat(responseBody.get("description"), is("Cannot be bigger than 1500 characters"));
    assertThat(response.contentType(), is(ContentType.JSON.toString()));
  }

  @Test
  @Sql(scripts = "classpath:cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void create_full_learning_path() throws IOException {
    // given
    JSONObject requestBody = new JSONObject(expectedResponseBody("stub-full-learning-path-request-body.json"));

    // when
    RequestSpecification request = httpRequestWithJSONContentType(requestBody);
    Response response = request.post(apiV1Endpoint("fulllearningpath"));
    JSONObject responseBody = buildResponseBody(response);
    String expectedResponseBody = expectedResponseBody("stub-full-learning-path-response-body.json");

    // then make sure the response reflects created entity
    assertThat(response.getStatusCode(), is(201));
    assertThat(responseBody.toString(), is(expectedResponseBody));
  }


  @Test
  @Sql(scripts = "classpath:cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void  create_multilple_full_learning_paths_and_view_them_in_catalogue() throws IOException {
    // given
    JSONObject requestBody = new JSONObject(expectedResponseBody("stub-full-learning-path-request-body.json"));

    // when
    RequestSpecification request = httpRequestWithJSONContentType(requestBody);
    request.post(apiV1Endpoint("fulllearningpath")); // first insert
    request.post(apiV1Endpoint("fulllearningpath")); // second insert

    Response catalogueResponse = httpRequest().get(apiV1Endpoint("learningpath"));
    JSONObject catalogueResponseBody = buildResponseBody(catalogueResponse);
    JSONArray learningPaths = catalogueResponseBody.getJSONArray("learningPaths");

    // then
    assertThat(learningPaths.length(), is(2));
    assertThat(catalogueResponseBody.toString(), is(expectedResponseBody("stub-full-learning-paths-catalogue.json")));
  }

}
