package hu.nn.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.nn.entity.Policy;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {

    @Query("from Policy p where p.id = :id")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    Policy getById(@Param("id") Integer id);

}