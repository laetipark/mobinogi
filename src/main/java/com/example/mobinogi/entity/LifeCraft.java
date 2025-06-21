package com.example.mobinogi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "life_craft")
public class LifeCraft{
	@Id
	@Column(name = "craft_id")
	private int craftId;
	
	@Column(name = "item_id", nullable = false)
	private int itemId;
	
	@Column(name = "ingredient_id", nullable = false)
	private int craftIngredientId;
}
