package org.nistagram.followermicroservice.data.repository;

import org.neo4j.springframework.data.repository.Neo4jRepository;
import org.neo4j.springframework.data.repository.query.Query;
import org.nistagram.followermicroservice.data.model.User;

public interface UserRepository extends Neo4jRepository<User, Long> {
    User findByUsername(String username);

    @Query("MATCH (p:NistagramUser) WHERE id(p) = $0 SET p.username = $1, p.private = $2 RETURN p")
    User updateProperties(Long id, String username, boolean profilePrivate);
}
