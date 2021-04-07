package hu.nn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.nn.entity.Policy;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {

}