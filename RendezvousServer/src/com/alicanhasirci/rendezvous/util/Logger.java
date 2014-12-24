package com.alicanhasirci.rendezvous.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alicanhasirci.rendezvous.main.Server;

public class Logger {

    public final static String LOG_FILE = "RendezvousServer.log";

    private PrintWriter logWriter;
    private Object logMutex;
    private SimpleDateFormat logFormatter;

    public Logger() {
    	if(!new File(LOG_FILE).isFile()){
    		new File(LOG_FILE);
    	}
    	
        try {
            logWriter = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE)));
        } catch (IOException e) {
            System.err.println(e);
        }
        logMutex = new Object();
        logFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

        String welcome = "________________________________Rendezvous Server "+Server.VERSION+"________________________________";
        logWriter.write(welcome);
        System.out.println(welcome);
    }

    public final void debug(String message) {
        synchronized (logMutex) {
            logWriter.write(genericLog(message.toString(), "DEBUG"));
            logWriter.flush();
        }
    }
    
    public final void info(String message) {
        synchronized (logMutex) {
            logWriter.write(genericLog(message.toString(), "INFO "));
            logWriter.flush();
        }
    }
    
    public final void error(String message) {
        synchronized (logMutex) {
            logWriter.write(genericLog(message.toString(), "ERROR"));
            logWriter.flush();
        }
    }
    
    public final void error(String message, Exception exception) {
        synchronized (logMutex) {
            logWriter.write(genericLog(message, "ERROR"));
            exception.printStackTrace(logWriter);
            logWriter.flush();
        }
    }
    
    private final String genericLog(String message, String level) {
        StringBuffer buf = new StringBuffer();
        buf.append(logFormatter.format(new Date()));
        buf.append(" ");
        buf.append(level);
        buf.append(" [");
        buf.append(Thread.currentThread().getName());
        buf.append("] ");
        buf.append(message);
        buf.append("\n");
        System.out.println(buf.toString());
        return buf.toString();
    }
}
