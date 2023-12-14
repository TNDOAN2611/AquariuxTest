package aquariux.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aquariux.trading.model.Coin;
import java.util.Optional;


public interface CoinRepository extends JpaRepository<Coin, Integer>{
	Optional<Coin> findByName(String name);
}
