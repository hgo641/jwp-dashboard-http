package org.apache.coyote.http11.message.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private static final String HEADER_VALUE_DELIMITER = ": ";
    private static final String VALUE_DELIMITER = "; ";
    private static final String HEADER_END_CHARACTER = " ";

    private final Map<String, String> headers;

    public ResponseHeaders() {
        this.headers = new HashMap<>();
    }

    public void add(final String key, final String value) {
        if (headers.putIfAbsent(key, value) != null) {
            final String originalValue = headers.get(key);
            headers.replace(key, originalValue + VALUE_DELIMITER + value);
        }
    }

    public void addAll(final Map<String, String> headers) {
        headers.keySet().forEach(key -> add(key, headers.get(key)));
    }

    public List<String> getHeaderLines() {
        return headers.keySet().stream()
                .map(key -> key + HEADER_VALUE_DELIMITER + headers.get(key) + HEADER_END_CHARACTER)
                .collect(Collectors.toList());
    }
}
