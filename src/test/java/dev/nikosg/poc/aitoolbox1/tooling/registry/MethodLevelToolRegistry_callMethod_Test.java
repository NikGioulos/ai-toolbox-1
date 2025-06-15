package dev.nikosg.poc.aitoolbox1.tooling.registry;

import dev.nikosg.poc.aitoolbox1.backend.domain.ExchangeRate;
import dev.nikosg.poc.aitoolbox1.backend.service.AmountService;
import dev.nikosg.poc.aitoolbox1.backend.service.MyService;
import dev.nikosg.poc.aitoolbox1.backend.service.SportService;
import dev.nikosg.poc.aitoolbox1.tooling.tools.AmountTools;
import dev.nikosg.poc.aitoolbox1.tooling.tools.MyTools;
import dev.nikosg.poc.aitoolbox1.tooling.tools.SportTools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MethodLevelToolRegistry_callMethod_Test {
    @InjectMocks
    MethodLevelToolRegistry sut;

    MyTools myTools = new MyTools(new MyService());
    SportTools sportTools = new SportTools(new SportService());
    AmountTools amountTools = new AmountTools(new AmountService());

    @Test
    void shouldCallMethodWithZeroArguments() throws Exception {
        String argJson = "{}";
        Method method = MyTools.class.getMethod("getTime");
        String reply = sut.callMethod(method, myTools, argJson);
        assertThat(reply).isNotEmpty();
    }

    @Test
    void shouldCallMethodWithOneStringArgument() throws Exception {
        String argJson = """
                {
                  "location" : "Paris"
                }
                """;
        Method method = MyTools.class.getMethod("getWeather", String.class);
        String reply = sut.callMethod(method, myTools, argJson);
        assertThat(reply).contains("Paris");
    }

    @Test
    void shouldCallMethodWithOneDtoArgument() throws Exception {
        String argJson = """
                {
                  "exchangeRate" : {
                    "fromCurrency" : "CHF",
                    "toCurrency" : "USD",
                    "fromAmount" : 200
                  }
                }
                """;

        Method method = AmountTools.class.getMethod("convertAmount", ExchangeRate.class);
        String reply = sut.callMethod(method, amountTools, argJson);
        assertThat(reply).contains("244");
    }

    @Test
    void shouldCallMethodWithOneCollectionArgument() throws Exception {
        String argJson = """
                {
                  "exchangeRate" : {
                    "fromCurrency" : "CHF",
                    "toCurrency" : "USD",
                    "fromAmount" : 200
                  }
                }
                """;

        Method method = AmountTools.class.getMethod("convertAmount", ExchangeRate.class);
        String reply = sut.callMethod(method, amountTools, argJson);
        assertThat(reply).contains("244");
    }

    @Test
    void shouldCallMethodWithOneDtoArray() throws Exception {
        String argJson = """
                {
                   "exchangeRates" : [\s
                         {
                         "fromCurrency" : "CHF",
                                 "toCurrency" : "USD",
                                 "fromAmount" : 200
                        },\s
                        {
                         "fromCurrency" : "CHF",
                                 "toCurrency" : "USD",
                                 "fromAmount" : 100
                        }\s
                   ]
                }
               \s""";

        Method method = AmountTools.class.getMethod("convertAmounts", ExchangeRate[].class);
        String reply = sut.callMethod(method, amountTools, argJson);
        assertThat(reply).contains("244");
    }

    @Test
    void shouldCallMethodWithOneStringArray() throws Exception {
        String argJson = """
                {
                  "names" : [ "Peter", "Tobias" ]
                }
                """;

        Method method = SportTools.class.getMethod("findFavoriteFootballClubs", String[].class);
        String reply = sut.callMethod(method, sportTools, argJson);
        assertThat(reply).contains("Arsenal");
    }

    @Test
    void shouldCallMethodWithOneCollectionOfString() throws Exception {
        String argJson = """
                {
                  "names" : [ "Peter", "Tobias" ]
                }
                """;

        Method method = SportTools.class.getMethod("findFavoriteBasketBallClubs", List.class);
        String reply = sut.callMethod(method, sportTools, argJson);
        assertThat(reply).contains("Bulls");
    }
}
