package org.nistagram.followermicroservice.data.model;

import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Property;
import org.neo4j.springframework.data.core.schema.Relationship;

import java.util.Map;
import java.util.Objects;

@Node("NistagramUser")
public class User {
    @Id
    private String username;
    @Property("private")
    private boolean profilePrivate;
    @Relationship(type = "FOLLOWING", direction = Relationship.Direction.OUTGOING)
    private Map<User, Interaction> following;
    @Relationship(type = "FOLLOWING", direction = Relationship.Direction.INCOMING)
    private Map<User, Interaction> followers;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isProfilePrivate() {
        return profilePrivate;
    }

    public void setProfilePrivate(boolean profilePrivate) {
        this.profilePrivate = profilePrivate;
    }

    public Map<User, Interaction> getFollowing() {
        return following;
    }

    public void setFollowing(Map<User, Interaction> following) {
        this.following = following;
    }

    public Map<User, Interaction> getFollowers() {
        return followers;
    }

    public void setFollowers(Map<User, Interaction> followers) {
        this.followers = followers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return profilePrivate == user.profilePrivate &&
                Objects.equals(username, user.username) &&
                Objects.equals(following, user.following) &&
                Objects.equals(followers, user.followers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, profilePrivate, following, followers);
    }
}
