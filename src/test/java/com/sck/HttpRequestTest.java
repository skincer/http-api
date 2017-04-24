package com.sck;

import com.sck.web.controller.RootController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by steph on 4/24/2017.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(RootController.class)
public class HttpRequestTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void pingShouldReturnPong() throws Exception {
        this.mockMvc.perform(
                get("/ping"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("pong")));
    }
}
