package com.example.mobinogi.service;

import com.example.mobinogi.entity.LifeBarter;
import com.example.mobinogi.repository.LifeBarterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameBarterService{
	private final LifeBarterRepository lifeBarterRepository;
	
	public List<LifeBarter> getBartersByItemName(String itemName){
		return lifeBarterRepository.findByGameItem_ItemName(itemName);
	}
}
