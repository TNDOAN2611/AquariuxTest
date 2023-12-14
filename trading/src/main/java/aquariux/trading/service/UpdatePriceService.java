package aquariux.trading.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aquariux.trading.model.dto.CoinPriceResponseDTO;

@Service
public class UpdatePriceService {
	@Autowired
	RestTemplate restTemplate;
	
	public List<CoinPriceResponseDTO> getPrice() {
		ResponseEntity<CoinPriceResponseDTO[]> result = restTemplate.getForEntity("https://api.binance.com/api/v3/ticker/bookTicker", CoinPriceResponseDTO[].class);
		List<CoinPriceResponseDTO> coinPriceList = Arrays.asList(result.getBody());
		
		return coinPriceList.stream().filter(coin ->
			"ETHUSDT".equals(coin.getSymbol()) || "BTCUSDT".equals(coin.getSymbol())).collect(Collectors.toList());
	}
}
