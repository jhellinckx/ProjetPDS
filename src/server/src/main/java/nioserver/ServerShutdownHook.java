package nioserver;
import org.calorycounter.shared.Constants;

import java.util.List;
import java.util.LinkedList;


public class ServerShutdownHook implements Runnable{
	private AbstractNIOServer _server;

	public ServerShutdownHook(AbstractNIOServer server){
		this._server = server;
	}

	public void run(){
		System.out.println("Shutting down server. Please wait...");
		this._server.stop();
		System.out.println("Server shutted down "+Constants.color.GREEN+"successfully " +Constants.color.RESET+"!");
	}
}