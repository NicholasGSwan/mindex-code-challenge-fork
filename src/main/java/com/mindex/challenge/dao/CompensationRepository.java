package com.mindex.challenge.dao;

import org.springframework.stereotype.Repository;

import com.mindex.challenge.data.Compensation;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;



@Repository
public interface CompensationRepository extends MongoRepository<Compensation, String> {
    List<Compensation> findByEmployeeOrderByEffectiveDateDesc(String employee);
}
