package com.socialnetwork.api.service;

import io.jaegertracing.internal.JaegerTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageSagaService {

    private JaegerTracer tracer;

    private MessageService messageService;

    MessageSagaService(
        MessageService messageService,
        @Qualifier("tracer") JaegerTracer tracer
    ){
        this.messageService = messageService;
        this.tracer = tracer;
    }

    public boolean postMessageSaga(int profileId, int roomId, String text) {
        var messageId = messageService.saveMessagePending(roomId, text, profileId);

        var isAllCountersIncreased = increaseCounterForAllParticipantsInChat(roomId, profileId);
        if (isAllCountersIncreased){
            messageService.saveMessageAproved(messageId);
        } else
        {
            messageService.saveMessageRejected(messageId);
        }
        return true;
    }


    private boolean increaseCounterForAllParticipantsInChat(int roomId, int posterProfileId){
        var participants = messageService.getChatParticipants(roomId);
        var filteredParticipants = participants.stream().filter(id -> id != posterProfileId).toList();

//        TODO move the logic to {<list of ids>} -> CounterService,-> return false or true as it is counter logic
        return filteredParticipants.stream().allMatch(participantId -> {
            return messageService.callToIncreaseCounter(participantId, roomId,1);
        });
    }

}
