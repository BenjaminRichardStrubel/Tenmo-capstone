package com.techelevator.tenmo.model;

public class User {

	private Integer id;
	private String username;
	private Transfer transfer;

	public Transfer getTransfer() {
		return transfer;
	}

	public void setTransfer(Transfer transfer) {
		this.transfer = transfer;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
}
