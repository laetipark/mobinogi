package com.example.mobinogi.dto;

import com.example.mobinogi.entity.LifeBarter;
import com.example.mobinogi.entity.LifeCraft;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRelatedDataDto{
	private List<LifeBarter> bartersByItemId;
	private List<LifeBarter> bartersByExchangeId;
	private List<LifeCraft> craftsByItemId;
	
}
