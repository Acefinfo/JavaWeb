/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 *
 * @author DELL
 */
public class EsewaUtil {

    public static final String MERCHANT_CODE = "EPAYTEST";
    public static final String SECRET_KEY = "8gBm/:&EnhH.1/q";
    public static final String FORM_URL = "https://rc-epay.esewa.com.np/api/epay/main/v2/form";
    public static final String STATUS_URL = "https://rc.esewa.com.np/api/epay/transaction/status/";

    public static String formatAmount(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }

    public static String generateSignature(String secretKey, String message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printBase64Binary(digest);
        } catch (Exception e) {
            throw new RuntimeException("Could not generate eSewa signature", e);
        }
    }

    public static String buildInitiationMessage(String formattedTotalAmount, String transactionUuid, String productCode) {
        return "total_amount=" + formattedTotalAmount + ",transaction_uuid=" + transactionUuid + ",product_code=" + productCode;
    }

    public static JsonObject decodeCallbackData(String base64Data) {
        byte[] raw = DatatypeConverter.parseBase64Binary(base64Data);
        String json = new String(raw, StandardCharsets.UTF_8);
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            return reader.readObject();
        }
    }

    
    public static boolean amountsMatch(String expected, String actual) {
        try {
            double a = Double.parseDouble(expected.trim());
            double b = Double.parseDouble(actual.trim());
            return Math.abs(a - b) < 0.01;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String jsonValueAsString(JsonObject json, String key) {
        JsonValue value = json.get(key);
        if (value == null) return "";
        if (value.getValueType() == JsonValue.ValueType.STRING) {
            return ((JsonString) value).getString();
        }
        return value.toString();
    }

    public static String buildMessageFromSignedFields(JsonObject json, String signedFieldNamesCsv) {
        String[] fields = signedFieldNamesCsv.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].trim();
            if (i > 0) sb.append(",");
            sb.append(field).append("=").append(jsonValueAsString(json, field));
        }
        return sb.toString();
    }

    
    public static boolean verifyTransactionStatus(String transactionUuid, String totalAmount) {
        try {
            String url = STATUS_URL + "?product_code=" + URLEncoder.encode(MERCHANT_CODE, "UTF-8")
                    + "&total_amount=" + URLEncoder.encode(totalAmount, "UTF-8")
                    + "&transaction_uuid=" + URLEncoder.encode(transactionUuid, "UTF-8");

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            java.io.InputStream stream = (responseCode >= 200 && responseCode < 300)
                    ? conn.getInputStream() : conn.getErrorStream();

            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }

            System.out.println("eSewa status check: HTTP " + responseCode + " body=" + body);

            if (responseCode < 200 || responseCode >= 300) {
                return false;
            }

            try (JsonReader reader = Json.createReader(new StringReader(body.toString()))) {
                JsonObject statusJson = reader.readObject();
                String status = jsonValueAsString(statusJson, "status");
                String returnedUuid = jsonValueAsString(statusJson, "transaction_uuid");
                return "COMPLETE".equals(status) && transactionUuid.equals(returnedUuid);
            }
        } catch (Exception e) {
            System.out.println("eSewa status check FAILED (network/parse error): " + e);
            e.printStackTrace();
            return false;
        }
    }
}