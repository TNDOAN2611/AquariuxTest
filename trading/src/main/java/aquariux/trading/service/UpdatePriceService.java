package aquariux.trading.service;

import aquariux.trading.repository.CoinRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aquariux.trading.model.dto.CoinPriceResponseDTO;

import static aquariux.trading.constant.Constant.BTCUSDT;
import static aquariux.trading.constant.Constant.ETHUSDT;

@Service
public class UpdatePriceService
{
	final RestTemplate restTemplate;

	final CoinRepository coinRepository;
	
	public UpdatePriceService(RestTemplate restTemplate, CoinRepository coinRepository)
	{
		this.restTemplate = restTemplate;
		this.coinRepository = coinRepository;
	}

	public List<CoinPriceResponseDTO> getPrice()
	{
		String url = "https://api.binance.com/api/v3/ticker/bookTicker";
		ResponseEntity<CoinPriceResponseDTO[]> result = restTemplate.getForEntity(url, CoinPriceResponseDTO[].class);
		List<CoinPriceResponseDTO> coinPriceList = Arrays.asList(result.getBody());

		return coinPriceList.stream()
		 .filter(coin -> ETHUSDT.equals(coin.getSymbol()) || BTCUSDT.equals(coin.getSymbol()))
		 .collect(Collectors.toList());
	}

	public List<CoinPriceResponseDTO> getCoinPriceFromDatabase()
	{
		return coinRepository.findAll().stream().map(
		 coin -> new CoinPriceResponseDTO(coin.getName(), String.valueOf(coin.getAskPrice()),
		  String.valueOf(coin.getBidPrice()))).collect(Collectors.toList());
	}
}
