


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class peerUtilites{
	private MulticastSocket socket;
	private int PORT;
	private int ttl = 64; /* time to live */
	private InetAddress group; 
	private String IPadrress;
	private String name;
	
	public peerUtilites(String IPadrress,int PORT)throws Exception{
		this.PORT = PORT;
		this.IPadrress = IPadrress;
		/* instantiate a MulticastSocket */
		System.setProperty("java.net.preferIPv4Stack", "true");  //IMPORTANT IMPORTANT IMPORTANT
		this.socket = new MulticastSocket(PORT);
		/* set the time to live */
		socket.setTimeToLive(ttl);
		this.group = InetAddress.getByName(IPadrress);
	}
	public void setName() throws Exception {
	    System.out.print("Please enter a screen name by ");
		name = readFromKeyboard();
	}

	public String getName() throws Exception {
	    return name;
	}
	
	public void joinGroup() throws Exception {
		//IMPORTANT IMPORTANT IMPORTANT
		//the following two lines are needed for MACs and BC
		InetAddress IP=InetAddress.getLocalHost();  //now this is what is called the nat address, and should work for us since we are only talking to machines on our segment
		socket.setInterface(IP);
		group = InetAddress.getByName(IPadrress);
		socket.joinGroup(group);
	}
	public void leaveGroup() throws Exception {
		socket.leaveGroup(group);
		socket.close();
	}

	public  String readFromKeyboard() throws Exception {
		BufferedReader stdin; /* input from keyboard */
		String sendString; /* string to be sent */
		stdin = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter text: ");
		sendString = stdin.readLine();
		return sendString;
	}
	
	public void sendToSocket(String msg) throws Exception{
		/* remember to convert keyboard input (in msg) to bytes */
		 DatagramPacket sendPacket = new DatagramPacket(msg.getBytes(), msg.length(),group, PORT);
		 socket.send(sendPacket);
	}
	
	public String readFromSocket() throws Exception{
		String socketString = null; /* string from socket */
		// get their responses!
		//byte[] buf is a byte array from the socket
		 byte[] buf = new byte[1000];
		 DatagramPacket recv = new DatagramPacket(buf, buf.length);
		 socket.receive(recv);
		 socketString = new String(recv.getData(), 0, recv.getLength());
		return 	socketString;	
	}
	public void sendToTerminal(String msg) throws Exception{
		//System.out.println("Multicast text: " + msg);
		System.out.println(msg);
	}
}

