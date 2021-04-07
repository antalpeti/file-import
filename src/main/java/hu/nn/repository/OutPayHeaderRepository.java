package hu.nn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.nn.entity.OutPayHeader;

@Repository
public interface OutPayHeaderRepository extends JpaRepository<OutPayHeader, Integer> {

}