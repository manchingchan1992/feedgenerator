import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;

public class CertificateUpdate{

	private long timestamp;
	private String ISIN;
	private String currency;
	private BigDecimal bidPrice= new BigDecimal("0.00");
	private long bidSize;
	private BigDecimal askPrice= new BigDecimal("0.00");
	private long askSize;
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getISIN() {
		return ISIN;
	}
	public void setISIN(String iSIN) {
		ISIN = iSIN;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public BigDecimal getBidPrice() {
		return bidPrice;
	}
	public void setBidPrice(BigDecimal bidPrice) {
		this.bidPrice = bidPrice.setScale(2,RoundingMode.CEILING);
	}
	public long getBidSize() {
		return bidSize;
	}
	public void setBidSize(long bidSize) {
		this.bidSize = bidSize;
	}
	public BigDecimal getAskPrice() {
		return askPrice;
	}
	public void setAskPrice(BigDecimal askPrice) {
		this.askPrice = askPrice.setScale(2,RoundingMode.CEILING );
	}
	public long getAskSize() {
		return askSize;
	}
	public void setAskSize(long askSize) {
		this.askSize = askSize;
	}
	
	@Override
	public String toString(){
		return this.timestamp+","
				+this.ISIN+","
				+this.currency+","
				+this.bidPrice+","
				+this.bidSize+","
				+this.askPrice+","
				+this.askSize;
	}
}