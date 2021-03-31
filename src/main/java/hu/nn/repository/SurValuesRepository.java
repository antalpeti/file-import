package hu.nn.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.nn.entity.SurValues;

@Repository
public interface SurValuesRepository extends JpaRepository<SurValues, Integer> {

	@Query("from SurValues s where s.id = :id")
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
	SurValues getById(@Param("id") Integer id);

}