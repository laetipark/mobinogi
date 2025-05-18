package com.example.mobinogi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_item")
@Getter
@Setter
@NoArgsConstructor
public class GameItem{
	
	@Id
	@Column(name = "item_id")
	private Integer itemId;
	
	@Column(name = "item_type", length = 30)
	private String itemType;
	
	@Column(name = "item_rarity", length = 30)
	private String itemRarity;
	
	@Column(name = "item_name", length = 30, unique = true)
	private String itemName;
	
	@Column(name = "item_effect", length = 200)
	private String itemEffect;
}
