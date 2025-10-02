package com.example.mobinogi.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

// Composite key class
@EqualsAndHashCode
class UserRankId implements Serializable{
	
	private Integer serverId;
	private Integer classId;
	private String userName;
	
	// Default constructor
	public UserRankId(){
	}
	
	// Constructor with all key fields
	public UserRankId(Integer serverId, Integer classId, String userName){
		this.serverId = serverId;
		this.classId = classId;
		this.userName = userName;
	}
	
}

@Entity
@Table(name = "user_rank")
@IdClass(UserRankId.class)  // Specify the composite key class
@Getter
@Setter
public class UserRank{
	
	@Id
	@Column(name = "server_id", nullable = false)
	private Integer serverId;
	
	@Id
	@Column(name = "class_id", nullable = false)
	private Integer classId;
	
	@Id
	@Column(name = "user_name", nullable = false, length = 255)
	private String userName;
	
	@Column(name = "user_power")
	private Integer userPower;
	
	@Column(name = "user_vitality")
	private Integer userVitality;
	
	@Column(name = "user_attractiveness")
	private Integer userAttractiveness;
}