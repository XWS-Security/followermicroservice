package org.nistagram.followermicroservice.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.springframework.data.core.schema.*;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Node("NistagramUser")
public class User implements UserDetails {
    @Transient
    private final String administrationRole = "NISTAGRAM_USER_ROLE";

    @Id
    @GeneratedValue
    private Long id;
    @Property("username")
    private String username;
    @Property("private")
    private boolean profilePrivate;
    @Relationship(type = "FOLLOWING", direction = Relationship.Direction.INCOMING)
    private Map<User, Interaction> followers;

    @Property(name = "enabled")
    private boolean enabled = false;
    @Property(name = "lastPasswordResetDate")
    private Date lastPasswordResetDate;
    @Relationship(type = "HAS_ROLE", direction = Relationship.Direction.INCOMING)
    private List<Role> roles;

    public User() {
        this.followers = new HashMap<>();
    }

    public User(String username, boolean profilePrivate) {
        this.username = username;
        this.profilePrivate = profilePrivate;
        this.followers = new HashMap<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
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

    public String getAdministrationRole() {
        return administrationRole;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Date lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getGrantedAuthorities();
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return "";
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
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

    public boolean isBlocked(User user) {
        return this.followers.containsKey(user) && this.followers.get(user).getFollowingStatus() == FollowingStatus.BLOCKED;
    }

    public boolean isFollowing(User user) {
        return this.followers.containsKey(user) && this.followers.get(user).getFollowingStatus() == FollowingStatus.FOLLOWING;
    }

    public boolean isWaitingForApproval(User user) {
        return this.followers.containsKey(user) && this.followers.get(user).getFollowingStatus() == FollowingStatus.WAITING_FOR_APPROVAL;
    }

    private List<GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : this.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }
}
