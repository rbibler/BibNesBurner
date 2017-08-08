package com.bibler.awesome.bibnesburner.burnerutils;

import java.awt.Color;
import java.util.ArrayList;
import com.bibler.awesome.bibnesburner.fileutils.NESFile;
import com.bibler.awesome.bibnesburner.interfaces.Notifiable;
import com.bibler.awesome.bibnesburner.interfaces.Notifier;
import com.bibler.awesome.bibnesburner.serialutils.SerialPortInstance;
import com.bibler.awesome.bibnesburner.ui.MainFrame;

public class BitBurner implements Notifiable, Notifier {
	
	private final int WRITE_MODE = 0x61;
	private final int READ_ALL = 0x62;
	
	private final int SUCCESS = 0x69;
	private final int FAIL = 0x6B;
	private final int STOP_BURN = 0x73;
	
	private final int CHANGE_TO_PRG = 0x70;
	private final int CHANGE_TO_CHR = 0x71;
	
	private NESFile fileToBurn;
	private SerialPortInstance serial;
	private boolean messageSuccess = false;
	private boolean reading;
	private int state;
	private long timeOutTime = 8000;
	
	public static String updateMessageAreaChar = "M";
	public static  String updateProgressBarChar = "B";
	public static String updateHexAreaChar = "H";
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	
	public static final int PRG_BURN = 0x32;
	public static final int CHR_BURN = 0x33;
	private final String newLine = "\n";
	
	private int[] dataIn = new int[0x8000];
	private int[] currentRom;
	private int currentRomSize;
	
	private Chip chip;
	private MainFrame mainFrame;
	
	public BitBurner(MainFrame mainFrame) {
		state = PRG_BURN;
		this.mainFrame = mainFrame;
	}
	
	public void registerObjectToNotify(Notifiable objectToNotify) {
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
	}
	
	public void setSerialPort(SerialPortInstance serial) {
		this.serial = serial;
		serial.registerObjectToNotify(this);
	}
	
	public void setFile(NESFile fileToBurn) {
		this.fileToBurn = fileToBurn;
	}
	
	public void changeChip(String chipID) {
		serial.writeBlock(new int[] {0x72, 1}, 0, 2);
		chip = ChipFactory.createChip(chipID);
		System.out.println("New Chip:");
		chip.printChipInfo();
		dataIn = new int[chip.getChipSize()];
		messageSuccess = false;
		mainFrame.setReadData(dataIn);
		
	}
	
	
	public void startFullBurnSequence(int thingToBurn) {
		state = thingToBurn;
		Thread t = new Thread(new Runnable() {
			@Override 
			public void run() {
				writeROM();
			}
		});
		t.start();
	}
	
	private void initiateWriteSequence(int addressToWrite) {
		serial.writeInstruction(WRITE_MODE);
		long sentWriteMessageTime = System.currentTimeMillis();
		while(!messageSuccess) {
			if(System.currentTimeMillis() - sentWriteMessageTime >= timeOutTime) {
				logMessage(updateMessageAreaChar + "Write Message response timed out!");
				break;
			}
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {}
		}
		if(messageSuccess) {
			logMessage(updateMessageAreaChar + "In Write mode!");
		}
		messageSuccess = false;
	}
	
	
	private void writeROM() {
		
		chip.resetBurnAddress();
		currentRom = state == PRG_BURN ? fileToBurn.getPrg() : fileToBurn.getChr();
		mainFrame.setRomData(currentRom);
		currentRomSize = currentRom.length;
		initiateWriteSequence(0);
		final int pageSize = chip.getPageSize();
		int currentBurnAddress;
		do {
			currentBurnAddress = chip.getCurrentBurnAddress();
			serial.writeBlock(currentRom, currentBurnAddress, pageSize);
			long time = System.currentTimeMillis();
			while(messageSuccess == false){
				System.out.println("IN IT!");
				if(System.currentTimeMillis() - time > 15000) {
					logMessage(updateMessageAreaChar + " FAIL at " + currentBurnAddress + "!");
					break;
				}
				try {
					Thread.sleep(10);
				} catch(InterruptedException e) {}
			}
				//try {
					//Thread.sleep(200);
				//} catch(InterruptedException e) {}
			chip.incrementBurnAddressByPage();
			logMessage(updateProgressBarChar + ((float) (chip.getCurrentBurnAddress()) / currentRomSize));
			messageSuccess = false;
			
		} while(chip.getCurrentBurnAddress() < currentRomSize);
		try {
			Thread.sleep(100);
		} catch(InterruptedException e) {}
		serial.writeInstruction(STOP_BURN);
		verifyRom(state);
		
	}
	
	public void readFullRom() {
		mainFrame.setReadData(dataIn);
		reading = true;
		state = this.READ_ALL;
		serial.writeInstruction(READ_ALL);
		chip.setReadAddress(0);
	}
	
	private void logMessage(String message) {
		notifyAllObjects(this, message);
	}
	
	public void verifyRom(int oldState) {
		int errorCount = 0;
		readFullRom();
		while(reading) {
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {}
		}
		for(int i = 0; i < currentRomSize; i++) {
			if((dataIn[i] & 0xFF) != (currentRom[i] & 0xFF)) {
				errorCount++;
				logMessage(updateMessageAreaChar + " Error found at " + Integer.toHexString(i) + " Expected: " + Integer.toHexString(currentRom[i]) 
				+ " Found: " + Integer.toHexString(dataIn[i]));
			}
			logMessage(updateProgressBarChar + ((float) (i) / currentRomSize));
		}
		if(errorCount != 0) {
			logMessage(updateMessageAreaChar + "Burn Failed! Found " + errorCount + " errors.");
		} else {
			logMessage(updateMessageAreaChar + "Burn Success!");
		}
		state = oldState;
	}
	
	public synchronized void processMessage(int[] message, int length) {
		if(length == 1&& message[0] == SUCCESS) {
			messageSuccess = true;
			//processSuccess();
		} else if(length == 1 && message[0] == FAIL) {
			messageSuccess = false;

			//processFailure();
		} else if(state == READ_ALL) {
			int currentReadAddress = chip.getCurrentReadAddress();
			for(int i = 0; i < length; i++) {
				dataIn[currentReadAddress++] = message[i];
			}
			chip.setReadAddress(currentReadAddress);
			logMessage(updateProgressBarChar + ((float) (currentReadAddress) / chip.getChipSize()));
			if(currentReadAddress >= chip.getChipSize()) {
				System.out.println("READ FINISHED!!!");
				logMessage(updateMessageAreaChar + "Read finished!");
				reading = false;
			}
		} 
	}
	
	
	@Override
	public void takeNotice(Object notifier, Object packet, String s) {
		if(notifier instanceof SerialPortInstance) {
			processMessage((int[]) packet, Integer.parseInt(s));
		}
	}

	@Override
	public void notifyAllObjects(Object packet, String message) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(this, packet, message);
		}
	}

	
	
}
