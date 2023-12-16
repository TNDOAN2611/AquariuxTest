package aquariux.trading.controller;

import aquariux.trading.exceptionhandler.exception.ClosedTradingTransactionException;
import aquariux.trading.exceptionhandler.exception.CoinDoesNotExistException;
import aquariux.trading.exceptionhandler.exception.UserDoesNotExistException;
import aquariux.trading.exceptionhandler.exception.WalletBalanceDoesNotEnoughException;
import aquariux.trading.model.dto.CoinPriceResponseDTO;
import aquariux.trading.model.dto.CoinWalletBalanceDTO;
import aquariux.trading.model.dto.TradingTransactionDTO;
import aquariux.trading.model.requestbody.TradingInputRequestBody;
import aquariux.trading.service.TradingService;
import aquariux.trading.service.UpdatePriceService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trading")
public class TradingController
{
	final UpdatePriceService updatePriceService;

	final TradingService tradingService;

	public TradingController(UpdatePriceService updatePriceService, TradingService tradingService)
	{
		this.updatePriceService = updatePriceService;
		this.tradingService = tradingService;
	}

	@GetMapping("/coinPriceList")
	public ResponseEntity<List<CoinPriceResponseDTO>> getCoinPriceList()
	{
		return new ResponseEntity<>(updatePriceService.getCoinPriceFromDatabase(), HttpStatus.OK);
	}

	@PostMapping("/openTradeTransaction")
	public ResponseEntity<TradingTransactionDTO> openTradeTransaction(@RequestBody TradingInputRequestBody tradingInputRequestBody) throws UserDoesNotExistException, CoinDoesNotExistException, WalletBalanceDoesNotEnoughException
	{
		return new ResponseEntity<>(tradingService.openTradingTransaction(tradingInputRequestBody), HttpStatus.OK);
	}
	
	@PutMapping("/closedTradeTransaction/{positionNumber}")
	public ResponseEntity<TradingTransactionDTO> closedTradeTransaction(@PathVariable Integer positionNumber) throws ClosedTradingTransactionException
	{
		return new ResponseEntity<>(tradingService.closedTradingTransaction(positionNumber), HttpStatus.OK);
	}

	@GetMapping("/walletBalance/{username}")
	public ResponseEntity<List<CoinWalletBalanceDTO>> getUserWalletBalance(@PathVariable String username) throws UserDoesNotExistException
	{
		return new ResponseEntity<>(tradingService.getUserWalletBalance(username), HttpStatus.OK);
	}

	@GetMapping("/tradingTransactionHistory/{username}")
	public ResponseEntity<List<TradingTransactionDTO>> getTradingTransactionHistory(@PathVariable String username) throws UserDoesNotExistException
	{
		return new ResponseEntity<>(tradingService.getTradingTransactionList(username), HttpStatus.OK);
	}
}
