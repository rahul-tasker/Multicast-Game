import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Scanner;

public class Chat_Room {

	
	public static void prompt(String server, String port) throws Exception {
		DatagramSocket clientSocket = new DatagramSocket(); //for network
		BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in)); //from keyboard

		InetAddress serverAddress = InetAddress.getByName(server);  // Server address
		int servPort = Integer.parseInt(port);  //get port number

		String clientInput;  //from user
		String echoString = "";   //echo from server
		System.out.println("Please enter 'Chat' to recieve a list of chat groups");

//		while(true) {
			clientInput = fromKeyboard.readLine();

			byte[] buf=clientInput.getBytes();  //byte array for data to SEND to server
			//			System.out.println("FROM CONSOLE (size of input string is " + buf.length + "): " + clientInput);

			//create a UDP (datagram) packet TO send to server
			DatagramPacket packet = new DatagramPacket(buf,buf.length, serverAddress, servPort);
			clientSocket.send(packet);  //now send it
			byte[] rbuf = new byte[256];  //new byte array to RECEIVE from server
			packet = new DatagramPacket(rbuf,rbuf.length);  //UDP packet to receive FROM server
			clientSocket.receive(packet); //receive it
			echoString = new String(packet.getData(),0,packet.getLength());
			System.out.println(echoString);
	}
	

	public static void main(String[] args) throws Exception {
		if ((args.length != 4) ) { // Test for correct # of args
			throw new IllegalArgumentException("Parameter(s): <Server> <Port> <Multicast Address> <Multicast Port>");
		}
		prompt(args[0], args[1]);
		System.out.println("select a game room by entering the multicase and port number on seperate lines");
		System.out.println("when you want to switch to another room, enter 'Done'");
		Scanner scanner = new Scanner(System.in);
		String multicast = scanner.nextLine();
		int port = Integer.parseInt(scanner.nextLine());

		peerSendThread PST;
		peerReceiveThread PRT;
		int PORT = Integer.parseInt(args[3]);
		peerUtilites peerU = new peerUtilites(args[2],PORT);
		
		peerU.setName();
		peerU.joinGroup();
		PST = new peerSendThread(peerU, args);
		Thread T = new Thread(PST);
		T.start();
		
		PRT = new peerReceiveThread(peerU);
		Thread TT = new Thread(PRT);
		TT.setDaemon(true);
		TT.start();
		
		gameThread gameThread = new gameThread(port, multicast);
		Thread gameReader = new Thread(gameThread);
		gameReader.setDaemon(true);	
		gameReader.start();
		
		T.join();	
		gameReader.join();	
	}

}
