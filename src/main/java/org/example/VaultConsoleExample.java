package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class VaultConsoleExample {
    public static void main(String[] args) {
        String vaultAddr = "http://192.168.50.69:8200"; // Vault 주소는 환경에 맞게 변경
        String token = "root";

        try {
            HttpClient client = HttpClient.newHttpClient();

            // =========================
            // 1. 암호화
            // =========================

            String plaintext = "hello,vault";
            String encoded = Base64.getEncoder().encodeToString(plaintext.getBytes());

            String encryptJson = "{ \"plaintext\": \"" + encoded + "\" }";

            HttpRequest encryptRequest = HttpRequest.newBuilder()
                    .uri(URI.create(vaultAddr + "/v1/transit/encrypt/test-key"))
                    .header("X-Vault-Token", token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(encryptJson))
                    .build();

            HttpResponse<String> encryptResponse =
                    client.send(encryptRequest, HttpResponse.BodyHandlers.ofString());

            if (encryptResponse.statusCode() != 200) {
                System.out.println("암호화 실패");
                System.out.println(encryptResponse.body());
                return;
            }

            System.out.println("암호화 성공");
            System.out.println(encryptResponse.body());

            // 간단하게 ciphertext 추출 (문자열 파싱)
            String ciphertext = extractValue(encryptResponse.body(), "ciphertext");

            // =========================
            // 2. 복호화
            // =========================

            String decryptJson = "{ \"ciphertext\": \"" + ciphertext + "\" }";

            HttpRequest decryptRequest = HttpRequest.newBuilder()
                    .uri(URI.create(vaultAddr + "/v1/transit/decrypt/test-key"))
                    .header("X-Vault-Token", token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(decryptJson))
                    .build();

            HttpResponse<String> decryptResponse =
                    client.send(decryptRequest, HttpResponse.BodyHandlers.ofString());

            if (decryptResponse.statusCode() != 200) {
                System.out.println("복호화 실패");
                System.out.println(decryptResponse.body());
                return;
            }

            System.out.println("복호화 성공");
            System.out.println(decryptResponse.body());

            // Base64 plaintext 추출
            String decodedBase64 = extractValue(decryptResponse.body(), "plaintext");

            // Base64 → 원문 문자열
            String result = new String(Base64.getDecoder().decode(decodedBase64), StandardCharsets.UTF_8);

            System.out.println("최종 복호화 결과: " + result);

        } catch (Exception e) {
            System.out.println("Vault 호출 중 오류 발생");
            e.printStackTrace();
        }
    }

    // 간단한 JSON 값 추출 (예제용)
    private static String extractValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern) + pattern.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}