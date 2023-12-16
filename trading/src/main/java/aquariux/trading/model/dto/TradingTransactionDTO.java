package aquariux.trading.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradingTransactionDTO
{
	private Integer tradingTransactionNumber;

	private String orderType;

	private float entryPrice;

	private float currentPrice;

	private float profit;
	
	private float volume;

	private String username;

	private String status;

	private LocalDateTime createdDateTime;

	private String coinName;
}
