import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Game_Client {

	public static boolean isInteger(String s) {
		boolean isValidInteger = false;
		try {
			Integer.parseInt(s);
			// s is a valid integer
			isValidInteger = true;
		}
		catch (NumberFormatException ex) {
			// s is not an integer
		}
		return isValidInteger;
	}

	public static void main(String[] args) throws Exception {
		String multicast_address = "";
		int port_number = 0;
		int player_number = 0;
		boolean isDone = false;

		if ((args.length != 2)) { // Test for correct # of args
			throw new IllegalArgumentException("Parameter(s): <Server> <Port>");
		}
		DatagramSocket clientSocket = new DatagramSocket(); // for network
		BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in)); // from keyboard
		InetAddress serverAddress = InetAddress.getByName(args[0]); // Server address
		int servPort = Integer.parseInt(args[1]); // get port number

		String clientInput; // from user
		System.out.println("Please enter your name to get started and enter done when finished playing");

		while (multicast_address=="" && port_number==0 && player_number == 0) {
			clientInput = fromKeyboard.readLine();
			if(clientInput.equals("done")) {
				isDone = true;
			}

			byte[] buf = clientInput.getBytes(); // byte array for data to SEND

			// create a UDP (datagram) packet TO send to server
			DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, servPort);
			clientSocket.send(packet); // now send it
			byte[] rbuf = new byte[256]; // new byte array to RECEIVE from
			// server
			packet = new DatagramPacket(rbuf, rbuf.length); // UDP packet to

			clientSocket.receive(packet); // receive it
			ByteArrayInputStream bin = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
			BufferedReader reader = new BufferedReader(new InputStreamReader(bin));
			multicast_address = reader.readLine();
			port_number = Integer.parseInt(reader.readLine());
			player_number = Integer.parseInt(reader.readLine());
		}
		clientSocket.close();
		while(true){
			if(isDone==true) {
				DatagramSocket cs = new DatagramSocket(); //for network
				InetAddress sa = InetAddress.getByName(args[0]);  // Server address
				int sp = Integer.parseInt(args[1]);  //get port number
				String cin;  //from user
				//				String echoString;   //echo from server
				System.out.println("Thank you for playing");
				cin = "Done";
				byte[] buf=cin.getBytes();  //byte array for data to SEND to server
				//create a UDP (datagram) packet TO send to server
				DatagramPacket packet = new DatagramPacket(buf,buf.length, sa, sp);
				cs.send(packet);  //now send it
				
			}
			else if(player_number == 1) {
				String IPadrress = multicast_address;
				System.setProperty("java.net.preferIPv4Stack", "true");  
				@SuppressWarnings("resource")
				MulticastSocket socket = new MulticastSocket(port_number);
				int ttl = 64;
				socket.setTimeToLive(ttl);
				InetAddress group = InetAddress.getByName(IPadrress);
				InetAddress IP=InetAddress.getLocalHost();  
				socket.setInterface(IP);
				group = InetAddress.getByName(IPadrress);
				socket.joinGroup(group);
				BufferedReader fromKeyboard2 = new BufferedReader(new InputStreamReader(System.in));
				String output = "1" + System.lineSeparator();
				int num = 0;

				System.out.println("Choose a number between 1 and 50");
				String in = fromKeyboard2.readLine();
				System.out.println("Wait for player 2 to guess correctly... ");

				if(isInteger(in)){
					num = Integer.parseInt(in);
				}
				else if(in.equals("exit")){
					isDone = true;
					break;
				}
				else {
					output += "enter valid integer";
				}

				while(num!=0){
					//process a datagram
					output = "1" + System.lineSeparator();
					byte[] rbuf = new byte[256];
					byte[] sbuf = new byte [256];

					boolean toBreak = false;
					DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
					socket.receive(packet);

					// Obtain a byte input stream to really read the UDP packet
					ByteArrayInputStream bin = new ByteArrayInputStream (packet.getData(), 0, packet.getLength() );
					// Connect a reader for easier access
					BufferedReader reader = new BufferedReader (new InputStreamReader ( bin ) );
					String line = reader.readLine();  //we can read a string, since Reader has a method for char reading
					if(line.equals("2")){
						line = reader.readLine(); 
						//If it is not an integer, it will process as a name
						if (isInteger(line)) {
							int guess = Integer.parseInt(line);
							if(num > guess) {
								output += "Too low";
							}
							else if (num < guess) {
								output += "Too high";
							}
							else {
								output += "Correct!";
								player_number = 2;
								toBreak = true;
							}
						}
						else if(line.equals("exit")) {
							isDone=true;
							break;
						}
						else {
							output += "Enter a valid integer. You entered: " + line;	
						}
						InetAddress address = InetAddress.getByName(multicast_address);
						sbuf =  output.getBytes();
						//create a datagram packet
						DatagramPacket sendPacket = new DatagramPacket(sbuf, sbuf.length, address, port_number);
						socket.send(sendPacket);
						if(toBreak){
							break;
						} 
					}
				}
			}
			else if(player_number==2){ //guesser
				String IPadrress = multicast_address;
				System.setProperty("java.net.preferIPv4Stack", "true");  
				@SuppressWarnings("resource")
				MulticastSocket socket = new MulticastSocket(port_number);
				int ttl = 64;
				socket.setTimeToLive(ttl);
				InetAddress group = InetAddress.getByName(IPadrress);
				InetAddress IP=InetAddress.getLocalHost();  
				socket.setInterface(IP);
				group = InetAddress.getByName(IPadrress);
				socket.joinGroup(group);
				BufferedReader fromKeyboard1 = new BufferedReader(new InputStreamReader(System.in));

				String clientInput1;  //from user
				String echoString;   //echo from server
				System.out.println("Guess a number between 1 and 50");
				boolean isCorrect = false;
				while(!isCorrect && !isDone) {
					boolean isRead = false;
					clientInput1 = "2" + System.lineSeparator() + fromKeyboard1.readLine();
					if (clientInput1.equals("exit")){  //user want to stop having fun with the server
						isDone=true;
						break;
					}
					byte[] buf=clientInput1.getBytes();  //byte array for data to SEND to server
					//create a UDP (datagram) packet TO send to server
					DatagramPacket packet = new DatagramPacket(buf,buf.length, group, port_number);
					socket.send(packet);  //now send it
					while(isRead==false){
						byte[] rbuf = new byte[256];  //new byte array to RECEIVE from server
						packet = new DatagramPacket(rbuf,rbuf.length);  //UDP packet to receive FROM server
						socket.receive(packet); //receive it
						ByteArrayInputStream bin = new ByteArrayInputStream (packet.getData(), 0, packet.getLength() );
						BufferedReader reader = new BufferedReader (new InputStreamReader ( bin ) );
						String line = reader.readLine();  //we can read a string, since Reader has a method for char reading
						if(line.equals("1")){ //coming from player 1
							isRead=true;
							line = reader.readLine(); 
							if(line.equals("Correct!")){
								System.out.println(line);
								player_number=1;
								isCorrect=true;
								break;
							}else if(line.equals("exit")) {
								isDone=true;
								break;
							}
							System.out.println(line);
						}
					}
				} 
				socket.close();
			}
		}
	}
}