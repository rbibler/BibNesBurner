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
import java.util.HashSet;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.bibler.awesome.bibnesburner.burnerutils.BitBurner;
import com.bibler.awesome.bibnesburner.fileutils.NESFile;
import com.bibler.awesome.bibnesburner.serialutils.SerialPortInstance;
import com.bibler.awesome.bibnesburner.serialutils.SerialPortManager;
import com.bibler.awesome.bibnesburner.ui.MainFrame;

import gnu.io.CommPortIdentifier;

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
		
	}

}
