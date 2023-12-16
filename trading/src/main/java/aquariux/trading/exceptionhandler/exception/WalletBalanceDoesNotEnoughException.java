package aquariux.trading.exceptionhandler.exception;

public class WalletBalanceDoesNotEnoughException extends Exception
{
	public WalletBalanceDoesNotEnoughException(){
		super("Wallet Balance does not enough to make this trading transaction");
	}
}
