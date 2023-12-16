package aquariux.trading.exceptionhandler;

import aquariux.trading.exceptionhandler.exception.ClosedTradingTransactionException;
import aquariux.trading.exceptionhandler.exception.CoinDoesNotExistException;
import aquariux.trading.exceptionhandler.exception.UserDoesNotExistException;
import aquariux.trading.exceptionhandler.exception.WalletBalanceDoesNotEnoughException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TradingExceptionHandler
{
	@ExceptionHandler({ UserDoesNotExistException.class, WalletBalanceDoesNotEnoughException.class, CoinDoesNotExistException.class, ClosedTradingTransactionException.class })
	public ResponseEntity<String> handleCustomException(Exception exception)
	{
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<String> handleException(Exception exception)
	{
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
