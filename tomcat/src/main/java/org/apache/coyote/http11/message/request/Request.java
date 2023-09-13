package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.message.HttpMethod;

public class Request {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestForms requestForms;

    public Request(final RequestLine requestLine, final RequestHeaders requestHeaders,
                   final RequestForms requestForms) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestForms = requestForms;
    }

    public static Request from(final BufferedReader br) throws IOException {
        final RequestLine requestLine = RequestLine.from(br.readLine());
        final RequestHeaders requestHeaders = RequestHeaders.from(br);
        final RequestForms requestForms = createRequestBody(br, requestHeaders);
        return new Request(requestLine, requestHeaders, requestForms);
    }

    private static RequestForms createRequestBody(final BufferedReader br, final RequestHeaders requestHeaders)
            throws IOException {
        if (!requestHeaders.hasContentType()) {
            return new RequestForms(null);
        }
        final int contentLength = Integer.parseInt((String) requestHeaders.get(CONTENT_LENGTH));
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        final String requestBody = new String(buffer);
        return RequestForms.from(requestBody);
    }

    public boolean noSession() {
        final Optional<String> sessionId = requestHeaders.getCookieValue(JSESSIONID);
        return sessionId.map(s -> SessionManager.findSession(s).isEmpty()).orElse(true);
    }

    public Optional<Object> getSessionValue(final String key) {
        final Optional<String> sessionId = requestHeaders.getCookieValue(JSESSIONID);
        if (sessionId.isEmpty()) {
            return Optional.empty();
        }
        final Optional<Session> session = SessionManager.findSession(sessionId.get());
        return session.map(value -> value.getAttribute(key));
    }

    public boolean isMatchMethod(final HttpMethod httpMethod) {
        return requestLine.getHttpMethod().equals(httpMethod);
    }

    public RequestURI getRequestURI() {
        return requestLine.getRequestURI();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestForms getRequestForms() {
        return requestForms;
    }
}
