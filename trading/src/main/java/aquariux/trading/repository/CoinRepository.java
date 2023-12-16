package aquariux.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aquariux.trading.model.Coin;
import java.util.Optional;
import org.springframework.stereotype.Repository;


@Repository
public interface CoinRepository extends JpaRepository<Coin, Integer>{
	Optional<Coin> findByName(String name);
}
