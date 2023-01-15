package com.hoanghiep.hust.repository;

import com.hoanghiep.hust.entity.UnitTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitTestRepository extends JpaRepository<UnitTest, Long> {


}
