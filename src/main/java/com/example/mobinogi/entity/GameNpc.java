package com.example.mobinogi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_npc")
@Getter
@Setter
@NoArgsConstructor
public class GameNpc{
	
	@Id
	@Column(name = "npc_id", nullable = false)
	private Integer npcId;
	
	@Column(name = "region_id")
	private Integer regionId;
	
	@Column(name = "npc_name", length = 30)
	private String npcName;
}
