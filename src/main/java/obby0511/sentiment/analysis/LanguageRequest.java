package obby0511.sentiment.analysis;

import lombok.Builder;
import lombok.Value;

import java.nio.charset.Charset;

@Value
@Builder
public class LanguageRequest {
    private String text;
    private Charset encodingType;
}
