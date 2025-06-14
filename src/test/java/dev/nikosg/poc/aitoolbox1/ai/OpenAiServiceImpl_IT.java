package dev.nikosg.poc.aitoolbox1.ai;

import com.openai.models.chat.completions.ChatCompletionMessageParam;
import com.openai.models.chat.completions.ChatCompletionTool;
import dev.nikosg.poc.aitoolbox1.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class OpenAiServiceImpl_IT extends AbstractIntegrationTest {

    @Autowired
    private OpenAiServiceImpl sut;

    @SpyBean
    private OpenAiClientHelper openAiClientHelper;

    @Test
    void shouldReceiveResponseFromAi_whenToolAreNotRelatedToUserPrompt() throws Exception {
        // given a prompt that is not related to any given tool
        String userPrompt = "Hi, my name is Nikos";

        // when
        String reply = sut.chat(userPrompt);

        // then
        assertThat(reply).isNotEmpty();
        captureAiRequestMessage(1);
    }

    @Test
    void shouldReceiveResponseFromAi_whenOneToolIsRelatedToUserPrompt() throws Exception {
        // given a prompt related to the GetWeather tool
        String userPrompt = "Hi, what is the weather in Zurich?";

        // when
        sut.chat(userPrompt);

        // then
        ArgumentCaptor<List<ChatCompletionTool>> captor = captureAiRequestTools(2);

        assertThat(captor.getAllValues().get(0)).isNotEmpty();
        assertThat(captor.getAllValues().get(1)).isNotEmpty();
    }

    @Test
    void shouldReceiveResponseFromAi_whenTwoToolsAreRelatedToUserPrompt() throws Exception {
        // given a prompt related both to the GetWeather tool and to the GetTimeTool
        String userPrompt = "Hi, what is the current time in server? how is the weather in Zurich?";

        // when
        sut.chat(userPrompt);

        // then
        ArgumentCaptor<List<ChatCompletionTool>> captor = captureAiRequestTools(3);


        assertThat(captor.getAllValues().get(0)).isNotEmpty();
        assertThat(captor.getAllValues().get(1)).isNotEmpty();
        assertThat(captor.getAllValues().get(2)).isNotEmpty();
    }

    private ArgumentCaptor<List<ChatCompletionMessageParam>> captureAiRequestMessage(int numberOfCalls) {
        final ArgumentCaptor<List<ChatCompletionMessageParam>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(openAiClientHelper, times(numberOfCalls)).sendToAi(captor.capture(), isNotNull());
        return captor;
    }

    private ArgumentCaptor<List<ChatCompletionTool>> captureAiRequestTools(int numberOfCalls) {
        final ArgumentCaptor<List<ChatCompletionTool>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(openAiClientHelper, times(numberOfCalls)).sendToAi(isNotNull(), captor.capture());
        return captor;
    }

}