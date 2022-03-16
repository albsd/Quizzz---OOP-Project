package server.repository;

import commons.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface ActivityRepository extends CrudRepository<Activity, String> {
    long count();
    Page<Activity> findAll(Pageable pageable);

}
