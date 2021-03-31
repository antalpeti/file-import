package hu.nn.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.nn.entity.OutPayHeader;

@Repository
public interface OutPayHeaderRepository extends JpaRepository<OutPayHeader, Integer> {

	@Query("from OutPayHeader o where o.outpayHeaderId = :outpayHeaderId")
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
	OutPayHeader getByOutpayHeaderId(@Param("outpayHeaderId") Integer outpayHeaderId);

}