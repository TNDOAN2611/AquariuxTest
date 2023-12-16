package aquariux.trading.model.requestbody;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradingInputRequestBody
{
	private String username;

	private String coinName;

	private String orderType;

	private float volume;
}
