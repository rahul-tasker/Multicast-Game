
public class peerReceiveThread implements Runnable{
	peerUtilites peerU;
	public peerReceiveThread(peerUtilites peerU){
		this.peerU = peerU;
	}
	public void run(){
		String fromSocket = null;
		try {
			while (true) {
				fromSocket = peerU.readFromSocket();
				//System.out.println(Thread.currentThread().getName() + " receiving");
				System.out.println(peerU.getName() + " receiving");
				peerU.sendToTerminal(fromSocket);
			}

		} catch (Exception E) {
		}
	}	
}
