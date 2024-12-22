package com.micro.workload.repository;

import com.micro.workload.model.base.Trainer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerMongoRepository extends MongoRepository<Trainer, String> {

    List<Trainer> findByFirstNameAndLastName(String firstName, String lastName);
}
