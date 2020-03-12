package obby0511.sentiment.analysis.impl;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import obby0511.sentiment.analysis.LanguageRequest;
import obby0511.sentiment.analysis.LanguageResponse;
import obby0511.sentiment.analysis.LanguageService;
import obby0511.sentiment.analysis.LanguageServiceException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class DefaultLanguageService implements LanguageService {
    private final Tokenizer tokenizer = new Tokenizer.Builder().build();
    private final Map<String, Float> scores;

    public DefaultLanguageService() {
        scores = loadDictionary("/pn.csv.m3.120408.trim.csv");
    }

    public DefaultLanguageService(final String dictionary) {
        scores = loadDictionary(dictionary);
    }

    @Override
    public LanguageResponse analyzeSentiment(final LanguageRequest request) {
        try {
            var text = normalize(request.getText());
            final var score = tokenizer.tokenize(text).parallelStream()
                    .map(Token::getSurface)
                    .mapToDouble(s -> scores.getOrDefault(s, 0f))
                    .sum();
            return new LanguageResponse(score);

        } catch (Exception e) {
            throw new LanguageServiceException(e.getMessage(), e);
        }
    }

    static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFKC);
    }

    static Map<String, Float> loadDictionary(final String dictionary) {
        try (var is = DefaultLanguageService.class.getResourceAsStream(dictionary);
             var br = new BufferedReader(new InputStreamReader(is))) {
            final var scores = br.lines().map(s -> s.split("\t"))
                    .filter(s -> s.length > 1) // e.g. "word  flag   description..."
                    .map(mapToEntry)
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1));
            return Collections.unmodifiableMap(scores);
        } catch (Exception e) {
            throw new LanguageServiceException(e);
        }
    }

    static Function<String[], Entry<String, Float>> mapToEntry = strings -> {
        final var word = normalize(strings[0].trim());
        final var sentiment = strings[1].trim(); // [p|e|n]
        final var score = sentiment.equals("p") ? 1
                : sentiment.equals("n") ? -1
                : 0f;
        return new SimpleEntry<>(word, score);
    };
}
