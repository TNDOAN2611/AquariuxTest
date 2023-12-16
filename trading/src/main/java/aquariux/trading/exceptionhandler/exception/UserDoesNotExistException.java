package aquariux.trading.exceptionhandler.exception;

public class UserDoesNotExistException extends Exception
{
	public UserDoesNotExistException(String username){
		super("The user with username " + username +  " does not exist in system.");
	}
}
