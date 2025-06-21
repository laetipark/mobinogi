package com.example.mobinogi.service.game;

import com.example.mobinogi.dto.ItemRelatedDataDto;
import com.example.mobinogi.entity.GameItem;
import com.example.mobinogi.entity.LifeBarter;
import com.example.mobinogi.entity.LifeCraft;
import com.example.mobinogi.repository.GameItemRepository;
import com.example.mobinogi.repository.LifeBarterRepository;
import com.example.mobinogi.repository.LifeCraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameItemService{
	private final GameItemRepository gameItemRepository;
	private final LifeBarterRepository lifeBarterRepository;
	private final LifeCraftRepository lifeCraftRepository;
	
	public ItemRelatedDataDto getAllRelatedDataByItemName(String itemName){
		int itemId = gameItemRepository.findByItemNameContaining(itemName)
			.stream()
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Item not found"))
			.getItemId();
		
		List<LifeBarter> bartersByItemId = lifeBarterRepository.findByItemId(itemId);
		List<LifeBarter> bartersByExchangeId = lifeBarterRepository.findByExchangeId(itemId);
		List<LifeCraft> craftsByItemId = lifeCraftRepository.findByItemId(itemId);
		
		ItemRelatedDataDto dto = new ItemRelatedDataDto();
		dto.setBartersByItemId(bartersByItemId);
		dto.setBartersByExchangeId(bartersByExchangeId);
		dto.setCraftsByItemId(craftsByItemId);
		return dto;
	}
	
	public void deleteGameItemSafely(int rowIndex){
		// 1. 관련된 life_barter 삭제
		lifeBarterRepository.deleteAllByItemId(rowIndex);
		lifeBarterRepository.deleteAllByExchangeId(rowIndex);
		
		// 2. 관련된 life_craft 삭제
		lifeCraftRepository.deleteAllByItemId(rowIndex);
		
		// ✅ rowIndex 이후의 기존 아이템 삭제
		gameItemRepository.deleteByItemIdGreaterThanEqual(rowIndex);
	}
}
