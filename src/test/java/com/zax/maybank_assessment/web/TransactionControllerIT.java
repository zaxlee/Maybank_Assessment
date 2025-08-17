// src/test/java/com/zax/maybank_assessment/web/TransactionControllerIT.java
package com.zax.maybank_assessment.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zax.maybank_assessment.domain.Transaction;
import com.zax.maybank_assessment.repo.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIT {

    @Autowired MockMvc mvc;
    @Autowired TransactionRepository repo;
    @Autowired ObjectMapper om;

    Long id;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        var t = Transaction.builder()
                .accountNumber("8872838283")
                .trxAmount(new BigDecimal("123.00"))
                .description("FUND TRANSFER")
                .trxDate(LocalDate.parse("2019-09-12"))
                .trxTime(LocalTime.parse("11:11:11"))
                .customerId("222")
                .build();
        id = repo.save(t).getId();
    }

    @Test
    void search_byCustomer_withPagination_ok() throws Exception {
        mvc.perform(get("/api/transactions")
                        .param("page", "0")
                        .param("size", "5")
                        .param("customerId", "222"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    void getById_returnsEtag_andBody() throws Exception {
        mvc.perform(get("/api/transactions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(header().exists("ETag"))
                .andExpect(jsonPath("$.customerId").value("222"))
                .andExpect(jsonPath("$.accountNumber").value("8872838283"));
    }

    @Test
    void updateDescription_withIfMatch_ok() throws Exception {
        // 1) read current ETag (Spring returns quotes like "0")
        var etag = mvc.perform(get("/api/transactions/{id}", id))
                .andReturn().getResponse().getHeader("ETag");

        // 2) send update with same ETag in If-Match
        var body = """
          {"description":"UPDATED DESCRIPTION"}
        """;

        mvc.perform(put("/api/transactions/{id}/description", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("If-Match", etag)     // keep quotes from server
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(header().exists("ETag"))
                .andExpect(jsonPath("$.description").value("UPDATED DESCRIPTION"));
    }

    @Test
    void updateDescription_wrongIfMatch_returns412() throws Exception {
        var body = """
          {"description":"FAIL PLEASE"}
        """;

        mvc.perform(put("/api/transactions/{id}/description", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("If-Match", "999")  // wrong version on purpose
                        .content(body))
                .andExpect(status().isPreconditionFailed());
    }
}