package com.bibler.awesome.bibnesburner.burnerutils;

public class ChipFactory {
	
	public static final int AT28C256 = 0x03;
	public static final int GLS29EE010 = 0x04;
	public static final int AM29F040 = 0x05;
	
	public static Chip createChip(int chipID) {
		Chip chip = null;
		switch(chipID) {
		case AT28C256:
			chip = new Chip(0x8000, 0x40, "C0");
			break;
		case GLS29EE010:
			chip = new Chip(0x20000, 0x80, "C1");
			break;
		case AM29F040:
			chip = new Chip(0x20000, 0x40, "C1");
			break;
		}
		return chip;
	}

	public static Chip createChip(String chipID) {
		Chip chip = null;
		switch(chipID) {
		case "AT28C256":
			chip = new Chip(0x8000, 0x40, "C0");
			break;
		case "GLS29EE010":
			chip = new Chip(0x20000, 0x80, "C1");
			break;
		case "AM29F040":
			chip = new Chip(0x20000, 0x40, "C1");
			break;
		}
		return chip;
	}

}
