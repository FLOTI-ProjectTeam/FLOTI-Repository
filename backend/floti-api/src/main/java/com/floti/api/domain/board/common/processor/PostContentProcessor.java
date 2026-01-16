package com.floti.api.domain.board.common.processor;

import com.floti.api.error.exception.InvalidPostContentException;
import lombok.RequiredArgsConstructor;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class PostContentProcessor {
    // Markdown 링크 패턴: [text](url)
    private static final Pattern LINK_PATTERN = Pattern.compile("\\[.*?]\\((.*?)\\)");

    private final Parser parser;
    private final TextContentRenderer renderer;

    // 입력 Markdown을 검증하고, Plain Text를 만들어 저장용 객체 반환
    public PostContent process(String markdown) {
        validateLinkScheme(markdown);
        String plainText = extractPlainText(markdown);
        return new PostContent(markdown, plainText);
    }

    // 링크 스킴 검증
    private void validateLinkScheme(String markdown) {
        Matcher matcher = LINK_PATTERN.matcher(markdown);

        while (matcher.find()) {
            String url = matcher.group(1).trim();

            if (!url.startsWith("http://") && !url.startsWith("https://"))
                throw new InvalidPostContentException("Invalid link scheme: " + url);
        }
    }

    // Markdown → Plain Text
    private String extractPlainText(String markdown) {
        var document = parser.parse(markdown);
        return renderer.render(document);
    }

    // 반환 DTO
    public record PostContent(String content, String contentPlain) {}
}
