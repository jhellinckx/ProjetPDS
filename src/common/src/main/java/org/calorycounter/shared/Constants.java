package org.calorycounter.shared;

import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.net.InetAddress;

public final class Constants {
	public static final int PORT = 2015;
	public static final String HOST = "127.0.0.1";
	public static final String EMULATOR_DEVICE_ADDRESS = "10.0.2.2";
	public static final int MAX_CLIENTS = 100;
	public static final int BUFFER_SIZE = 1000;
	public static final int INT_SIZE = 4;
	public static final Charset ENCODING = StandardCharsets.UTF_8;
	public static final boolean SHOW_LOG = true;

	public static final String OC_RESET = "\u001B[0m";
	public static final String OC_BLACK = "\u001B[30m";
	public static final String OC_RED = "\u001B[31m";
	public static final String OC_GREEN = "\u001B[32m";
	public static final String OC_YELLOW = "\u001B[33m";
	public static final String OC_BLUE = "\u001B[34m";
	public static final String OC_PURPLE = "\u001B[35m";
	public static final String OC_CYAN = "\u001B[36m";
	public static final String OC_WHITE = "\u001B[37m";

	public static final String O_IN = OC_CYAN + " >> " + OC_RESET;
	public static final String O_OUT = OC_PURPLE + " << " + OC_RESET;

	public static String localhost(){
		try{
			return InetAddress.getLocalHost().getHostAddress();
		}catch(Exception e){
			return "";
		}
	}

	public static String repr(Object obj){
		return "[" + obj.getClass().getName() + "]";
	}

	public static String errorMessage(String msg, Object obj){
		return repr(obj) + OC_RED + " Error : " + msg + OC_RESET;  
	}

	public static String errorMessage(String msg, String str){
		return "[" + str +"]" + OC_RED + " Error : " + msg + OC_RESET;  
	}
}