package org.nistagram.followermicroservice.data.repository;

import org.neo4j.springframework.data.repository.Neo4jRepository;
import org.neo4j.springframework.data.repository.query.Query;
import org.nistagram.followermicroservice.data.model.Interaction;

public interface InteractionRepository extends Neo4jRepository<Interaction, Long> {
    @Query("MATCH (u1:NistagramUser {username: $0}), (u2:NistagramUser {username: $1}) CREATE (u1)-[r:FOLLOWING {followingStatus: $2, muted: false, notificationsOn: false}]->(u2) RETURN r")
    Interaction saveRelationship(String followerUsername, String followeeUsername, String followingStatus);

    @Query("MATCH (u1:NistagramUser {username: $0})-[r:FOLLOWING]->(u2:NistagramUser {username: $1}) SET r.followingStatus = $2 RETURN r")
    Interaction updateFollowingStatus(String followerUsername, String followeeUsername, String followingStatus);

    @Query("MATCH (u1:NistagramUser {username: $0})-[r:FOLLOWING]->(u2:NistagramUser {username: $1}) RETURN r")
    Interaction getRelationship(String followerUsername, String followeeUsername);

    @Query("MATCH (u1:NistagramUser {username: $0})-[r:FOLLOWING]->(u2:NistagramUser {username: $1}) DELETE r")
    void deleteRelationship(String followerUsername, String followeeUsername);
}
