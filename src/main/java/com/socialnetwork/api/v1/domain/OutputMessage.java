package com.socialnetwork.api.v1.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OutputMessage {
    public String from;

    public String text;

    public String time;
}
