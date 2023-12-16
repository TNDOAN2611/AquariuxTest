package aquariux.trading.service.factory;

import aquariux.trading.model.Coin;
import aquariux.trading.model.TradingTransaction;
import aquariux.trading.model.User;


public interface TradingTransactionFactory {

	TradingTransaction createTradingTransaction(String orderType, float price, User user, Coin coin, float volume);
}
