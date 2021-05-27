package org.nistagram.followermicroservice.data.model;


import org.neo4j.springframework.data.core.schema.*;

@RelationshipProperties()
public class Interaction {
    @Id
    @GeneratedValue
    private Long id;
    @Property("followingStatus")
    private FollowingStatus followingStatus;
    @Property("muted")
    private boolean muted;
    @Property("notificationsOn")
    private boolean notificationsOn;

    public Interaction() {
    }

    public Interaction(FollowingStatus followingStatus, boolean muted, boolean notificationsOn) {
        this.followingStatus = followingStatus;
        this.muted = muted;
        this.notificationsOn = notificationsOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FollowingStatus getFollowingStatus() {
        return followingStatus;
    }

    public void setFollowingStatus(FollowingStatus followingStatus) {
        this.followingStatus = followingStatus;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isNotificationsOn() {
        return notificationsOn;
    }

    public void setNotificationsOn(boolean notificationsOn) {
        this.notificationsOn = notificationsOn;
    }

    public void acceptFollowingRequest() {
        this.followingStatus = FollowingStatus.FOLLOWING;
    }
}
