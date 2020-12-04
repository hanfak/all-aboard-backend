package com.codurance.allaboard.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.codurance.allaboard.config.security.interceptors.GoogleTokenInterceptor;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class GoogleTokenInterceptorShould {

  private final String email = "user@codurance.com";

  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private Object handler;

  @Mock
  private GoogleIdTokenVerifier googleIdTokenVerifier;

  @Mock
  private GoogleIdToken googleIdToken;

  @Mock
  private Payload payload;

  @Test
  void deny_request_without_authorization() throws GeneralSecurityException, IOException {
    String INVALID_TOKEN = "";

    request = mock(HttpServletRequest.class);
    given(request.getHeader("Authorization"))
        .willReturn(INVALID_TOKEN);

    given(googleIdTokenVerifier.verify(anyString()))
        .willThrow(GeneralSecurityException.class);

    GoogleTokenInterceptor interceptor = new TestableGoogleTokenAuthenticator();

    assertThat(interceptor.preHandle(request, response, handler), is(false));
  }

  @Test
  void accept_request_with_authorization() throws GeneralSecurityException, IOException {
    GoogleTokenInterceptor interceptor = new TestableGoogleTokenAuthenticator();

    request = new ValidTokenHttpServletRequest();

    given(googleIdTokenVerifier.verify(anyString()))
        .willReturn(googleIdToken);

    given(googleIdToken.getPayload())
        .willReturn(payload);

    given(payload.getEmail())
        .willReturn(email);

    assertThat(interceptor.preHandle(request, response, handler), is(true));
  }

  class TestableGoogleTokenAuthenticator extends GoogleTokenInterceptor {

    @Override
    protected GoogleIdTokenVerifier buildGoogleIdTokenVerifier() {
      return googleIdTokenVerifier;
    }
  }

  @Test
  void set_email_in_request_header() throws GeneralSecurityException, IOException {
    GoogleTokenInterceptor interceptor = new TestableGoogleTokenAuthenticator();

    request = new ValidTokenHttpServletRequest();

    given(googleIdTokenVerifier.verify(anyString()))
        .willReturn(googleIdToken);

    given(googleIdToken.getPayload())
        .willReturn(payload);

    given(payload.getEmail())
        .willReturn(email);

    assertThat(request.getAttribute("user_email"), is(nullValue()));

    interceptor.preHandle(request, response, handler);

    assertThat(request.getAttribute("user_email"), is(email));
  }

  class ValidTokenHttpServletRequest extends MockHttpServletRequest {

    @Override
    public String getHeader(String name) {
      return "some valid token";
    }
  }
}