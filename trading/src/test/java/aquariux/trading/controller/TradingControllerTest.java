package aquariux.trading.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import aquariux.trading.exceptionhandler.exception.ClosedTradingTransactionException;
import aquariux.trading.exceptionhandler.exception.UserDoesNotExistException;
import aquariux.trading.model.dto.CoinPriceResponseDTO;
import aquariux.trading.model.dto.CoinWalletBalanceDTO;
import aquariux.trading.model.dto.TradingTransactionDTO;
import aquariux.trading.model.requestbody.TradingInputRequestBody;
import aquariux.trading.service.TradingService;
import aquariux.trading.service.UpdatePriceService;

@WebMvcTest(TradingController.class)
public class TradingControllerTest {
	@Autowired
    private MockMvc mvc;
	
    @Autowired
    private ObjectMapper objectMapper;
	
    @MockBean
    private UpdatePriceService updatePriceService;
    
    @MockBean
    TradingService tradingService;
    
    private static final String GET_COIN_PRICE_LIST_URL = "/trading/coinPriceList";
    private static final String OPEN_TRADING_TRANSACTION_URL = "/trading/openTradeTransaction";
    private static final String CLOSED_TRADING_TRANSACTION_URL = "/trading/closedTradeTransaction/";
    private static final String GET_USER_WALLET_BALANCE_URL = "/trading/walletBalance/";
    private static final String GET_USER_TRADING_TRANSACTION_HISTORY_URL = "/trading/tradingTransactionHistory/";

    private static final String ETH = "ETH";
    private static final String USERNAME = "user1";
    private static final String SELL_ORDER = "SELL";
    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Test
    void performGetCoinPriceListThenReturnOkStatus() throws Exception {
    	List<CoinPriceResponseDTO> coinPriceResponseDTOList = new ArrayList<>();
    	CoinPriceResponseDTO coinPriceResponseDTO = new CoinPriceResponseDTO(ETH, "2000", "2005");
    	coinPriceResponseDTOList.add(coinPriceResponseDTO);
    	
    	Mockito.when(updatePriceService.getCoinPriceFromDatabase()).thenReturn(coinPriceResponseDTOList);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(GET_COIN_PRICE_LIST_URL))
                .andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getContentAsString(),
                objectMapper.writeValueAsString(coinPriceResponseDTOList));
    }
    
    @Test
    void performOpenTradingTransactionThenReturnOkStatus() throws Exception {
    	TradingInputRequestBody tradingInputRequestBody = new TradingInputRequestBody(USERNAME, ETH, SELL_ORDER, 1);
    	TradingTransactionDTO tradingTransactionDTO = new TradingTransactionDTO();
    	tradingTransactionDTO.setCoinName(ETH);
    	tradingTransactionDTO.setVolume(1);
    	
    	Mockito.when(tradingService.openTradingTransaction(tradingInputRequestBody)).thenReturn(tradingTransactionDTO);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post(OPEN_TRADING_TRANSACTION_URL).contentType(APPLICATION_JSON_UTF8)
                .content(asJsonString(tradingInputRequestBody)))
                .andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getContentAsString(),
                objectMapper.writeValueAsString(tradingTransactionDTO));
    }
    
    @Test
    void performOpenTradingTransactionThenReturnBadRequestStatus() throws Exception {
    	TradingInputRequestBody tradingInputRequestBody = new TradingInputRequestBody(USERNAME, ETH, SELL_ORDER, 1);

    	Mockito.when(tradingService.openTradingTransaction(tradingInputRequestBody)).thenThrow(UserDoesNotExistException.class);
    	
    	mvc.perform(MockMvcRequestBuilders.post(OPEN_TRADING_TRANSACTION_URL).contentType(APPLICATION_JSON_UTF8)
                .content(asJsonString(tradingInputRequestBody)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void performClosedTradingTransactionThenReturnOkStatus() throws Exception {
    	int tradingTransactionNumber = 1;
    	TradingTransactionDTO tradingTransactionDTO = new TradingTransactionDTO();
    	tradingTransactionDTO.setCoinName(ETH);
    	tradingTransactionDTO.setVolume(1);
    	tradingTransactionDTO.setTradingTransactionNumber(tradingTransactionNumber);
    	
    	Mockito.when(tradingService.closedTradingTransaction(tradingTransactionNumber)).thenReturn(tradingTransactionDTO);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.put(CLOSED_TRADING_TRANSACTION_URL + tradingTransactionNumber))
                .andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getContentAsString(),
                objectMapper.writeValueAsString(tradingTransactionDTO));
    }
    
    @Test
    void performClosedTradingTransactionThenBadRequestStatus() throws Exception {
    	int tradingTransactionNumber = 1;
    	TradingTransactionDTO tradingTransactionDTO = new TradingTransactionDTO();
    	tradingTransactionDTO.setCoinName(ETH);
    	tradingTransactionDTO.setVolume(1);
    	tradingTransactionDTO.setTradingTransactionNumber(tradingTransactionNumber);
    	
    	Mockito.when(tradingService.closedTradingTransaction(tradingTransactionNumber)).thenThrow(ClosedTradingTransactionException.class);
        mvc.perform(MockMvcRequestBuilders.put(CLOSED_TRADING_TRANSACTION_URL + tradingTransactionNumber))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void performGetUserWalletBalanceThenReturnOkStatus() throws Exception {
    	List<CoinWalletBalanceDTO> coinWalletBalanceDTOList = new ArrayList<>();
    	CoinWalletBalanceDTO coinWalletBalanceDTO = new CoinWalletBalanceDTO(ETH, 10);
    	coinWalletBalanceDTOList.add(coinWalletBalanceDTO);
    	
    	Mockito.when(tradingService.getUserWalletBalance(USERNAME)).thenReturn(coinWalletBalanceDTOList);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(GET_USER_WALLET_BALANCE_URL + USERNAME))
                .andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getContentAsString(),
                objectMapper.writeValueAsString(coinWalletBalanceDTOList));
    }
    
    @Test
    void performGetUserWalletBalanceThenReturnBadRequestStatus() throws Exception {
    	Mockito.when(tradingService.getUserWalletBalance(USERNAME)).thenThrow(UserDoesNotExistException.class);
    	
        mvc.perform(MockMvcRequestBuilders.get(GET_USER_WALLET_BALANCE_URL + USERNAME))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void performGetUserTradingTransactionHistoryThenReturnOkStatus() throws Exception {
    	List<TradingTransactionDTO> tradingTransactionDTOList = new ArrayList<>();
    	int tradingTransactionNumber = 1;
    	TradingTransactionDTO tradingTransactionDTO = new TradingTransactionDTO();
    	tradingTransactionDTO.setCoinName(ETH);
    	tradingTransactionDTO.setVolume(1);
    	tradingTransactionDTO.setTradingTransactionNumber(tradingTransactionNumber);
    	tradingTransactionDTOList.add(tradingTransactionDTO);
    	
    	Mockito.when(tradingService.getTradingTransactionList(USERNAME)).thenReturn(tradingTransactionDTOList);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(GET_USER_TRADING_TRANSACTION_HISTORY_URL + USERNAME))
                .andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getContentAsString(),
                objectMapper.writeValueAsString(tradingTransactionDTOList));
    }
    
    @Test
    void performGetUserTradingTransactionHistoryThenReturnBadRequestStatus() throws Exception {
    	Mockito.when(tradingService.getTradingTransactionList(USERNAME)).thenThrow(UserDoesNotExistException.class);
    	
        mvc.perform(MockMvcRequestBuilders.get(GET_USER_TRADING_TRANSACTION_HISTORY_URL + USERNAME))
                .andExpect(status().isBadRequest());
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
