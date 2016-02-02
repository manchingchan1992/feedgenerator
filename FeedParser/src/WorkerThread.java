import java.util.concurrent.BlockingQueue;

public class WorkerThread extends Thread {
	private BlockingQueue q;
	private boolean isRunning = true;

	WorkerThread(BlockingQueue q) {
		this.q = q;
	}

	public void run() {
		try {
			while (isRunning) {
				Object obj = q.take();
				if (obj == null) {
					isRunning = false;
					break;
				}
				if (obj.equals(false)) {
					isRunning = false;
					break;
				}
				if (obj instanceof String){
					FeedParser.parseCertList((String)obj);
				}
				if (obj instanceof ExceptionMessage){
					FeedParser.parseExceptionList((ExceptionMessage)obj);
				}
			}
		} catch (InterruptedException e) {
		}
	}
	
	public boolean getRunning() {
	    return this.isRunning;
	}
	
	public void setRunning(boolean isRunning) {
	    this.isRunning = isRunning;
	}
}