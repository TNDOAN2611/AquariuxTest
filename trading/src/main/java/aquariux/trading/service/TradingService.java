package aquariux.trading.service;

import aquariux.trading.exceptionhandler.exception.ClosedTradingTransactionException;
import aquariux.trading.exceptionhandler.exception.CoinDoesNotExistException;
import aquariux.trading.exceptionhandler.exception.UserDoesNotExistException;
import aquariux.trading.exceptionhandler.exception.WalletBalanceDoesNotEnoughException;
import aquariux.trading.mapper.TradingTransactionDTOMapper;
import aquariux.trading.model.Coin;
import aquariux.trading.model.TradingTransaction;
import aquariux.trading.model.User;
import aquariux.trading.model.dto.CoinWalletBalanceDTO;
import aquariux.trading.model.dto.TradingTransactionDTO;
import aquariux.trading.model.requestbody.TradingInputRequestBody;
import aquariux.trading.repository.CoinRepository;
import aquariux.trading.repository.TradingTransactionRepository;
import aquariux.trading.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static aquariux.trading.constant.Constant.BUY_ORDER;
import static aquariux.trading.constant.Constant.CLOSED_STATUS;
import static aquariux.trading.constant.Constant.OPEN_STATUS;
import static aquariux.trading.constant.Constant.SELL_ORDER;
import static aquariux.trading.constant.Constant.USDT;

@Service
public class TradingService
{
	final UserRepository userRepository;

	final CoinRepository coinRepository;

	final TradingTransactionRepository tradingTransactionRepository;

	final TradingTransactionDTOMapper tradingTransactionDTOMapper;

	public TradingService(UserRepository userRepository, CoinRepository coinRepository, TradingTransactionRepository tradingTransactionRepository, TradingTransactionDTOMapper tradingTransactionDTOMapper)
	{
		this.userRepository = userRepository;
		this.coinRepository = coinRepository;
		this.tradingTransactionRepository = tradingTransactionRepository;
		this.tradingTransactionDTOMapper = tradingTransactionDTOMapper;
	}

	@Transactional
	public TradingTransactionDTO openTradingTransaction(TradingInputRequestBody tradingInputRequestBody) throws UserDoesNotExistException, CoinDoesNotExistException, WalletBalanceDoesNotEnoughException
	{
		Optional<User> user = userRepository.findByUsername(tradingInputRequestBody.getUsername());
		if (user.isPresent())
		{
			Optional<Coin> coin = coinRepository.findByName(tradingInputRequestBody.getCoinName());
			if (coin.isPresent())
			{
				//Initialize data base on order type
				String orderType;
				float price;
				if (BUY_ORDER.equals(tradingInputRequestBody.getOrderType()))
				{
					orderType = BUY_ORDER;
					price = coin.get().getAskPrice();
				}
				else
				{
					orderType = SELL_ORDER;
					price = coin.get().getBidPrice();
				}
				
				Map<String, Float> walletBalance = user.get().getWalletBalance();
				Float usdtWalletBalance = getQuantityCoinWalletBalance(walletBalance, USDT);
				Float currentCoinWalletBalance = getQuantityCoinWalletBalance(walletBalance, coin.get().getName());
				
				if (isWalletBalanceEnoughForOrderTradingTransaction(usdtWalletBalance, price,
				 tradingInputRequestBody.getVolume()))
				{
					TradingTransaction tradingTransaction = createTradingTransaction(orderType, price, user.get(), coin.get(),
					 tradingInputRequestBody.getVolume());
					walletBalance.put(USDT, usdtWalletBalance - price * tradingInputRequestBody.getVolume());
					walletBalance.put(coin.get().getName(), currentCoinWalletBalance + tradingInputRequestBody.getVolume());
					tradingTransactionRepository.save(tradingTransaction);
					userRepository.save(user.get());
					return tradingTransactionDTOMapper.mapTradingTransactionDTOInfo(tradingTransaction);
				}
				else
				{
					throw new WalletBalanceDoesNotEnoughException();
				}
			}
			throw new CoinDoesNotExistException(tradingInputRequestBody.getCoinName());
		}
		throw new UserDoesNotExistException(tradingInputRequestBody.getUsername());
	}
	
	private Float getQuantityCoinWalletBalance(Map<String, Float> walletBalance, String coinName) {
		Float currentCoinWalletBalance = walletBalance.get(coinName);
		if (currentCoinWalletBalance == null){
			currentCoinWalletBalance = Float.valueOf(0);
		}
		
		return currentCoinWalletBalance;
	}

	private boolean isWalletBalanceEnoughForOrderTradingTransaction(float usdtWalletBalance, float coinPrice, float volume)
	{
		return coinPrice * volume <= usdtWalletBalance;
	}

	private TradingTransaction createTradingTransaction(String orderType, float price, User user, Coin coin, float volume)
	{
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

	public List<CoinWalletBalanceDTO> getUserWalletBalance(String username) throws UserDoesNotExistException
	{
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isPresent())
		{
			List<CoinWalletBalanceDTO> coinWalletBalanceDTOList = new ArrayList<>();
			user.get().getWalletBalance().forEach((coin, quantity) -> coinWalletBalanceDTOList.add(new CoinWalletBalanceDTO(coin, quantity)));
			return coinWalletBalanceDTOList;
		}
		throw new UserDoesNotExistException(username);
	}

	@Transactional
	public TradingTransactionDTO closedTradingTransaction(Integer tradingTransactionNumber) throws ClosedTradingTransactionException
	{
		Optional<TradingTransaction> tradingTransaction = tradingTransactionRepository.findById(
		 tradingTransactionNumber);
		if (tradingTransaction.isPresent() && tradingTransaction.get().getStatus().equals(OPEN_STATUS))
		{
			//update trading transaction data
			tradingTransaction.get().setProfit(caculateProfit(tradingTransaction.get()));
			tradingTransaction.get().setStatus(CLOSED_STATUS);
			Coin coin = tradingTransaction.get().getCoin();
			float currentCoinPrice =
			 tradingTransaction.get().getOrderType().equals(BUY_ORDER) ? coin.getAskPrice() : coin.getBidPrice();
			tradingTransaction.get().setCurrentPrice(currentCoinPrice);

			//update wallet balance
			User user = tradingTransaction.get().getUser();
			float closedCoinQuality = tradingTransaction.get().getVolume();
			Float currentQuality = user.getWalletBalance().get(coin.getName());
			user.getWalletBalance().put(coin.getName(), currentQuality - closedCoinQuality);
			Float currentUsdt = user.getWalletBalance().get(USDT);
			currentUsdt = currentUsdt + tradingTransaction.get().getProfit();
			float returningUsdt = closedCoinQuality * tradingTransaction.get().getEntryPrice();
			currentUsdt = currentUsdt + returningUsdt;
			user.getWalletBalance().put(USDT, currentUsdt);

			userRepository.save(user);
			tradingTransactionRepository.save(tradingTransaction.get());
			return tradingTransactionDTOMapper.mapTradingTransactionDTOInfo(tradingTransaction.get());
		}
		throw new ClosedTradingTransactionException();
	}

	@Transactional
	public List<TradingTransactionDTO> getTradingTransactionList(String username) throws UserDoesNotExistException
	{
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isPresent())
		{
			List<TradingTransaction> tradingTransactionList = user.get().getTradingTransactionList();
			for (TradingTransaction tradingTransaction : tradingTransactionList)
			{
				if (tradingTransaction.getStatus().equals(OPEN_STATUS))
				{
					tradingTransaction.setProfit(caculateProfit(tradingTransaction));
					tradingTransaction.setCurrentPrice(
					 BUY_ORDER.equals(tradingTransaction.getOrderType()) ? tradingTransaction.getCoin().getAskPrice() :
					  tradingTransaction.getCoin().getBidPrice());
				}
			}
			tradingTransactionRepository.saveAll(tradingTransactionList);

			Collections.sort(tradingTransactionList,
			 (o1, o2) -> o2.getCreatedDateTime().compareTo(o1.getCreatedDateTime()));

			return tradingTransactionList.stream()
			 .map(tradingTransaction -> tradingTransactionDTOMapper.mapTradingTransactionDTOInfo(tradingTransaction))
			 .collect(Collectors.toList());
		}

		throw new UserDoesNotExistException(username);
	}

	private float caculateProfit(TradingTransaction tradingTransaction)
	{
		float profit = 0;
		if (BUY_ORDER.equals(tradingTransaction.getOrderType()))
		{
			profit = (tradingTransaction.getCoin().getAskPrice() - tradingTransaction.getEntryPrice())
					 * tradingTransaction.getVolume();
		}
		else
		{
			profit = (tradingTransaction.getEntryPrice() - tradingTransaction.getCoin().getBidPrice())
					 * tradingTransaction.getVolume();
		}

		return profit;
	}
}
