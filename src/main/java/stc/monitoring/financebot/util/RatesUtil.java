package stc.monitoring.financebot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import stc.monitoring.financebot.dto.RateDto;
import stc.monitoring.financebot.model.Rate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RatesUtil {

    public static List<RateDto> parseRatesFromResponse(String response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response);
        JsonNode quotesNode = rootNode.get("quotes");
        JsonNode timestampNode = rootNode.get("timestamp");
        Instant instant = Instant.ofEpochSecond(timestampNode.asLong());
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        Map<String,Double> ratesMap = mapper.convertValue(quotesNode,Map.class);
        List<RateDto> rates = new ArrayList<>();
        ratesMap.forEach((key,value)->
                rates.add(
                        new RateDto(key.substring(3), value, date)));
        return rates;
    }
}
