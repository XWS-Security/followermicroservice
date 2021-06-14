package org.nistagram.followermicroservice.controller.dto;

import org.nistagram.followermicroservice.util.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class SubscriptionDto implements Serializable {
    @NotNull
    @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE)
    private String subscribedTo;

    @NotNull
    @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE)
    private String subscribedFrom;

    public SubscriptionDto() {
    }

    public SubscriptionDto(String subscribedTo, String subscribedFrom) {
        this.subscribedTo = subscribedTo;
        this.subscribedFrom = subscribedFrom;
    }

    public String getSubscribedTo() {
        return subscribedTo;
    }

    public void setSubscribedTo(String subscribedTo) {
        this.subscribedTo = subscribedTo;
    }

    public String getSubscribedFrom() {
        return subscribedFrom;
    }

    public void setSubscribedFrom(String subscribedFrom) {
        this.subscribedFrom = subscribedFrom;
    }
}
