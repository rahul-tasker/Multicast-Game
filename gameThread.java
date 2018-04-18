import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class gameThread implements Runnable {
	String multicast;
	int port;

	//	peerUtilites peerU;
	public gameThread(int port, String multicast){
		
		this.port = port;
		this.multicast = multicast;
		//		this.peerU = peerU;
	}
	public void run(){
		try{
			while(true) {

				String IPadrress = multicast;
				System.setProperty("java.net.preferIPv4Stack", "true");  
				@SuppressWarnings("resource")
				MulticastSocket socket = new MulticastSocket(port);
				int ttl = 64;
				socket.setTimeToLive(ttl);
				InetAddress group = InetAddress.getByName(IPadrress);
				InetAddress IP=InetAddress.getLocalHost();  
				socket.setInterface(IP);
				group = InetAddress.getByName(IPadrress);
				socket.joinGroup(group);
				byte[] rbuf = new byte[256];  
				DatagramPacket packet = new DatagramPacket(rbuf,rbuf.length);
				socket.receive(packet); //receive it
				ByteArrayInputStream bin = new ByteArrayInputStream (packet.getData(), 0, packet.getLength() );
				BufferedReader reader = new BufferedReader (new InputStreamReader ( bin ) );
				String line = reader.readLine(); 
				
				if (line.equals("2")) {
					System.out.println("PLAYER 2: " + reader.readLine());
				}
				else{ //can't figure pout why it wont read player 1
					System.out.println("PLAYER 1: " + reader.readLine());	
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}	
}
