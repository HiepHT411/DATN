package com.hoanghiep.hust.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hoanghiep.hust.entity.Result;

@Repository
public interface ResultRepo extends JpaRepository<Result, Integer> {
	
}
