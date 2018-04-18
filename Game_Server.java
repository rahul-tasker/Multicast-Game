import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Game_Server {

	public static void main(String argv[]) throws IOException {
		ArrayList<ArrayList<String>> addresses_in_use = new ArrayList<ArrayList<String>>();

		System.out.println("Server Running...");
		if (argv.length != 1) { // Test for correct argument list
			throw new IllegalArgumentException("Parameter(s): <Port>");
		}

		int servPort = Integer.parseInt(argv[0]);
		String output = "";
		String fred;
		int count = 1;
		DatagramSocket serverSocket = new DatagramSocket(servPort);
		int port_to_use = servPort+1;


		// process a datagram
		byte[] rbuf = new byte[256];
		byte[] sbuf = new byte[256];

		while (true) {

			DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
			serverSocket.receive(packet);
			// Obtain a byte input stream to really read the UDP packet
			ByteArrayInputStream bin = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
			// Connect a reader for easier access
			BufferedReader reader = new BufferedReader(new InputStreamReader(bin));
			String line = reader.readLine(); 

			if (line.equals("Done")) {
				// void multicast group
				String address = reader.readLine();
				for (int i = 0; i < addresses_in_use.size() - 1; i++) {
					if (addresses_in_use.get(i).get(0).equals(address)) {
						ArrayList<String> sublist = addresses_in_use.get(i);
						sublist.set(1, "0");
						addresses_in_use.set(i, sublist);

					}
				}
				System.out.println(addresses_in_use);
			} 
			else if (line.equals("Chat")){
				for(int i=0; i<addresses_in_use.size(); i++) {
					output += addresses_in_use.get(i).get(0) +"||"+port_to_use+ System.lineSeparator();
				} 
			}
			else {
				if (!addresses_in_use.isEmpty()) {
					for (int i = 0; i < addresses_in_use.size(); i++) {
						// empty multicast group address
						if (!addresses_in_use.get(i).isEmpty()) {
							if (addresses_in_use.get(i).get(1).equals("0")) {
								output = addresses_in_use.get(i).get(0) +System.lineSeparator() + port_to_use + System.lineSeparator()+"1";
								ArrayList<String> sublist = addresses_in_use.get(i);
								sublist.set(1, "1");
								addresses_in_use.set(i, sublist);
								break;
							}
							// one player in a multicast group
							else if (addresses_in_use.get(i).get(1).equals("1")) {
								output = addresses_in_use.get(i).get(0) + System.lineSeparator() + port_to_use +System.lineSeparator()+"2";
								ArrayList<String> sublist = addresses_in_use.get(i);
								sublist.set(1, "2");
								addresses_in_use.set(i, sublist);
								break;
							}
							// reaches the end and is full
							else if (i == addresses_in_use.size() - 1 && addresses_in_use.get(i).get(1).equals("2")) {
								output = "225.0.0." + count + System.lineSeparator() + port_to_use + System.lineSeparator() +"1";
								ArrayList<String> sublist = new ArrayList<String>();
								sublist.add("225.0.0." + count);
								sublist.add("1");
								addresses_in_use.add(sublist);
								count++;
								break;
							}
						}
					}
				}
				else {
					output = "225.0.0." + count + System.lineSeparator() + port_to_use + System.lineSeparator()+"1";
					ArrayList<String> sublist = new ArrayList<String>();
					sublist.add("225.0.0." + count);
					sublist.add("1");
					addresses_in_use.add(sublist);
					port_to_use++;
					count++;
				}
			}

			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			sbuf = output.getBytes();
			// create a datagram packet
			packet = new DatagramPacket(sbuf, sbuf.length, address, port);
			serverSocket.send(packet);
		}

	}
}