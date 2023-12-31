package aquariux.trading.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoinPriceResponseDTO
{
	private String symbol;

	private String bidPrice;

	private String askPrice;

}
