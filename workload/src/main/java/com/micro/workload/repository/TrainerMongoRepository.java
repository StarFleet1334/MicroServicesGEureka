package com.micro.workload.repository;

import com.micro.workload.model.base.Trainer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerMongoRepository extends MongoRepository<Trainer, String> {


}
