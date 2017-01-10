package com.bibler.awesome.bibnesburner.burnerutils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Stack;

import com.bibler.awesome.bibnesburner.fileutils.NESFile;
import com.bibler.awesome.bibnesburner.interfaces.Notifiable;
import com.bibler.awesome.bibnesburner.interfaces.Notifier;
import com.bibler.awesome.bibnesburner.serialutils.MySerial;
import com.bibler.awesome.bibnesburner.utils.Utility;

public class BitBurner implements Notifiable, Notifier {
	
	private NESFile fileToBurn;
	private MySerial serial;
	private boolean messageSuccess = false;
	private boolean serialIndicatesFailure = false;
	private int address;
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
	private Stack<String> messageStack = new Stack<String>();
	private boolean readingRom;
	private String[] dataIn = new String[0x8000];
	private int pageSize = 64;
	private int chipSize = 0x8000;
	private int currentRomSize;
	
	public BitBurner() {
		state = PRG_BURN;
		serial = new MySerial();
		try {
			serial.connect("COM4", 115200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serial.registerObjectToNotify(this);
	}
	
	public void registerObjectToNotify(Notifiable objectToNotify) {
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
	}
	
	public void setFile(NESFile fileToBurn) {
		this.fileToBurn = fileToBurn;
	}
	
	public void connectToCommPort(String commPort) {
		try {
			serial.connect(commPort, 115200);
		} catch(Exception e) {}
	}
	
	public void changeChip(String chip) {
		switch(chip) {
		case "AT28C256":
			serial.writeString("C0" + newLine);
			pageSize = 64;
			chipSize = 0x8000;
			break;
		case "GLS29EE010":
			pageSize = 128;
			chipSize = 0x20000;
			serial.writeString("C1" + newLine);
			break;
		case "AM29F040":
			pageSize = 64;
			chipSize = 0x80000;
			serial.writeString("C2" + newLine);
			break;
		}
		dataIn = new String[chipSize];
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
				if(state == PRG_BURN) {
					writePRGBlocks();
				} else {
					writeCHRBlocks();
				}
			}
		});
		t.start();
	}
	
	private void initiateWriteSequence(int addressToWrite) {
		serial.writeString("W" + Utility.wordToHex(addressToWrite) + newLine);
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
	
	private void writePRGBlocks() {
		
		System.out.println(pageSize);
		address = 0;
		byte[] prg = fileToBurn.getPrg();
		currentRomSize = prg.length;
		//sendRomSize();
		initiateWriteSequence(0);
		OutputStream outStream = serial.getSerialOut();
		do {
			try {
				outStream.write(prg, address, pageSize);
				outStream.flush();
			} catch (IOException e) {}
			long time = System.currentTimeMillis();
			while(messageSuccess == false){
				if(System.currentTimeMillis() - time > 15000 || serialIndicatesFailure) {
					logMessage(updateMessageAreaChar + " FAIL at " + address + "!");
					serialIndicatesFailure = false;
					break;
				}
				try {
					Thread.sleep(10);
				} catch(InterruptedException e) {}
			}
			address += pageSize;
			logMessage(updateProgressBarChar + ((float) (address) / currentRomSize));
			messageSuccess = false;
			
		} while(address < currentRomSize);
		logMessage(updateMessageAreaChar + "Finished PRG burn in " + (System.currentTimeMillis() - burnStartTime) + " milliseconds!" + 
				"\n Please swap for CHR rom and press \"Burn\"");
		state = CHR_BURN;
		startReadAndCompareSequence();
		
	}
	
	private void writeCHRBlocks() {
		address = 0;
		initiateWriteSequence(0);
		byte[] chr = fileToBurn.getChr();
		currentRomSize = chr.length;
		OutputStream outStream = serial.getSerialOut();
		do {
			try {
				outStream.write(chr, address, pageSize);
				outStream.flush();
			} catch (IOException e) {}
			long time = System.currentTimeMillis();
			while(messageSuccess == false){
				if(System.currentTimeMillis() - time > 2000) {
					logMessage(updateMessageAreaChar + "FAIL!");
					break;
				}
				try {
					Thread.sleep(10);
				} catch(InterruptedException e) {}
			}
			address += pageSize;
			logMessage(updateProgressBarChar + ((float) (address) / currentRomSize));
			messageSuccess = false;
		} while(address < currentRomSize);
		logMessage(updateMessageAreaChar + "Finished burn in " + (System.currentTimeMillis() - burnStartTime) + " milliseconds!");
	}
	
	public void startReadAndCompareSequence() {
		address = 0;
		readingRom = true;
		logMessage(updateMessageAreaChar + "About to Read!");
		//serial.writeString("R" + newLine);
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
			if(s.length() == 1) {
				s = "0" + s;
			}
			dataIn[address] = s;
			address++;
			logMessage(updateProgressBarChar + ((float) (address) / chipSize));
			if(address >= dataIn.length) {
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
		if(notifier instanceof MySerial) {
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
