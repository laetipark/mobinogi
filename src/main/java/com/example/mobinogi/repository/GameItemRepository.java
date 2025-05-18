package com.example.mobinogi.repository;

import com.example.mobinogi.entity.GameItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameItemRepository extends JpaRepository<GameItem, Integer>{
	Optional<GameItem> findAllByItemName(String itemName);
	void deleteByItemIdGreaterThanEqual(int itemId);
}
