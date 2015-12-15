package nioserver;
import org.calorycounter.shared.Constants;
import dao.DAOFactory;
import dao.UserDAO;
import dao.FoodDAO;
import java.util.HashMap;
import java.util.Map;
import java.net.Socket;


import org.json.simple.JSONObject;
import java.io.IOException;

public abstract class AbstractNIOServer{
	private StreamController _controller;
	private Boolean _running;
	private DAOFactory _daoFactory;
	private HashMap<Socket, String> _clients;

	protected UserDAO _userDatabase;
	protected FoodDAO _foodDatabase;

	public AbstractNIOServer(){
		_controller = new StreamController(this);
		_running = new Boolean(false);
		_daoFactory = DAOFactory.getInstance();
		_userDatabase = _daoFactory.getUserDAO();
		_foodDatabase = _daoFactory.getFoodDAO();
		_clients = new HashMap<Socket, String>();
	}

	public void addClient(String name, Socket socket){
		if(!_clients.containsKey(socket)){
			_clients.put(socket, name);
		}
	}

	public void addClient(String name, Message msg){
		Socket socket = msg.socket();
		if(!_clients.containsKey(socket)){
			_clients.put(socket, name);
		}
	}

	public void removeClient(Message msg){
		_clients.remove(msg.socket());
	}

	public void removeClient(Socket socket){
		_clients.remove(socket);
	}

	public boolean isConnected(String name){
		return _clients.containsValue(name);
	}

	public void send(Message msg){
		this._controller.addOutgoingMessage(msg);
	}

	public void listen(){
		while(this.isRunning()){
			try{
				Message msg = this._controller.receiveIncomingMessage();
				this.handleMessage(msg);
			}
			catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
			catch(IOException e){
				System.out.println(Constants.errorMessage(e.getMessage(), this));
			}
		}
	}

	public abstract void handleMessage(Message msg) throws IOException;

	public void run(){
		this.setRunning();
		_controller.startStreams();
		Runtime.getRuntime().addShutdownHook(new Thread(new ServerShutdownHook(this, this._controller)));
		System.out.println("Server " + Constants.color.GREEN + Constants.network.HOST + Constants.color.RESET
			+" launched. Listening on port " + Constants.color.YELLOW + Constants.network.PORT + Constants.color.RESET + ".");
		this.listen();
	}

	public void setRunning() { 
		synchronized(this._running){
			this._running = true; 
		}
	}

	public boolean isRunning() { 
		synchronized(this._running){
			return this._running; 
		}
	}

	public void stop() { 
		synchronized(this._running){
			this._running = false;
		}
		this._controller.wakeupIn();
		this._controller.stopStreams();
	}
}