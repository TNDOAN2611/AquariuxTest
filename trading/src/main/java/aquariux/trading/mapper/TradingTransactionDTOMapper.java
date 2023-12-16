package aquariux.trading.mapper;

import aquariux.trading.model.TradingTransaction;
import aquariux.trading.model.dto.TradingTransactionDTO;
import org.springframework.stereotype.Component;

@Component
public class TradingTransactionDTOMapper
{
	public TradingTransactionDTO mapTradingTransactionDTOInfo(TradingTransaction tradingTransaction)
	{
		TradingTransactionDTO tradingTransactionDTO = new TradingTransactionDTO();
		tradingTransactionDTO.setTradingTransactionNumber(tradingTransaction.getId());
		tradingTransactionDTO.setOrderType(tradingTransaction.getOrderType());
		tradingTransactionDTO.setCurrentPrice(tradingTransaction.getCurrentPrice());
		tradingTransactionDTO.setEntryPrice(tradingTransaction.getEntryPrice());
		tradingTransactionDTO.setUsername(tradingTransaction.getUser().getUsername());
		tradingTransactionDTO.setCoinName(tradingTransaction.getCoin().getName());
		tradingTransactionDTO.setProfit(tradingTransaction.getProfit());
		tradingTransactionDTO.setStatus(tradingTransaction.getStatus());
		tradingTransactionDTO.setVolume(tradingTransaction.getVolume());
		tradingTransactionDTO.setCreatedDateTime(tradingTransaction.getCreatedDateTime());
		return tradingTransactionDTO;
	}
}
