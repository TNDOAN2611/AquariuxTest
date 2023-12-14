package aquariux.trading.model.dto;

public class CoinPriceResponseDTO {
	public CoinPriceResponseDTO(String symbol, String bidPrice, String askPrice) {
		this.symbol = symbol;
		this.bidPrice = bidPrice;
		this.askPrice = askPrice;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getBidPrice() {
		return bidPrice;
	}
	public void setBidPrice(String bidPrice) {
		this.bidPrice = bidPrice;
	}
	public String getAskPrice() {
		return askPrice;
	}
	public void setAskPrice(String askPrice) {
		this.askPrice = askPrice;
	}
	private String symbol;
	private String bidPrice;
	private String askPrice;

}
