package com.composebean.global.slack;

public record SlackMessageResponse(
        boolean ok,
        String error
) {
}