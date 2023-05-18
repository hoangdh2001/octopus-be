package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.models.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpaceRepository extends JpaRepository<Space, UUID> {
//    @Query("SELECT s FROM Space s INNER JOIN Column c ON s.id=c.space.id WHERE s.name like '%?1' ")
//    public Space findByKeyword(String key);
//
//    @Query("SELECT s from Space s INNER JOIN Column c ON s.id=c.space.id where day(s.createTime) = ?1 and month(s.createTime) = ?2 and year(s.createTime) = ?3")
//    public List<Space> findByDay(int day, int month, int year);
//
//    @Query("select s from Space s where s.status = true")
//    public List<Space> findAllSpace();
}
