package nioserver;

import java.util.List;
import java.util.LinkedList;


public class ServerShutdownHook implements Runnable{
	private AbstractNIOServer _server;
	private StreamController _controller;
	private List<Thread> _threads;

	public ServerShutdownHook(AbstractNIOServer server, StreamController controller){
		this._server = server;
		this._controller = controller;
		this._threads = new LinkedList<Thread>();
		this._threads.add(Thread.currentThread());
		this._threads.add(controller.inThread());
		this._threads.add(controller.outThread());
	}

	public void run(){
		System.out.println("Shutting down server. Please wait...");
		this._server.stop();
		for(Thread thread : _threads){
			try{
				thread.join();
			}
			catch(InterruptedException e){}
		}
		this._controller.disconnectAll();
		System.out.println("Server shutted down "+Constants.OC_GREEN+"successfully " +Constants.OC_RESET+"!");
	}
}