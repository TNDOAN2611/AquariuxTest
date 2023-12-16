package aquariux.trading.repository;

import aquariux.trading.model.TradingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradingTransactionRepository extends JpaRepository<TradingTransaction, Integer>
{
}
