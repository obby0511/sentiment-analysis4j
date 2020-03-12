package obby0511.sentiment.analysis.impl;

import obby0511.sentiment.analysis.LanguageRequest;
import obby0511.sentiment.analysis.LanguageResponse;
import obby0511.sentiment.analysis.LanguageServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static obby0511.sentiment.analysis.impl.DefaultLanguageService.loadDictionary;
import static obby0511.sentiment.analysis.impl.DefaultLanguageService.mapToEntry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

class DefaultLanguageServiceTest {

    @Nested
    @DisplayName("constructor()")
    class Constructor {

        @Test
        void should_create_instance_with_default_constructor() {
            assertThat(new DefaultLanguageService()).isNotNull();
        }

        @Test
        void should_create_instance_with_string_constructor() {
            assertThat(new DefaultLanguageService("/test.csv")).isNotNull();
        }

        @Test
        void should_throw_exception_if_dictionary_notfound() {
            assertThatThrownBy(() -> new DefaultLanguageService("test"))
                    .isInstanceOf(LanguageServiceException.class);
        }
    }

    @Nested
    @DisplayName("analyzeSentiment()")
    class AnalyzeSentiment {

        DefaultLanguageService service = new DefaultLanguageService("/test.csv");

        @Test
        void should_analyze_positive_text() {
            LanguageRequest request = LanguageRequest.builder()
                    .text("OK is positive.")
                    .encodingType(StandardCharsets.UTF_8)
                    .build();
            LanguageResponse response = service.analyzeSentiment(request);
            assertThat(response).isNotNull();
            assertThat(response.getScore()).isEqualTo(1.0f);
            assertThat(response.getMagnitude()).isGreaterThan(0.0f);
        }

        @Test
        void should_analyze_negative_text() {
            var request = LanguageRequest.builder()
                    .text("NG is negative.")
                    .encodingType(StandardCharsets.UTF_8)
                    .build();
            var response = service.analyzeSentiment(request);
            assertThat(response).isNotNull();
            assertThat(response.getScore()).isEqualTo(-1.0f);
            assertThat(response.getMagnitude()).isLessThan(0.0f);
        }

        @Test
        void should_analyze_neutral_text() {
            var request = LanguageRequest.builder()
                    .text("NONE is neutral.")
                    .encodingType(StandardCharsets.UTF_8)
                    .build();
            var response = service.analyzeSentiment(request);
            assertThat(response).isNotNull();
            assertThat(response.getScore()).isEqualTo(0.0f);
            assertThat(response.getMagnitude()).isEqualTo(0.0f);
        }

        @Test
        void should_analyze_unnormalized_text() {
            var request = LanguageRequest.builder()
                    .text("ＯＫ is positive.")
                    .encodingType(StandardCharsets.UTF_8)
                    .build();
            var response = service.analyzeSentiment(request);
            assertThat(response).isNotNull();
            assertThat(response.getScore()).isEqualTo(1.0f);
            assertThat(response.getMagnitude()).isEqualTo(1.0f);
        }
    }

    @Nested
    @DisplayName("loadDictionary()")
    class LoadDictionary {

        @Test
        void should_load_scored_dictionary() {
            var scores = loadDictionary("/test.csv");
            assertThat(scores).hasSize(3)
                    .containsEntry("OK", 1.0f)
                    .containsEntry("NG", -1.0f)
                    .containsEntry("NONE", 0.0f);
        }

        @Test
        void should_throws_exception_if_dictionary_notfound() {
            assertThatThrownBy(() -> loadDictionary("test"))
                    .isInstanceOf(LanguageServiceException.class);
        }
    }

    @Nested
    @DisplayName("mapToEntry")
    class MapToEntry {

        @Test
        void should_returns_scored_entry() {
            assertThat(mapToEntry.apply(new String[]{"OK", "p"})).isEqualTo(entry("OK", 1.0f));
            assertThat(mapToEntry.apply(new String[]{"NG", "n"})).isEqualTo(entry("NG", -1.0f));
            assertThat(mapToEntry.apply(new String[]{"NONE", "e"})).isEqualTo(entry("NONE", 0.0f));
            assertThat(mapToEntry.apply(new String[]{"ERROR", "z"})).isEqualTo(entry("ERROR", 0.0f));
        }

        @Test
        void should_returns_normalized_entry() {
            assertThat(mapToEntry.apply(new String[]{"ＯＫ", "e"})).isEqualTo(entry("OK", 0.0f));
            assertThat(mapToEntry.apply(new String[]{"ｱｲｳｴｵ", "e"})).isEqualTo(entry("アイウエオ", 0.0f));
            assertThat(mapToEntry.apply(new String[]{"①②③④⑤⑥⑦⑧⑨⑩", "e"})).isEqualTo(entry("12345678910", 0.0f));
        }
    }
}
