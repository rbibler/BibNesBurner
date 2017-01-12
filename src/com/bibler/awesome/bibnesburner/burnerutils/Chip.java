package com.bibler.awesome.bibnesburner.burnerutils;

public class Chip {
	
	private int chipSize;
	private int pageSize;
	private String chipIdentifier;
	
	private int currentBurnAddress;
	private int currentReadAddress;
	
	
	public Chip(int chipSize, int pageSize, String chipIdentifier) {
		this.chipSize = chipSize;
		this.pageSize = pageSize;
		this.chipIdentifier = chipIdentifier;
	}
	
	public int getChipSize() {
		return chipSize;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public String getChipIdentifier() {
		return chipIdentifier;
	}
	
	public void setCurrentBurnAddress(int currentBurnAddress) {
		this.currentBurnAddress = currentBurnAddress;
	}
	
	public int getCurrentBurnAddress() {
		return currentBurnAddress;
	}
	
	public void incrementBurnAddressByPage() {
		currentBurnAddress += pageSize;
	}
	
	public void incrementBurnAddress(int increment) {
		currentBurnAddress += increment;
	}

	public void resetBurnAddress() {
		currentBurnAddress = 0;
		
	}

	public int getCurrentReadAddress() {
		return currentReadAddress;
	}
	
	public void incrementReadAddress() {
		currentReadAddress++;
	}

	public void setReadAddress(int currentReadAddress) {
		this.currentReadAddress = currentReadAddress;
		
	}

}
