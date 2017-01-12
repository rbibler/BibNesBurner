package com.bibler.awesome.bibnesburner.burnerutils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Stack;

import com.bibler.awesome.bibnesburner.fileutils.NESFile;
import com.bibler.awesome.bibnesburner.interfaces.Notifiable;
import com.bibler.awesome.bibnesburner.interfaces.Notifier;
import com.bibler.awesome.bibnesburner.serialutils.SerialPortInstance;
import com.bibler.awesome.bibnesburner.utils.Utility;

public class BitBurner implements Notifiable, Notifier {
	
	private final int WRITE_MODE = 0x03;
	private final int READ_ALL = 0x04;
	
	private NESFile fileToBurn;
	private SerialPortInstance serial;
	private boolean messageSuccess = false;
	private boolean serialIndicatesFailure = false;
	private int state;
	private long burnStartTime;
	private long timeOutTime = 8000;
	
	public static String updateMessageAreaChar = "M";
	public static  String updateProgressBarChar = "B";
	public static String updateHexAreaChar = "H";
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	
	private final int PRG_BURN = 0x32;
	private final int CHR_BURN = 0x33;
	private final String newLine = "\n";
	
	private boolean readingRom;
	private String[] dataIn = new String[0x8000];
	private int currentRomSize;
	
	private Chip chip;
	
	public BitBurner() {
		state = PRG_BURN;
		chip = ChipFactory.createChip(ChipFactory.AT28C256);
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
		chip = ChipFactory.createChip(chipID);
		dataIn = new String[chip.getChipSize()];
		messageSuccess = false;
		long sentWriteMessageTime = System.currentTimeMillis();
		while(!messageSuccess) {
			if(System.currentTimeMillis() - sentWriteMessageTime >= timeOutTime) {
				logMessage(updateMessageAreaChar + "Change Chip Message response timed out!");
				break;
			}
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {}
		}
		messageSuccess = false;
	}
	
	
	public void startFullBurnSequence() {
		burnStartTime = System.currentTimeMillis();
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
	
	private void sendRomSize() {
		String s = Utility.doubleWordToHex(currentRomSize).substring(3);
		System.out.println("Rom Size: " + s);
		serial.writeString("S" + s + newLine);
		long sentWriteMessageTime = System.currentTimeMillis();
		while(!messageSuccess) {
			if(System.currentTimeMillis() - sentWriteMessageTime >= timeOutTime) {
				logMessage(updateMessageAreaChar + "Rom Message response timed out!");
				break;
			}
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {}
		}
		messageSuccess = false;
	}
	
	private void writeROM() {
		chip.resetBurnAddress();
		byte[] rom = state == PRG_BURN ? fileToBurn.getPrg() : fileToBurn.getChr();
		currentRomSize = rom.length;
		initiateWriteSequence(0);
		final int pageSize = chip.getPageSize();
		int currentBurnAddress;
		do {
			currentBurnAddress = chip.getCurrentBurnAddress();
			serial.writeBlock(rom, currentBurnAddress, pageSize);
			long time = System.currentTimeMillis();
			while(messageSuccess == false){
				if(System.currentTimeMillis() - time > 15000 || serialIndicatesFailure) {
					logMessage(updateMessageAreaChar + " FAIL at " + currentBurnAddress + "!");
					serialIndicatesFailure = false;
					break;
				}
				try {
					Thread.sleep(10);
				} catch(InterruptedException e) {}
			}
			chip.incrementBurnAddressByPage();
			logMessage(updateProgressBarChar + ((float) (chip.getCurrentBurnAddress()) / currentRomSize));
			messageSuccess = false;
			
		} while(chip.getCurrentBurnAddress() < currentRomSize);
		
		if(state == PRG_BURN) {
			finishPRG();
		} else {
			finishCHR();
		}
	}
	
	private void finishPRG() {
		logMessage(updateMessageAreaChar + "Finished PRG burn in " + (System.currentTimeMillis() - burnStartTime) + " milliseconds!" + 
				"\n Please swap for CHR rom and press \"Burn\"");
		state = CHR_BURN;
	}
	
	private void finishCHR() {
		state = PRG_BURN;
	}
	
	private void processDataIn() {
		int count = 0;
		StringBuilder b = new StringBuilder();
		for(String s : dataIn) {
			b.append(s + " ");
			count++;
			if(count == 16) {
				count = 0;
				logMessage(updateHexAreaChar + b.toString());
				b.delete(0, b.length());
			}
		}
	}
	
	private void logMessage(String message) {
		notifyAllObjects(message);
	}
	
	public synchronized void processMessage(String s) {
		System.out.println(s);
		if(readingRom) {
			int currentReadAddress = chip.getCurrentReadAddress();
			if(s.length() == 1) {
				s = "0" + s;
			}
			dataIn[currentReadAddress++] = s;
			chip.setReadAddress(currentReadAddress);
			logMessage(updateProgressBarChar + ((float) (currentReadAddress) / chip.getChipSize()));
			if(currentReadAddress >= dataIn.length) {
				logMessage(updateMessageAreaChar + "Read finished!");
				readingRom = false;
				processDataIn();
			}
		} else if(s.contains("%")) {
			System.out.println("Message success! at: " + (System.currentTimeMillis() / 1000));
			messageSuccess = true;
		} else if(s.equals("Q")) {
			messageSuccess = false;
			serialIndicatesFailure = true;
		} else if(s.charAt(0) == '*'){
			System.out.println(s);
			logMessage(updateMessageAreaChar + s.substring(1));
		}
	}
	
	
	@Override
	public void takeNotice(Object notifier, String s) {
		if(notifier instanceof SerialPortInstance) {
			processMessage(s);
		}
	}

	@Override
	public void notifyAllObjects(String message) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(this, message);
		}
	}
	
}
