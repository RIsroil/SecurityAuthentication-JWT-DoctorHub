package com.example.demo.message.mapper;

import com.example.demo.message.MessageEntity;
import com.example.demo.message.model.MessageView;
import com.example.demo.message.model.MessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    @Mapping(source = "chat.id", target = "chatId")
    @Mapping(source = "sender.username", target = "senderName")
    @Mapping(source = "sender.id", target = "senderId")
    MessageView toView(MessageEntity entity);

    List<MessageView> toViewList(List<MessageEntity> entities);

    // Helper to convert MessageView to MessageResponse
    // The original MessageResponse only had id, content, senderName, timestamp
    default MessageResponse toMessageResponse(MessageView view) {
        if (view == null) {
            return null;
        }
        return MessageResponse.builder()
                .id(view.getId())
                .content(view.getContent())
                .senderName(view.getSenderName())
                .timestamp(view.getTimestamp())
                // systemGenerated is not in original MessageResponse, so not mapped here
                .build();
    }

    default List<MessageResponse> toMessageResponseList(List<MessageView> views) {
        if (views == null) {
            return null;
        }
        return views.stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
    }
}
