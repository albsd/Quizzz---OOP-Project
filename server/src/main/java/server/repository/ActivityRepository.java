package server.repository;

import commons.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Activity repository connected to the database.
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * Retries all activity records in the database.
     */
    Page<Activity> findAll(Pageable pageable);

    /**
     * Finds the latest record in the database.
     */
    Optional<Activity> findTopByOrderByIdDesc();
}
