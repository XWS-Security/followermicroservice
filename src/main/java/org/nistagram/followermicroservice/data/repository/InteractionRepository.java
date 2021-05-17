package org.nistagram.followermicroservice.data.repository;

import org.nistagram.followermicroservice.data.model.Interaction;
import org.springframework.data.repository.CrudRepository;

public interface InteractionRepository extends CrudRepository<Interaction, Long> {
}
