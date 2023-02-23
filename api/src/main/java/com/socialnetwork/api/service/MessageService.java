package com.socialnetwork.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.socialnetwork.api.v1.domain.MessageDto;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.log.Fields;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class MessageService {

    private final WebClient client;

    private final OkHttpClient httpClient;

    @Qualifier("tracer")
    private JaegerTracer tracer;

    private final String BASE_URL = "http://localhost:8081";

    private String MESSAGES_URL = "localhost";

    private int MESSAGES_API_PORT = 8081;

    ObjectMapper mapper = new ObjectMapper();


    public MessageService(@Qualifier("tracer") JaegerTracer tracer) {
        this.tracer = tracer;
        this.client = WebClient.builder()
            .baseUrl(BASE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultUriVariables(Collections.singletonMap("url", BASE_URL))
            .build();
        this.httpClient = new OkHttpClient();
    }

    @SneakyThrows
    public List<String> getListOfRooms(int profileId) {
        Span span = this.tracer.buildSpan("app.api.service.get-list-of-rooms").start();
        try (Scope scope = tracer.scopeManager().activate(span, true)) {
            HttpUrl url = new HttpUrl.Builder().scheme("http").host(MESSAGES_URL).port(MESSAGES_API_PORT)
                .addPathSegment("api")
                .addPathSegment("profile")
                .addPathSegment(String.valueOf(profileId))
                .addPathSegment("rooms")
                .build();
            var response = makeRequest(url);
            Map<String, List<String>> prop = mapper.readValue(response.body().string(), Map.class);
            return prop.get("rooms");
        } finally {
            span.finish();
        }
    }

    @SneakyThrows
    public List<MessageDto> getMessages(int profileId, int roomId) {
        Span span = this.tracer.buildSpan("app.api.service.get-messages").start();

        try (Scope scope = tracer.scopeManager().activate(span, true)) {
            HttpUrl url = new HttpUrl.Builder().scheme("http").host(MESSAGES_URL).port(MESSAGES_API_PORT)
                .addPathSegment("api")
                .addPathSegment("profile")
                .addPathSegment(String.valueOf(profileId))
                .addPathSegment("rooms")
                .addPathSegment(String.valueOf(roomId))
                .build();
            var response = makeRequest(url);

            Map<String, List<MessageDto>> prop = mapper.readValue(response.body().string(), Map.class);
            return prop.get("messages");
        } finally {
            span.finish();
        }

    }

    private Response makeRequest(HttpUrl url) {
        try {
            Request.Builder requestBuilder = new Request.Builder().url(url);
            Span activeSpan = tracer.activeSpan();
            Tags.SPAN_KIND.set(activeSpan, Tags.SPAN_KIND_CLIENT);
            Tags.HTTP_METHOD.set(activeSpan, "GET");
            Tags.HTTP_URL.set(activeSpan, url.toString());
            tracer.inject(activeSpan.context(), Format.Builtin.HTTP_HEADERS, new RequestBuilderCarrier(requestBuilder));
            Request request = requestBuilder.build();
            Response response = httpClient.newCall(request).execute();
            Tags.HTTP_STATUS.set(activeSpan, response.code());
            if (response.code() != 200) {
                throw new RuntimeException("Bad HTTP result: " + response);
            }
            return response;
        } catch (Exception e) {
            Tags.ERROR.set(tracer.activeSpan(), true);
            tracer.activeSpan().log(ImmutableMap.of(Fields.EVENT, "error", Fields.ERROR_OBJECT, e));
            throw new RuntimeException(e);
        }
    }


}
