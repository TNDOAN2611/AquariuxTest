package aquariux.trading.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import aquariux.trading.service.factory.TradingTransactionFactory;
import aquariux.trading.service.factory.implement.TradingTransactionFactoryImplement;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class TradingServiceTest {
	@InjectMocks
	TradingService testClass;
	
	UserRepository userRepository;
	
	CoinRepository coinRepository;
	
	TradingTransactionRepository tradingTransactionRepository;
	
	TradingTransactionDTOMapper tradingTransactionDTOMapper;
	
	TradingTransactionFactory tradingTransactionFactory;
	
    private static final String ETH = "ETH";
    private static final String USDT = "USDT";
    private static final String USERNAME = "user1";
    private static final String SELL_ORDER = "SELL";
    private static final String BUY_ORDER = "BUY";
    private static final String OPEN_STATUS = "OPEN";
    private static final String CLOSED_STATUS = "CLOSED";
    private static final float VOLUMN = 1;

    private User user;
    private Coin coin;
    
    @BeforeAll
    void prepareDataForTest() {
    	userRepository = Mockito.mock(UserRepository.class);
    	coinRepository = Mockito.mock(CoinRepository.class);
    	tradingTransactionRepository = Mockito.mock(TradingTransactionRepository.class);
    	tradingTransactionDTOMapper = Mockito.mock(TradingTransactionDTOMapper.class);
    	tradingTransactionFactory = Mockito.mock(TradingTransactionFactoryImplement.class);
    	user = new User();
    	user.setUsername(USERNAME);
    
    	coin = new Coin();
    	coin.setName(ETH);
    	coin.setAskPrice(2005);
    	coin.setBidPrice(2000);
    	Mockito.when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
    	Mockito.when(coinRepository.findByName(ETH)).thenReturn(Optional.of(coin));
    }
    
    @BeforeEach
    void initWalletBalance() {
    	Map<String, Float> walletBalance = new HashMap<>();
    	walletBalance.put(USDT, (float) 50000.0);
    	user.setWalletBalance(walletBalance);
    }
    
    @AfterEach
    void refreshWalletBalance() {
    	user.setWalletBalance(new HashMap<>());
    }
	
    
	@Test
	void performOpenBuyOrderTradingTransactionThenSucess() throws UserDoesNotExistException, CoinDoesNotExistException, WalletBalanceDoesNotEnoughException {
    	TradingTransaction tradingTransaction = mock(TradingTransaction.class);
    	Mockito.when(tradingTransactionFactory.createTradingTransaction(BUY_ORDER, coin.getAskPrice(), user, coin, VOLUMN))
    	.thenReturn(tradingTransaction);
    	
    	TradingTransactionDTO tradingTransactionDTO = createTradingTransactionDTO(ETH, USERNAME, BUY_ORDER, VOLUMN, OPEN_STATUS);
    	Mockito.when(tradingTransactionDTOMapper.mapTradingTransactionDTOInfo(tradingTransaction)).thenReturn(tradingTransactionDTO);
    	
    	TradingInputRequestBody tradingInputRequestBody = new TradingInputRequestBody(USERNAME, ETH, BUY_ORDER, VOLUMN);
    	TradingTransactionDTO result = testClass.openTradingTransaction(tradingInputRequestBody);
    	
    	validateReturnedTradingTransactionDTO(ETH, USERNAME, BUY_ORDER, VOLUMN, OPEN_STATUS, result);
	}
	
	@Test
	void performOpenSellOrderTradingTransactionThenSucess() throws UserDoesNotExistException, CoinDoesNotExistException, WalletBalanceDoesNotEnoughException {
    	TradingTransaction tradingTransaction = mock(TradingTransaction.class);
    	Mockito.when(tradingTransactionFactory.createTradingTransaction(SELL_ORDER, coin.getBidPrice(), user, coin, VOLUMN))
    	.thenReturn(tradingTransaction);
    	
    	TradingTransactionDTO tradingTransactionDTO = createTradingTransactionDTO(ETH, USERNAME, SELL_ORDER, VOLUMN, OPEN_STATUS);
    	Mockito.when(tradingTransactionDTOMapper.mapTradingTransactionDTOInfo(tradingTransaction)).thenReturn(tradingTransactionDTO);
    	
    	TradingInputRequestBody tradingInputRequestBody = new TradingInputRequestBody(USERNAME, ETH, SELL_ORDER, VOLUMN);
    	TradingTransactionDTO result = testClass.openTradingTransaction(tradingInputRequestBody);
    	
    	validateReturnedTradingTransactionDTO(ETH, USERNAME, SELL_ORDER, VOLUMN, OPEN_STATUS, result);
	}
	
	@Test()
	void performOpenOrderTradingTransactionThenThrowUserDoesNotExistException() {
    	TradingInputRequestBody tradingInputRequestBody = new TradingInputRequestBody(USERNAME+"x", ETH, SELL_ORDER, VOLUMN);

		assertThrows(UserDoesNotExistException.class, () -> testClass.openTradingTransaction(tradingInputRequestBody));
	}
	
	@Test()
	void performOpenOrderTradingTransactionThenThrowCoinDoesNotExistException() {
    	TradingInputRequestBody tradingInputRequestBody = new TradingInputRequestBody(USERNAME, ETH+"x", SELL_ORDER, VOLUMN);

		assertThrows(CoinDoesNotExistException.class, () -> testClass.openTradingTransaction(tradingInputRequestBody));
	}
	
	@Test()
	void performOpenOrderTradingTransactionThenThrowWalletBalanceDoesNotEnoughException() {
    	TradingInputRequestBody tradingInputRequestBody = new TradingInputRequestBody(USERNAME, ETH, SELL_ORDER, VOLUMN*30);

		assertThrows(WalletBalanceDoesNotEnoughException.class, () -> testClass.openTradingTransaction(tradingInputRequestBody));
	}
	
	@Test()
	void performGetUserWalletBalanceThenSuccess() throws UserDoesNotExistException {

		List<CoinWalletBalanceDTO> result = testClass.getUserWalletBalance(USERNAME);
		
		assertEquals(result.get(0).getCoinName(), USDT);
		assertEquals(result.get(0).getQuantity(), 50000);
	}
	
	@Test()
	void performGetUserWalletBalanceThenThrowUserDoesNotExistException(){
		assertThrows(UserDoesNotExistException.class, () -> testClass.getUserWalletBalance(USERNAME + "x"));
	}
	
	@Test()
	void performClosedTradingTransactionThenSuccess() throws ClosedTradingTransactionException {
		int tradingTransactionNumber = 1;
		TradingTransaction tradingTransaction = new TradingTransaction();
		tradingTransaction.setId(tradingTransactionNumber);
		tradingTransaction.setStatus(OPEN_STATUS);
		tradingTransaction.setCoin(coin);
		tradingTransaction.setUser(user);
		user.getWalletBalance().put(ETH, (float) 10);
		tradingTransaction.setCreatedDateTime(LocalDateTime.now());
		tradingTransaction.setOrderType(BUY_ORDER);
		when(tradingTransactionRepository.findById(tradingTransactionNumber)).thenReturn(Optional.of(tradingTransaction));
    	TradingTransactionDTO tradingTransactionDTO = createTradingTransactionDTO(ETH, USERNAME, BUY_ORDER, VOLUMN, CLOSED_STATUS);
    	Mockito.when(tradingTransactionDTOMapper.mapTradingTransactionDTOInfo(tradingTransaction)).thenReturn(tradingTransactionDTO);
		
		TradingTransactionDTO result = testClass.closedTradingTransaction(tradingTransactionNumber);
		validateReturnedTradingTransactionDTO(ETH, USERNAME, BUY_ORDER, tradingTransactionNumber, CLOSED_STATUS, result);
	}
	
	@Test
	void performClosedTradingTransactionThenThrowClosedTradingTransactionException() {
		//trading transaction does not exist
		int tradingTransactionNumber = 1;
		assertThrows(ClosedTradingTransactionException.class, () ->  testClass.closedTradingTransaction(tradingTransactionNumber));
		
		//trading transaction already closed
		TradingTransaction tradingTransaction = new TradingTransaction();
		tradingTransaction.setStatus(CLOSED_STATUS);

		when(tradingTransactionRepository.findById(tradingTransactionNumber)).thenReturn(Optional.of(tradingTransaction));
		assertThrows(ClosedTradingTransactionException.class, () ->  testClass.closedTradingTransaction(tradingTransactionNumber));
	}
	
	@Test
	void performGetTradingTransactionHistoryThenSuccess() throws UserDoesNotExistException {
		TradingTransaction closedTradingTransaction = new TradingTransaction();
		closedTradingTransaction.setStatus(CLOSED_STATUS);
		closedTradingTransaction.setCoin(coin);
		closedTradingTransaction.setUser(user);
		closedTradingTransaction.setCreatedDateTime(LocalDateTime.now());
		
		TradingTransaction openTradingTransaction = new TradingTransaction();
		openTradingTransaction.setStatus(OPEN_STATUS);
		openTradingTransaction.setCoin(coin);
		openTradingTransaction.setUser(user);
		openTradingTransaction.setCreatedDateTime(LocalDateTime.now());

		List<TradingTransaction> tradingTransactionList = new ArrayList<>();
		tradingTransactionList.add(openTradingTransaction);
		tradingTransactionList.add(closedTradingTransaction);
		
		user.setTradingTransactionList(tradingTransactionList);
		
    	TradingTransactionDTO closedTradingTransactionDTO = createTradingTransactionDTO(ETH, USERNAME, BUY_ORDER, VOLUMN, CLOSED_STATUS);
    	TradingTransactionDTO openTradingTransactionDTO = createTradingTransactionDTO(ETH, USERNAME, BUY_ORDER, VOLUMN, OPEN_STATUS);
    	
    	List<TradingTransactionDTO> tradingTransactionDTOList = new ArrayList<>();
    	tradingTransactionDTOList.add(openTradingTransactionDTO);
    	tradingTransactionDTOList.add(closedTradingTransactionDTO);

    	
    	when(tradingTransactionDTOMapper.mapTradingTransactionDTOInfo(openTradingTransaction)).thenReturn(openTradingTransactionDTO);
    	when(tradingTransactionDTOMapper.mapTradingTransactionDTOInfo(closedTradingTransaction)).thenReturn(closedTradingTransactionDTO);


		
		List<TradingTransactionDTO> result = testClass.getTradingTransactionList(USERNAME);
		assertEquals(result, tradingTransactionDTOList);
	}
	
	@Test
	void performGetTradingTransactionHistoryThenThrowUserDoesNotExistException(){
		assertThrows(UserDoesNotExistException.class, () ->  testClass.getTradingTransactionList(USERNAME + "x"));
	}
	
	private TradingTransactionDTO createTradingTransactionDTO(String coinName, String userName, String orderType, float volumn, String status) {
    	TradingTransactionDTO tradingTransactionDTO = new TradingTransactionDTO();
    	tradingTransactionDTO.setCoinName(coinName);
    	tradingTransactionDTO.setUsername(userName);
    	tradingTransactionDTO.setOrderType(orderType);
    	tradingTransactionDTO.setVolume(volumn);
    	tradingTransactionDTO.setStatus(status);
    	
    	return tradingTransactionDTO;
	}
	
	private void validateReturnedTradingTransactionDTO(String coinName, String userName, String orderType, float volumn, String status, TradingTransactionDTO result) {
    	assertEquals(result.getCoinName(), coinName);
    	assertEquals(result.getUsername(), userName);
    	assertEquals(result.getOrderType(), orderType);
    	assertEquals(result.getVolume(), volumn);
    	assertEquals(result.getStatus(), status);
	}
}
