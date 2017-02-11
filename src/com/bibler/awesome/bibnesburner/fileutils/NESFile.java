package com.bibler.awesome.bibnesburner.fileutils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.bibler.awesome.bibnesburner.interfaces.Notifiable;
import com.bibler.awesome.bibnesburner.interfaces.Notifier;
import com.bibler.awesome.bibnesburner.ui.InfoPanel;

public class NESFile implements Notifier {
	
	private int[] prg = new int[0x8000];
	private int[] chr = new int[0x8000];
	
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
		prg = new int[readIn.size()];
		for(int i = 0; i < prg.length; i++) {
			prg[i] = (byte) (readIn.get(i) & 0xFF);
		}
		notifyAllObjects(null, "P" + prg.length);
	}
	
	public void splitFile(FileInputStream f) {
		byte[] headerBytes = new byte[16];
		try {
			f.read(headerBytes);
		} catch(IOException e) {}
		int prgSize = headerBytes[4] * 0x4000;
		int chrSize = headerBytes[5] * 0x2000;
		notifyAllObjects(null, "P" + prgSize);
		notifyAllObjects(null, "C" + chrSize);
		fillPrg(f, prgSize);
		fillChr(f, chrSize);
	}
	
	private void fillPrg(FileInputStream f, int prgSize) {
		byte[] tempPrg = new byte[prgSize];
		prg = new int[0x8000];
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
	
	public int[] getPrg() {
		return prg;
	}
	
	public int[] getChr() {
		return chr;
	}

	@Override
	public void notifyAllObjects(Object packet, String message) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(this, packet, message);
		}
		
	}

	public int[] getCombinedData() {
		int[] combined = new int[prg.length + chr.length];
		for(int i = 0; i < prg.length; i++) {
			combined[i] = prg[i];
		}
		for(int i = 0; i < chr.length; i++) {
			combined[i + (prg.length - 1)] = chr[i];
		}
		return combined;
	}

}
