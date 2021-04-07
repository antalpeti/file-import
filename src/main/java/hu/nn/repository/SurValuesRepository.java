package hu.nn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.nn.entity.SurValues;

@Repository
public interface SurValuesRepository extends JpaRepository<SurValues, Integer> {

}