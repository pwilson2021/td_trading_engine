package turntabl.io.trade_engine.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import turntabl.io.trade_engine.model.TradeModel;
import turntabl.io.trade_engine.publish.TradeEngineRabbitMqSender;

@RestController
public class TradeEngineController {

    @Autowired
    TradeEngineRabbitMqSender tradeEngineRabbitMqSender;

    @PostMapping
    public String trade(@RequestBody TradeModel trade){
        tradeEngineRabbitMqSender.send(trade);

        return "Trade sent to Queue successfully";
    }
}
