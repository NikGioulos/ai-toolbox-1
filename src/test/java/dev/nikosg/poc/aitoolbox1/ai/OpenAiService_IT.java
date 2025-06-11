package dev.nikosg.poc.aitoolbox1.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.nikosg.poc.aitoolbox1.AbstractIntegrationTest;
import dev.nikosg.poc.aitoolbox1.awesome.RestTemplateHelper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class OpenAiService_IT extends AbstractIntegrationTest {

    @Autowired
    private OpenAiService sut;

    @SpyBean
    private RestTemplateHelper restTemplateHelper;

    @Test
    void shouldReceiveResponseFromAi_whenToolAreNotRelatedToUserPrompt() throws Exception {
        // given a prompt that is not related to any given tool
        String userPrompt = "Hi, my name is Nikos";

        // when
        sut.chat(userPrompt);

        // then
        verifyRestCall(1);
    }

    @Test
    void shouldReceiveResponseFromAi_whenOneToolIsRelatedToUserPrompt() throws Exception {
        // given a prompt related to the GetWeather tool
        String userPrompt = "Hi, what is the weather in Zurich?";

        // when
        sut.chat(userPrompt);

        // then
        ArgumentCaptor<AiRequest> captor = verifyRestCall(2);


        assertThat(captor.getAllValues().get(0).getTools()).isNotEmpty();
        assertThat(captor.getAllValues().get(1).getTools()).isNotEmpty();
    }

    @Test
    void shouldReceiveResponseFromAi_whenTwoToolsAreRelatedToUserPrompt() throws Exception {
        // given a prompt related both to the GetWeather tool and to the GetTimeTool
        String userPrompt = "Hi, what is the current time in server? how is the weather in Zurich?";

        // when
        sut.chat(userPrompt);

        // then
        ArgumentCaptor<AiRequest> captor = verifyRestCall(3);


        assertThat(captor.getAllValues().get(0).getTools()).isNotEmpty();
    }

    @Test
    void shouldSetToolCallId_whenRoleIsTool() throws Exception {
        // given a prompt related to the GetWeather tool
        String userPrompt = "Hi, what is the weather in Zurich?";

        // when
        sut.chat(userPrompt);

        // then
        ArgumentCaptor<AiRequest> captor = verifyRestCall(2);
        List<AiMessage> secondCallMessages = captor.getAllValues().get(1).getMessages();
        AiMessage toolMessage = secondCallMessages.stream()
                .filter(m -> m.getRole().equals("tool"))
                .findFirst()
                .orElseThrow();
        assertThat(toolMessage.getToolCallId()).isNotEmpty();
    }

    private ArgumentCaptor<AiRequest> verifyRestCall(int numberOfCalls) throws JsonProcessingException {
        ArgumentCaptor<AiRequest> captor = ArgumentCaptor.forClass(AiRequest.class);
        verify(restTemplateHelper, times(numberOfCalls)).postForEntity(
                eq(AiResponse.class),
                any(String.class),
                any(Map.class),
                captor.capture());
        return captor;
    }
}