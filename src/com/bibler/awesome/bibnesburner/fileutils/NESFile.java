package com.bibler.awesome.bibnesburner.fileutils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.bibler.awesome.bibnesburner.interfaces.Notifiable;
import com.bibler.awesome.bibnesburner.interfaces.Notifier;
import com.bibler.awesome.bibnesburner.ui.InfoPanel;

public class NESFile implements Notifier {
	
	private byte[] prg = new byte[0x8000];
	private byte[] chr = new byte[0x8000];
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	
	public NESFile(FileInputStream f, InfoPanel panel, boolean bin) {
		registerObjectToNotify(panel);
		if(bin) {
			loadBin(f);
		} else {
			splitFile(f);
		}
		
	}
	
	public void registerObjectToNotify(Notifiable objectToNotify) {
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
	}
	
	public void loadBin(FileInputStream f) {
		ArrayList<Integer> readIn = new ArrayList<Integer>();
		try {
			int r = 0;
			do {
				r = f.read();
				if(r == -1) {
					break;
				}
				readIn.add(r);
			} while(r != -1);
		} catch(IOException e) {}
		prg = new byte[readIn.size()];
		for(int i = 0; i < prg.length; i++) {
			prg[i] = (byte) (readIn.get(i) & 0xFF);
		}
		notifyAllObjects("P" + prg.length);
	}
	
	public void splitFile(FileInputStream f) {
		byte[] headerBytes = new byte[16];
		try {
			f.read(headerBytes);
		} catch(IOException e) {}
		int prgSize = headerBytes[4] * 0x4000;
		int chrSize = headerBytes[5] * 0x2000;
		notifyAllObjects("P" + prgSize);
		notifyAllObjects("C" + chrSize);
		fillPrg(f, prgSize);
		fillChr(f, chrSize);
	}
	
	private void fillPrg(FileInputStream f, int prgSize) {
		byte[] tempPrg = new byte[prgSize];
		prg = new byte[prgSize];
		try {
			f.read(tempPrg);
		} catch(IOException e) {}
		for(int i = 0; i < prg.length; i++) {
			prg[i] = tempPrg[i % prgSize];
		}
	}
	
	private void fillChr(FileInputStream f, int chrSize) {
		if(chrSize == 0) {
			return;
		}
		byte[] tempChr = new byte[chrSize];
		try {
			f.read(tempChr);
		} catch(IOException e) {}
		for(int i = 0; i < chr.length; i++) {
			chr[i] = tempChr[i % chrSize];
		}
	}
	
	public byte[] getPrg() {
		return prg;
	}
	
	public byte[] getChr() {
		return chr;
	}

	@Override
	public void notifyAllObjects(String message) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(this, message);
		}
		
	}

}
