package com.example.mobinogi.repository;

import com.example.mobinogi.entity.LifeBarter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LifeBarterRepository extends JpaRepository<LifeBarter, Integer>{
	List<LifeBarter> findByGameItem_ItemName(String itemName);
	void deleteByBarterIdGreaterThanEqual(int rowIndex);
}
