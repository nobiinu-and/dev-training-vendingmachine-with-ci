package dev.training.vendingmachine.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VendingMachineController.class)
class VendingMachineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 購入_正常系_コーラを100円で購入すると200ステータスと成功レスポンスを返す() throws Exception {
        mockMvc.perform(post("/api/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"item\":\"cola\",\"amount\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("コーラをご購入いただきありがとうございます！"));
    }

    @Test
    void 購入_エラー系_コーラを50円で購入すると400ステータスと金額不足エラーを返す() throws Exception {
        mockMvc.perform(post("/api/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"item\":\"cola\",\"amount\":50}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("金額が不足しています。コーラは100円です。"));
    }

    @Test
    void 購入_エラー系_ペプシを100円で購入すると400ステータスと商品未対応エラーを返す() throws Exception {
        mockMvc.perform(post("/api/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"item\":\"pepsi\",\"amount\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("申し訳ございません。コーラのみ販売しております。"));
    }

    @Test
    void ヘルスチェック_正常系_200ステータスとOKレスポンスを返す() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
