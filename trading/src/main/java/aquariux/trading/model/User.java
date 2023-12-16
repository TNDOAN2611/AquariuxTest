package aquariux.trading.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String username;

	@Column(name = "WALLETBALANCE")
	@ElementCollection
	private Map<String, Float> walletBalance = new HashMap<>();

	@Column(name = "TRADINGTRANSACTIONLIST")
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<TradingTransaction> tradingTransactionList = new ArrayList<>();
}
