package com.flix.ems_system.repository;

import com.flix.ems_system.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByTitleAndDescription(String title, String description);
}
