package com.bibler.awesome.bibnesburner.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.bibler.awesome.bibnesburner.burnerutils.BitBurner;
import com.bibler.awesome.bibnesburner.fileutils.NESFile;
import com.bibler.awesome.bibnesburner.serialutils.MySerial;
import com.bibler.awesome.bibnesburner.ui.MainFrame;

public class Main {
	
	static int address;
	static boolean goNext;
	
	
	public static void main(String[] args) {
		 try {
	            // Set cross-platform Java L&F (also called "Metal")
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
	        
		MainFrame mainFrame = new MainFrame();
		/*File f = new File("C:/users/ryan/desktop/nes/roms/chalnger.nes");
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(f);
		} catch(IOException e) {e.printStackTrace();}
		NESFile nesFile = new NESFile(fstream);
		
		MySerial serial = new MySerial();
		try {
			serial.connect("COM3", 115200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		BitBurner burner = new BitBurner();
		burner.setFile(nesFile);
		burner.setSerial(serial);
		try {
			Thread.sleep(3000);
		} catch(InterruptedException e) {}
		burner.startFullBurnSequence();
		*/
	}

}
