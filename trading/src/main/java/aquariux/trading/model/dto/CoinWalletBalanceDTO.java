package aquariux.trading.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoinWalletBalanceDTO {
	private String coinName;
	private float quantity;
}
