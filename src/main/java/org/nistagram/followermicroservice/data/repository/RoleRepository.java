package org.nistagram.followermicroservice.data.repository;

import org.neo4j.springframework.data.repository.Neo4jRepository;
import org.neo4j.springframework.data.repository.query.Query;
import org.nistagram.followermicroservice.data.model.Role;

import java.util.List;

public interface RoleRepository extends Neo4jRepository<Role, Long> {
    @Query("MATCH (r:FAuthRole {name: $0}) RETURN r")
    List<Role> findByName(String name);
}
