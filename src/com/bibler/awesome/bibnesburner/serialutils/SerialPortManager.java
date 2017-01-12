package com.bibler.awesome.bibnesburner.serialutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class SerialPortManager implements Runnable {

	private SerialPortInstance currentSerialPort;
	/**
     * @return A HashSet containing the CommPortIdentifier for all serial ports
     * that are not currently being used.
     */
    public HashSet<CommPortIdentifier> getAvailableSerialPorts() {
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
    
    public String[] getAvailableSerialPortNames() {
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> names = new ArrayList<String>();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            if (com.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                names.add(com.getName());
            }
        }
        return names.toArray(new String[names.size()]);
    }
    
    public void connect(String portName, int speed) throws Exception {
        SerialPort serialPort = null;
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
                
                System.out.println("Connected to " + serialPort);
                Thread t = new Thread(this);
                t.start();

            } else {
                throw (new Exception("Error: Only serial ports are handled by this example."));
            }
        }
        currentSerialPort = new SerialPortInstance(serialPort);
    }

    /*public void disconnect() throws Exception {
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
    }*/
    
    @Override
	public void run() {
		
	}

	public SerialPortInstance getActiveSerialPort() {
		return currentSerialPort;
	}
}
