package aquariux.trading.job;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import aquariux.trading.model.Coin;
import aquariux.trading.model.dto.CoinPriceResponseDTO;
import aquariux.trading.repository.CoinRepository;
import aquariux.trading.service.UpdatePriceService;
import jakarta.transaction.Transactional;

@Component
public class UpdateCoinPriceJob {
	@Autowired
	UpdatePriceService updatePriceService;
	@Autowired
	CoinRepository coinRepository;
	
	@Scheduled(fixedDelay = 10000)
	@Transactional
    public void scheduleUpdateCoinPriceTask() {
		List<CoinPriceResponseDTO> updateCoinPriceList = updatePriceService.getPrice();
		Optional<Coin> eth = coinRepository.findByName("ETH");
		Optional<Coin> btc = coinRepository.findByName("BTC");
		
		for(CoinPriceResponseDTO coinPriceResponseDTO: updateCoinPriceList) {
			if("ETHUSDT".equals(coinPriceResponseDTO.getSymbol()) && eth.isPresent()) {
				eth.get().setAskPrice(Float.parseFloat(coinPriceResponseDTO.getAskPrice()));
				eth.get().setBidPrice(Float.parseFloat(coinPriceResponseDTO.getBidPrice()));
				coinRepository.save(eth.get());
			}else if ("BTCUSDT".equals(coinPriceResponseDTO.getSymbol()) && btc.isPresent()) {
				btc.get().setAskPrice(Float.parseFloat(coinPriceResponseDTO.getAskPrice()));
				btc.get().setBidPrice(Float.parseFloat(coinPriceResponseDTO.getBidPrice()));
				coinRepository.save(btc.get());
			}
		}
	}
}
