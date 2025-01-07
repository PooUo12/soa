package com.spo.workerService.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestClient {
    private String url = "http://127.0.0.1:17180/worker/api/workers";
}
