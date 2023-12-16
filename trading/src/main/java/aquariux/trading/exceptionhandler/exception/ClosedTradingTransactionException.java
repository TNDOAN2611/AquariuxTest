package aquariux.trading.exceptionhandler.exception;

public class ClosedTradingTransactionException extends Exception
{
	public ClosedTradingTransactionException(){
		super("Can not close this trading transaction due to it does not exist or already closed");
	}
}
