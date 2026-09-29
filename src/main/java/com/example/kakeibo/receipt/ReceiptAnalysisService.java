package com.example.kakeibo.receipt;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Base64ImageSource;
import com.anthropic.models.messages.ContentBlockParam;
import com.anthropic.models.messages.ImageBlockParam;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.TextBlockParam;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Service
public class ReceiptAnalysisService {

    @Value("${anthropic.api.key:}")
    private String apiKey;

    private static final String PROMPT = """
            このレシートの画像を解析して、以下のJSON形式で情報を抽出してください。
            余分なテキストは不要です。JSONのみ返してください。

            {
              "date": "YYYY-MM-DD",
              "amount": 金額（整数、税込合計金額）,
              "storeName": "店舗名",
              "suggestedCategoryName": "カテゴリ名（食費/日用品/交通費/娯楽/外食/その他 のどれか）",
              "memo": "簡単なメモ（任意）"
            }

            - 日付が不明な場合は今日の日付を使用してください
            - 金額が不明な場合は0を使用してください
            - 店舗名が不明な場合は空文字を使用してください
            """;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReceiptAnalysisResult analyze(MultipartFile file) throws Exception {
        String base64Data = Base64.getEncoder().encodeToString(file.getBytes());
        Base64ImageSource.MediaType mediaType = detectMediaType(file.getContentType());

        AnthropicClient client = AnthropicOkHttpClient.builder()
                .apiKey(apiKey)
                .build();

        MessageCreateParams params = MessageCreateParams.builder()
                .model("claude-opus-4-8")
                .maxTokens(512L)
                .addUserMessageOfBlockParams(List.of(
                        ContentBlockParam.ofImage(ImageBlockParam.builder()
                                .source(Base64ImageSource.builder()
                                        .mediaType(mediaType)
                                        .data(base64Data)
                                        .build())
                                .build()),
                        ContentBlockParam.ofText(TextBlockParam.builder()
                                .text(PROMPT)
                                .build())
                ))
                .build();

        Message response = client.messages().create(params);
        String text = response.content().stream()
                .filter(b -> b.isText())
                .map(b -> b.asText().text())
                .findFirst()
                .orElse("{}");

        return parseResult(text);
    }

    private ReceiptAnalysisResult parseResult(String json) {
        ReceiptAnalysisResult result = new ReceiptAnalysisResult();
        result.setDate(LocalDate.now());
        result.setAmount(0);
        result.setStoreName("");
        result.setSuggestedCategoryName("その他");
        result.setMemo("");

        try {
            String cleaned = json.trim();
            int start = cleaned.indexOf('{');
            int end = cleaned.lastIndexOf('}');
            if (start >= 0 && end > start) {
                cleaned = cleaned.substring(start, end + 1);
            }
            JsonNode node = objectMapper.readTree(cleaned);

            if (node.has("date") && !node.get("date").isNull()) {
                try {
                    result.setDate(LocalDate.parse(node.get("date").asText()));
                } catch (Exception ignored) {}
            }
            if (node.has("amount") && !node.get("amount").isNull()) {
                result.setAmount(node.get("amount").asInt());
            }
            if (node.has("storeName") && !node.get("storeName").isNull()) {
                result.setStoreName(node.get("storeName").asText(""));
            }
            if (node.has("suggestedCategoryName") && !node.get("suggestedCategoryName").isNull()) {
                result.setSuggestedCategoryName(node.get("suggestedCategoryName").asText("その他"));
            }
            if (node.has("memo") && !node.get("memo").isNull()) {
                result.setMemo(node.get("memo").asText(""));
            }
        } catch (Exception ignored) {}

        return result;
    }

    private Base64ImageSource.MediaType detectMediaType(String contentType) {
        if (contentType == null) return Base64ImageSource.MediaType.IMAGE_JPEG;
        return switch (contentType.toLowerCase()) {
            case "image/png" -> Base64ImageSource.MediaType.IMAGE_PNG;
            case "image/gif" -> Base64ImageSource.MediaType.IMAGE_GIF;
            case "image/webp" -> Base64ImageSource.MediaType.IMAGE_WEBP;
            default -> Base64ImageSource.MediaType.IMAGE_JPEG;
        };
    }
}
