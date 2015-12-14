package org.calorycounter.shared;

import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.net.InetAddress;

public final class Constants {

	/* Network related constants, used to normalise communication */
	public static final class network {
		public static final String REQUEST_TYPE = "requesttype";
		public static final String DATA = "data";
		public static final String CONNECTION_STATUS = "co_status";
		public static final String CONNECTION_SUCCESS = "co_success";
		public static final String CONNECTION_FAILURE = "co_failure";
		public static final int PORT = 2015;
		public static final String HOST = "127.0.0.1";
		public static final String EMULATOR_DEVICE_ADDRESS = "10.0.2.2";
		public static final int MAX_CLIENTS = 100;
		public static final int BUFFER_SIZE = 1000;
		public static final int INT_SIZE = 4;
		public static final Charset ENCODING = StandardCharsets.UTF_8;


		public static String localhost(){
			try{
				return InetAddress.getLocalHost().getHostAddress();
			}catch(Exception e){
				return "";
			}
		}
	}

	/* Console color output, used for logging */
	public static final class color{
		public static final String RESET = "\u001B[0m";
		public static final String BLACK = "\u001B[30m";
		public static final String RED = "\u001B[31m";
		public static final String GREEN = "\u001B[32m";
		public static final String YELLOW = "\u001B[33m";
		public static final String BLUE = "\u001B[34m";
		public static final String PURPLE = "\u001B[35m";
		public static final String CYAN = "\u001B[36m";
		public static final String WHITE = "\u001B[37m";
	}

	
	/* Misc */
	public static final boolean SHOW_LOG = true;
	public static final String IN = color.CYAN + " >> " + color.RESET;
	public static final String OUT = color.PURPLE + " << " + color.RESET;


	public static String repr(Object obj){
		return "[" + obj.getClass().getName() + "]";
	}

	public static String errorMessage(String msg, Object obj){
		return repr(obj) + color.RED + " Error : " + msg + color.RESET;  
	}

	public static String errorMessage(String msg, String str){
		return "[" + str +"]" + color.RED + " Error : " + msg + color.RESET;  
	}
}