package com.whatweeat.wwe.controller;

import com.whatweeat.wwe.service.MiniGameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest(properties = "test")
class MiniGameGroupControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired MiniGameService miniGameServiceImpl;

    @Test
    void createAndDelete() throws Exception{
        String pin = mockMvc.perform(post("/group")
                        .queryParam("token", "host"))
                .andExpect(status().isCreated())
                .andDo(document("create-group",
                        requestParameters(parameterWithName("token").description("Host's token"))))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(delete("/group/{pin}", pin))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document("delete-group",
                        pathParameters(parameterWithName("pin").description("Group's pin")),
                        responseBody()));

    }

    @Test
    void lookUpGroup() {
//        int pin1 = miniGameServiceImpl.createGroup("host");
//
//        mockMvc.perform(get("group/{pin}", pin1))
//                .andExpect(status().isOk())
//                .andExpect()
    }
}