package aquariux.trading.service.factory.implement;

import static aquariux.trading.constant.Constant.OPEN_STATUS;

import org.springframework.stereotype.Component;

import aquariux.trading.model.Coin;
import aquariux.trading.model.TradingTransaction;
import aquariux.trading.model.User;
import aquariux.trading.service.factory.TradingTransactionFactory;

@Component
public class TradingTransactionFactoryImplement implements TradingTransactionFactory{

	@Override
	public TradingTransaction createTradingTransaction(String orderType, float price, User user, Coin coin,
			float volume) {
		TradingTransaction tradingTransaction = new TradingTransaction();
		tradingTransaction.setOrderType(orderType);
		tradingTransaction.setCurrentPrice(price);
		tradingTransaction.setEntryPrice(price);
		tradingTransaction.setUser(user);
		tradingTransaction.setCoin(coin);
		tradingTransaction.setVolume(volume);
		tradingTransaction.setStatus(OPEN_STATUS);

		return tradingTransaction;
	}

}
