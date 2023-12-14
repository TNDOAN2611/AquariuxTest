package aquariux.trading.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Coin {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	private String name;
	@Column(name = "BIDPRICE")
	private float bidPrice;
	@Column(name = "ASKPRICE")
	private float askPrice;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getBidPrice() {
		return bidPrice;
	}
	public void setBidPrice(float bidPrice) {
		this.bidPrice = bidPrice;
	}
	public float getAskPrice() {
		return askPrice;
	}
	public void setAskPrice(float askPrice) {
		this.askPrice = askPrice;
	}
}
