package server.repository;

import commons.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Activity repository connected to the database.
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Page<Activity> findAll(Pageable pageable);

}
