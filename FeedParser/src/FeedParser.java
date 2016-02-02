import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FeedParser {
	private static ArrayList<ExceptionMessage> certExceptionList;
	private static Hashtable<String, ArrayList<CertificateUpdate>> certificateUpdateList;
	private static Object lock = new Object();
	private static FileWriter writer;
	static Document doc;
	static Element rootElement;
	static Document exceptdoc;
	static Element exceptrootElement;
	public static boolean workerDone = false;
	public static void main(String args[]) throws IOException, ParserConfigurationException{
		long startTime = System.currentTimeMillis();
		if (certificateUpdateList != null){
			certificateUpdateList.clear();
		}
		if (certificateUpdateList == null){
			certificateUpdateList = new Hashtable<String, ArrayList<CertificateUpdate>>();
		} 
		if (certExceptionList != null){
			certExceptionList.clear();
		}
		if (certExceptionList == null){
			certExceptionList = new ArrayList<ExceptionMessage>();
		}
		String str_num_of_threads = args[0];
		String inputFilename = args[1];
		String priceReportFilename = args[2];
		String exceptionReportFileName = args[3];

		int num_of_threads = Integer.parseInt(str_num_of_threads);
		readFile(inputFilename);
		generateReport(num_of_threads,priceReportFilename, exceptionReportFileName);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("elapsedTime:"+elapsedTime);
	}

	public static void readFile(String inputFilename) throws FileNotFoundException{

		File file = new File(inputFilename);
		Scanner scanner = new Scanner(file);
		try {
			while(scanner.hasNextLine()) {
				String certUpd = scanner.nextLine();
				try {
					String[] certArray = certUpd.split(",");
					if (certArray == null || certArray.length != 7)
						throw new StringIndexOutOfBoundsException();
					CertificateUpdate obj = new CertificateUpdate();
					for (int i = 0; i< certArray.length ; i++){
						switch (i){
						case 0:
							obj.setTimestamp(Long.parseLong(certArray[i]));
							break;
						case 1:
							obj.setISIN(certArray[i]);
							break;
						case 2:
							obj.setCurrency(certArray[i]);
							break;	
						case 3:
							obj.setBidPrice(new BigDecimal(certArray[i]));
							break;
						case 4:
							obj.setBidSize(Long.parseLong(certArray[i]));
							break;
						case 5:
							obj.setAskPrice(new BigDecimal(certArray[i]));
							break;
						case 6:
							obj.setAskSize(Long.parseLong(certArray[i]));
							break;
						}
					}
					ArrayList<CertificateUpdate> updList = certificateUpdateList.get(obj.getISIN());
					if (updList ==null){
						updList = new ArrayList<CertificateUpdate>();
					}
					updList.add(obj);
					certificateUpdateList.put(obj.getISIN(), updList);
//					System.out.println(obj.toString());
				}
				catch (StringIndexOutOfBoundsException stre){
					ExceptionMessage detail = new ExceptionMessage();
					detail.setFeedLine(certUpd);
					detail.setException("Some data in this feedLine is missing");
					certExceptionList.add(detail);
//					stre.printStackTrace();
				}
				catch (NumberFormatException nume){
					ExceptionMessage detail = new ExceptionMessage();
					detail.setFeedLine(certUpd);
					detail.setException("Invalid Data Type in this feedLine");
					certExceptionList.add(detail);
//					nume.printStackTrace();
				}
				catch (Exception e){
					ExceptionMessage detail = new ExceptionMessage();
					detail.setFeedLine(certUpd);
					detail.setException("Invalid Data Format in this feedLine");
					certExceptionList.add(detail);
//					e.printStackTrace();
				}
			}
		}
		catch (Exception e){
//			e.printStackTrace();
		}
		finally {
			scanner.close();
		}

	}

	public static void generateReport(int num_of_threads, String priceFileName, String exceptionReportFileName) throws ParserConfigurationException{
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			Transformer excepttransformer = transformerFactory.newTransformer();
			excepttransformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamResult result = new StreamResult(new File(priceFileName));
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			rootElement = doc.createElement("PriceReport");
			doc.appendChild(rootElement);
			Attr attr = doc.createAttribute("numOfCertificates");
			int no_of_cert = certificateUpdateList.size();

			attr.setValue(String.valueOf(no_of_cert));
			rootElement.setAttributeNode(attr);
			BlockingQueue queue = new LinkedBlockingQueue();
			WorkerThread[] workers = new WorkerThread[num_of_threads];
		    for (int i = 0; i < workers.length; i++) {
		      workers[i] = new WorkerThread(queue);
		      workers[i].start();
		    }
			Enumeration keys = certificateUpdateList.keys();
			while(keys.hasMoreElements()){
				try {
					queue.put(keys.nextElement());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			StreamResult exceptionResult = new StreamResult(new File(exceptionReportFileName));
			docBuilder = docFactory.newDocumentBuilder();
			exceptdoc = docBuilder.newDocument();
			exceptrootElement = exceptdoc.createElement("ExceptionReport");
			exceptdoc.appendChild(exceptrootElement);
			attr = exceptdoc.createAttribute("numOfExceptions");
			int no_of_exception = certExceptionList.size();
			attr.setValue(String.valueOf(no_of_exception));
			exceptrootElement.setAttributeNode(attr);
			for (ExceptionMessage msg : certExceptionList){
				try {
					queue.put(msg);
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
			synchronized (certificateUpdateList){
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);
				DOMSource exceptsource = new DOMSource(exceptdoc);
				excepttransformer.transform(exceptsource, exceptionResult);
			}
			
		}  catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}

	public static void parseExceptionList(ExceptionMessage msg){
		synchronized (msg){
			Element details = exceptdoc.createElement("Details");
			exceptrootElement.appendChild(details);
			Attr attr = exceptdoc.createAttribute("feedLine");
			attr.setValue(msg.getFeedLine());
			details.setAttributeNode(attr);
			attr = exceptdoc.createAttribute("exception");
			attr.setValue(msg.getException());
			details.setAttributeNode(attr);
		}
	}
	
	public static void parseCertList(String ISIN){
		
		synchronized (certificateUpdateList){
			String tempISIN = ISIN;
			BigDecimal avgBidPrice = new BigDecimal("0.00");
			BigDecimal avgAskPrice = new BigDecimal("0.00");
			BigDecimal totalBidPrice = new BigDecimal("0.00");
			BigDecimal totalAskPrice = new BigDecimal("0.00");
			long num_of_update = 0;
				ArrayList<CertificateUpdate> certList = certificateUpdateList.get(tempISIN);
				num_of_update = certList.size();
				for (CertificateUpdate certUpd: certList){
					totalBidPrice = totalBidPrice.add(certUpd.getBidPrice());
					totalAskPrice = totalAskPrice.add(certUpd.getAskPrice());
				}
				avgBidPrice = totalBidPrice.divide(new BigDecimal(num_of_update), 2, RoundingMode.CEILING).setScale(2, RoundingMode.CEILING);
				avgAskPrice = totalAskPrice.divide(new BigDecimal(num_of_update), 2, RoundingMode.CEILING).setScale(2, RoundingMode.CEILING);
			
			
			
				Element certificate = doc.createElement("Certificate");
				rootElement.appendChild(certificate);
				Attr attr = doc.createAttribute("ISIN");
				attr.setValue(tempISIN);
				certificate.setAttributeNode(attr);
				attr = doc.createAttribute("numOfUpdate");
				attr.setValue(String.valueOf(num_of_update));
				certificate.setAttributeNode(attr);
				attr = doc.createAttribute("avgBidPrice");
				attr.setValue(avgBidPrice.toString());
				certificate.setAttributeNode(attr);
				attr = doc.createAttribute("avgAskPrice");
				attr.setValue(avgAskPrice.toString());
				certificate.setAttributeNode(attr);
		}
		
			
			
			
	}
}