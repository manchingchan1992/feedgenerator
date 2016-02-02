import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FeedGenerator {
	private static ArrayList<CertificateUpdate> certificateUpdateList;
	private static Hashtable<String, String> charConverTable = new Hashtable<String, String>();
	private static FileWriter writer;
	public static void main(String args[]) throws IOException{
		long startTime = System.currentTimeMillis();

		String str_num_of_cert = args[0];
		String str_num_of_price_upd = args[1];
		String str_num_of_threads = args[2];
		String outputFilename = args[3];

		int num_of_cert = Integer.parseInt(str_num_of_cert);
		int num_of_price_upd = Integer.parseInt(str_num_of_price_upd);
		int num_of_threads = Integer.parseInt(str_num_of_threads);

		System.out.println("num_of_cert:"+num_of_cert+
				" num_of_price_upd:"+num_of_price_upd+
				" num_of_threads:"+num_of_threads+
				" outputFilename:"+outputFilename);
		initCharTable();

		generateInputs(num_of_cert, num_of_price_upd);
		generateFeed(certificateUpdateList.size(),num_of_threads,outputFilename);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("elapsedTime:"+elapsedTime);
	}

	public static void generateInputs(int num_of_cert, int num_of_price_upd){
		if (certificateUpdateList != null){
			certificateUpdateList.clear();
		}
		if (certificateUpdateList == null){
			certificateUpdateList = new ArrayList<CertificateUpdate>();
		}

		for (int i = 0 ; i < num_of_cert; i++){
			CertificateUpdate obj = new CertificateUpdate();
			obj.setTimestamp(System.currentTimeMillis());
			obj.setCurrency("EUR");
			BigDecimal prev_bidPrice = obj.getBidPrice();
			BigDecimal prev_askPrice = obj.getAskPrice();

			BigDecimal newBidPrice = generatePrice(prev_askPrice, prev_bidPrice, false);
			BigDecimal newAskPrice = generatePrice(prev_askPrice, newBidPrice, true);
			obj.setBidPrice(newBidPrice);
			obj.setAskPrice(newAskPrice);
			obj.setBidSize(generateSize()); 
			obj.setAskSize(generateSize());
			obj.setISIN(generateISIN());
			certificateUpdateList.add(obj);
			for (int j  = 1; j<num_of_price_upd; j++){
				CertificateUpdate tempObj = new CertificateUpdate();
				tempObj.setTimestamp(System.currentTimeMillis());
				tempObj.setISIN(obj.getISIN());
				tempObj.setCurrency(obj.getCurrency());
				BigDecimal prevbidPrice = obj.getBidPrice();
				BigDecimal prevaskPrice = obj.getAskPrice();

				BigDecimal tempNewBidPrice = generatePrice(prevaskPrice, prevbidPrice, false);
				BigDecimal tempNewAskPrice = generatePrice(prevaskPrice, tempNewBidPrice, true);
				//				System.out.println("tempNewAskPrice-tempNewBidPrice:"+tempNewAskPrice.subtract(tempNewBidPrice));
				//				if (tempNewAskPrice.subtract(tempNewBidPrice).compareTo(new BigDecimal("5.00")) == 1)
				//					System.err.println("Bug!!!!");
				//				System.out.println("tempNewAskPrice-prevaskPrice:"+tempNewAskPrice.subtract(prevaskPrice));
				//				if (tempNewAskPrice.subtract(prevaskPrice).compareTo(new BigDecimal("10.00")) == 1 ||
				//						tempNewAskPrice.subtract(prevaskPrice).compareTo(new BigDecimal("-10.00")) == -1)
				//					System.err.println("Bug!!!!");
				//				System.out.println("tempNewBidPrice-prevbidPrice:"+tempNewBidPrice.subtract(prevbidPrice));
				//				if (tempNewBidPrice.subtract(prevbidPrice).compareTo(new BigDecimal("10.00")) == 1 ||
				//						tempNewBidPrice.subtract(prevbidPrice).compareTo(new BigDecimal("-10.00")) == -1)
				//					System.err.println("Bug!!!!");

				tempObj.setBidPrice(tempNewBidPrice);
				tempObj.setAskPrice(tempNewAskPrice);
				tempObj.setBidSize(generateSize()); 
				tempObj.setAskSize(generateSize());
				obj = tempObj;
				certificateUpdateList.add(obj);
			}
		}
	}

	public static BigDecimal generatePrice(BigDecimal prev_askPrice, BigDecimal prev_bidPrice, boolean getAskPrice ){
		BigDecimal hundred = new BigDecimal("100.00");
		BigDecimal twohundred = new BigDecimal("200.00");
		BigDecimal ten = new BigDecimal("10.00");
		BigDecimal zero = new BigDecimal("0.00");
		BigDecimal five = new BigDecimal("5.00");
		BigDecimal price = zero;

		if (getAskPrice){
			BigDecimal lowerLimit = (prev_bidPrice.compareTo(hundred) == 1)? prev_bidPrice: hundred;
			if (prev_bidPrice.compareTo(hundred) == 1){
				BigDecimal temp = ((prev_askPrice.subtract(ten)).compareTo(prev_bidPrice.subtract(five)) == 1) ? (prev_askPrice.subtract(ten)) : prev_bidPrice.subtract(five) ;
				lowerLimit = temp;
			}
			lowerLimit = (lowerLimit.compareTo(hundred) == 1)? lowerLimit: hundred;

			BigDecimal upperLimit = zero ;
			if (prev_bidPrice.compareTo(hundred) == 1){
				BigDecimal askBidSpread = prev_bidPrice.add(five);
				BigDecimal temp = prev_askPrice.add(ten);
				if ( prev_askPrice.compareTo(zero) == 1)
					upperLimit = askBidSpread.compareTo(temp) == -1 ? askBidSpread: temp;
				else upperLimit = askBidSpread;
			}
			upperLimit = (upperLimit.compareTo(hundred) != -1 && upperLimit.compareTo(twohundred) == -1) ? upperLimit : twohundred ;
			price = BigDecimal.valueOf((Math.random()*100.00/100.00)).multiply(upperLimit.subtract(lowerLimit)).add(lowerLimit);
			//			System.out.println("GetAskprice:"+getAskPrice
			//					+"prev_askPrice:"+prev_askPrice+"prev_bidPrice:"+prev_bidPrice
			//					+"upperLimit:"+upperLimit+" lowerLimit:"+lowerLimit+" price:"+price);
		}
		else {
			BigDecimal lowerLimit = prev_bidPrice.subtract(ten).compareTo(hundred) == 1? prev_bidPrice.subtract(ten) : hundred;

			BigDecimal upperLimit = zero ;
			if (prev_bidPrice.compareTo(hundred) == 1){
				BigDecimal temp = prev_bidPrice.add(ten);
				upperLimit = temp;
			}
			upperLimit = (upperLimit.compareTo(hundred) !=-1 && upperLimit.compareTo(twohundred) == -1) ? upperLimit : twohundred ;
			price = BigDecimal.valueOf((Math.random()*100/100)).multiply(upperLimit.subtract(lowerLimit)).add(lowerLimit);
			//			System.out.println("GetAskprice:"+getAskPrice
			//					+"prev_askPrice:"+prev_askPrice+"prev_bidPrice:"+prev_bidPrice
			//					+"upperLimit:"+upperLimit+" lowerLimit:"+lowerLimit+" price:"+price);
		}
		return price.setScale(2, RoundingMode.CEILING);
	}

	public static long generateSize(){
		return (long)(Math.random()*10000);
	}

	public static String generateISIN(){
		String ISIN = "D"+"E";
		String[] countryCode = {charConverTable.get("D"),charConverTable.get("E")};
		int[] ISINDigit = new int[9];
		for (int i = 0; i<ISINDigit.length; i++ ){
			int temp = (int)(Math.random()*10);
			ISIN+=temp;
			ISINDigit[i]=temp;
		}
		int sum = 0;
		for (int i = ISINDigit.length -1; i>=0; i-- ){
			int temp = ISINDigit[i];
			if (i%2 == 0){
				temp = temp*2;
				if (temp > 9){
					int tenthDigit = 1;
					int unitDigit = temp -10;
					sum +=unitDigit +tenthDigit ;
				}
				else 
					sum +=temp;
			}
			else
				sum +=temp;
		}
		for (int i = countryCode.length -1; i>=0; i--){
			String temp = countryCode[i];
			int intTemp = Integer.parseInt(temp);
			int tenthDigit = (intTemp/10)%10;
			int unitDigit = intTemp%10;
			tenthDigit= tenthDigit*2;
			sum +=unitDigit +tenthDigit ;
		}
		//		System.out.println("Sum:"+sum);
		int checkDigit = 0;
		if (sum%10 != 0){
			int numberEndingZero = (int)(Math.ceil((double)sum/(double)10)*10);
			checkDigit = numberEndingZero - sum;
		}
		ISIN+=checkDigit;
		//		System.out.println("ISIN:"+ISIN);
		return ISIN;
	}

	public static void initCharTable(){
		charConverTable.put("A", "10");
		charConverTable.put("B", "11");
		charConverTable.put("C", "12");
		charConverTable.put("D", "13");
		charConverTable.put("E", "14");
		charConverTable.put("F", "15");
		charConverTable.put("G", "16");
		charConverTable.put("H", "17");
		charConverTable.put("I", "18");
		charConverTable.put("J", "19");
		charConverTable.put("K", "20");
		charConverTable.put("L", "21");
		charConverTable.put("M", "22");
		charConverTable.put("N", "23");
		charConverTable.put("O", "24");
		charConverTable.put("P", "25");
		charConverTable.put("Q", "26");
		charConverTable.put("R", "27");
		charConverTable.put("S", "28");
		charConverTable.put("T", "29");
		charConverTable.put("U", "30");
		charConverTable.put("V", "31");
		charConverTable.put("W", "32");
		charConverTable.put("X", "33");
		charConverTable.put("Y", "34");
		charConverTable.put("Z", "35");
	}

	public static void generateFeed(int num_of_line, int num_of_threads, String outputFilename) throws IOException{
		File feed = new File(outputFilename);
		feed.createNewFile();
		writer = new FileWriter(feed); 
		BlockingQueue queue = new LinkedBlockingQueue();
		WorkerThread[] workers = new WorkerThread[num_of_threads];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new WorkerThread(queue);
			workers[i].start();
		}
		for (CertificateUpdate cert : certificateUpdateList){
			try {
				queue.put(cert);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < workers.length; i++) {
			try {
				//stop queue
				queue.put(Boolean.valueOf(false));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		boolean isStop = false;
		while (!isStop){
			boolean isRunning = false;
			for (int i = 0; i < workers.length; i++) {
			      if (workers[i].getRunning()){
			    	  isRunning = true;
			      }
			}
			isStop = !isRunning;
		}
	}

	public static void readFeed(CertificateUpdate cert){
		synchronized(writer){
			try {
				writer.write(cert.toString()+"\n");
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}