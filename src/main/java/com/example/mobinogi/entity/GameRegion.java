package com.example.mobinogi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_region")
@Getter
@Setter
@NoArgsConstructor
public class GameRegion{
	
	@Id
	@Column(name = "region_id", nullable = false)
	private Integer regionId;
	
	@Column(name = "region_name", length = 30)
	private String regionName;
}
