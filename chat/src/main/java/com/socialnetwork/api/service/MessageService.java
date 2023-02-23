package com.socialnetwork.api.service;

import com.google.common.collect.ImmutableMap;
import com.socialnetwork.api.v1.domain.MessageDto;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.log.Fields;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import jodd.net.HttpMethod;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageService {
    @Autowired
    @Qualifier("jdbcTemplate3")
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("tracer")
    private JaegerTracer tracer;

    @Value("${servicediscovery.counter.url}")
    private String COUNTER_URL;

    @Value("${servicediscovery.counter.port}")
    private int COUNTER_API_PORT;

    private final OkHttpClient httpClient = new OkHttpClient();

    public List<String> getListOfRooms(int userId) {
        String query = "SELECT id FROM rooms WHERE ?=ANY(participants)";
        var res = jdbcTemplate.query(
            query,
            (rs, rowNum) ->
                rs.getString("id"),
            userId);
        return res;

    }

    public List<MessageDto> getMessages(int roomId) {
        String query = "SELECT * FROM messages WHERE roomId=? AND status='RECEIVED'";
        return jdbcTemplate.query(
            query,
            (rs, rowNum) ->
                MessageDto.builder()
                    .text(rs.getString("text"))
                    .id(rs.getString("id"))
                    .timestamp(rs.getString("timestamp"))
                    .build(),
            roomId);
    }

    public boolean postMessageSaga(int profileId, int roomId, String text) {
        var messageId = saveMessagePending(roomId, text, profileId);

        var isAllCountersIncreased = increaseCounterForAllParticipantsInChat(roomId, profileId);
        if (isAllCountersIncreased){
            saveMessageAproved(messageId);
        } else
        {
            saveMessageRejected(messageId);
        }
        return true;
    }


    private boolean increaseCounterForAllParticipantsInChat(int roomId, int posterProfileId){
        var participants = getChatParticipants(roomId);
        var filteredParticipants = participants.stream().filter(id -> id != posterProfileId).toList();

//        TODO move the logic to {<list of ids>} -> CounterService,-> return false or true as it is counter logic
        return filteredParticipants.stream().allMatch(participantId -> {
            return callToIncreaseCounter(participantId, roomId,1);
        });
    }

    private Number saveMessagePending(int roomId, String text, int profileId) {
        var date = ZonedDateTime.now().toLocalDateTime();
        String query =
            "INSERT into messages (roomid,text,timestamp,status,poster_id) values (?,?,?,'RECEIVED_PENDING'::message_status_type,?) RETURNING id";
        KeyHolder keyHolder = new GeneratedKeyHolder();
//        query, roomId, text, date, profileId
        int key = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, roomId);
            ps.setString(2, text);
            ps.setTimestamp(3, Timestamp.valueOf(date));
            ps.setInt(4, profileId);
            return ps;
            },keyHolder
        );

        return keyHolder.getKey();
    }

    private boolean saveMessageAproved(Number messageId) {
        String query = "UPDATE messages SET status='RECEIVED'::message_status_type WHERE id=?";
        var res = jdbcTemplate.update(
            query, messageId
        );
        return true;
    }    private boolean saveMessageRejected(Number messageId) {
        String query = "UPDATE messages SET status='RECEIVED_REJECTED'::message_status_type WHERE id=?";
        var res = jdbcTemplate.update(
            query, messageId
        );
        return true;
    }

    @SneakyThrows
    private List<Integer> getChatParticipants(int roomId) {
        String query = "SELECT participants FROM rooms WHERE id=?";
        List<Array> res = jdbcTemplate.query(
            query,
            (rs, rowNum) ->
                rs.getArray("participants"),
            roomId);
        Integer[] r = (Integer[]) res.get(0).getArray();
        return new ArrayList<>(Arrays.asList(r));
    }

    private boolean callToIncreaseCounter(int profileId, int chatId, int value) {
        Span span = this.tracer.buildSpan("app.chat.service.request-to-increase-count").start();
        try (Scope scope = tracer.scopeManager().activate(span, true)) {
            HttpUrl url = new HttpUrl.Builder().scheme("http").host(COUNTER_URL).port(COUNTER_API_PORT)
                .addPathSegment("api")
                .addPathSegment("profile")
                .addPathSegment(String.valueOf(profileId))
                .addPathSegment("rooms")
                .addPathSegment(String.valueOf(chatId))
                .addPathSegment("counter")
                .build();
            var response = makeRequest(url, HttpMethod.PUT, String.valueOf(value));
            return response.isSuccessful();
        } finally {
            span.finish();
        }
//        return true;
    }

    private Response makeRequest(HttpUrl url, HttpMethod method, String postBody) {
        try {

            if (method == null) {
                method = HttpMethod.GET;
            }
            Request.Builder requestBuilder = new Request.Builder().url(url);
            if (HttpMethod.POST.equals(method)) {
                requestBuilder.post(
                    RequestBody.create(
                        MediaType.parse("application/json"), postBody
                    )
                );
            }
            if (HttpMethod.PUT.equals(method)) {
                requestBuilder.put(
                    RequestBody.create(
                        MediaType.parse("application/json"), postBody
                    )
                );
            }
            Span activeSpan = tracer.activeSpan();
            Tags.SPAN_KIND.set(activeSpan, Tags.SPAN_KIND_CLIENT);
            Tags.HTTP_METHOD.set(activeSpan, String.valueOf(method));
            Tags.HTTP_URL.set(activeSpan, url.toString());
            tracer.inject(activeSpan.context(), Format.Builtin.HTTP_HEADERS,
                new RequestBuilderCarrier(requestBuilder));
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
