package org.nistagram.followermicroservice.data.model;

import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Property;
import org.neo4j.springframework.data.core.schema.Relationship;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Node("NistagramUser")
public class User {
    @Id
    private String username;
    @Property("private")
    private boolean profilePrivate;
    @Relationship(type = "FOLLOWING", direction = Relationship.Direction.INCOMING)
    private Map<User, Interaction> followers;

    public User() {
        followers = new HashMap<>();
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
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
