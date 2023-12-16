package aquariux.trading.exceptionhandler.exception;

public class CoinDoesNotExistException extends Exception
{
	public CoinDoesNotExistException(String coinName)
	{
		super(coinName + " coin does not exist in system.");
	}
}
