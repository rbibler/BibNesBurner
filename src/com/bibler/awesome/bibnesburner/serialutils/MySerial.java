package com.bibler.awesome.bibnesburner.serialutils;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;

import com.bibler.awesome.bibnesburner.interfaces.Notifiable;
import com.bibler.awesome.bibnesburner.interfaces.Notifier;

/**
 *
 * @author mario
 */
public class MySerial implements Notifier, Runnable {

	private InputStream in = null;
	private OutputStream out = null;
	private SerialPort serialPort = null;
	private BufferedWriter serialWriter = null;
	private BufferedReader serialReader = null;
	private boolean running;
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();

	public MySerial() {
		in = null;
	    out = null;
	    serialPort = null;
	}
	    
	public InputStream getSerialIn() {
	    return in;
	}
	    
	public OutputStream getSerialOut() {
		return out;
	}
	
	public void registerObjectToNotify(Notifiable objectToNotify) {
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
	}
	
	public void writeString(String stringToWrite) {
		if(serialWriter == null) {
			setupSerialWriter();
		}
		try {
			serialWriter.write(stringToWrite);
			serialWriter.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setupSerialWriter() {
		serialWriter = new BufferedWriter(new OutputStreamWriter(out));
	}
	
	
    /**
     * @return A HashSet containing the CommPortIdentifier for all serial ports
     * that are not currently being used.
     */
    public static HashSet<CommPortIdentifier> getAvailableSerialPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            if (com.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                h.add(com);
            }
        }
        return h;
    }

    public void connect(String portName, int speed) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            throw (new Exception("Error: Port is currently in use"));
        } else {
            CommPort commPort = portIdentifier.open("MEEPROMMER", 2000);
            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(speed, SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                serialPort.enableReceiveThreshold(1);
                serialPort.enableReceiveTimeout(2000);
                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();
                serialReader = new BufferedReader(new InputStreamReader(in));
                System.out.println("Connected to " + serialPort);
                Thread t = new Thread(this);
                running = true;
                t.start();

            } else {
                throw (new Exception("Error: Only serial ports are handled by this example."));
            }
        }
    }

    public void disconnect() throws Exception {
        // do io streams need to be closed first?
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        in = null;
        out = null;

        if (serialPort != null) {
            serialPort.close(); // close the port
        }
        serialPort = null;
    }
    
    public boolean isConnected() {
        return (serialPort != null);
    }

	@Override
	public void notifyAllObjects(String message) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(this, message);
		}
		
	}

	@Override
	public void run() {
		while(running) {
			try {
				if(serialReader.ready()) {	
					String message = serialReader.readLine();
					notifyAllObjects(message);
				}
			} catch (IOException e) {}
		}	
	}
}
