


public class peerSendThread implements Runnable {
	peerUtilites peerU;
	String[] args;
	
	public peerSendThread(peerUtilites peerU, String[] args) {
		this.peerU = peerU;
		this.args = args;
		
	}

	public void run() {
		String fromKeyboard = null;
		String message = null;
		try {
			while (!(fromKeyboard = peerU.readFromKeyboard()).equalsIgnoreCase("bye") ) {
				//System.out.println(Thread.currentThread().getName() + " sending");
				message = peerU.getName() + " sent: " + fromKeyboard;
				//System.out.println(peerU.getName() + " sending");
				peerU.sendToSocket(message);
				//fromSocket = peerU.readFromSocket();
				//peerU.sendToTerminal(fromSocket);
				if(peerU.readFromKeyboard().equals("Done"))
					peerU.leaveGroup();
					Chat_Room.main(args);
			}

		} catch (Exception E) {
		}
	}
}

