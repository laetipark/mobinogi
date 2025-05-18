package com.example.mobinogi.repository;

import com.example.mobinogi.entity.LifeBarter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LifeBarterRepository extends JpaRepository<LifeBarter, Integer>{
	void deleteByBarterIdGreaterThanEqual(int rowIndex);
}
