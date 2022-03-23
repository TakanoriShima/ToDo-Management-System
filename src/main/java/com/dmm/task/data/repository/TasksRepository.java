package com.dmm.task.data.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmm.task.data.entity.Tasks;

public interface TasksRepository extends JpaRepository<Tasks, Integer>, JpaSpecificationExecutor<Tasks> {

  @Query("select a from Tasks a where a.date between :from and :to and name = :name")
  List<Tasks> findByDateBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("name") String name);

  @Query("select a from Tasks a where a.date between :from and :to")
  List<Tasks> findAllByDateBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}

