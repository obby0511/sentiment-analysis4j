package obby0511.sentiment.analysis;

import lombok.Getter;

@Getter
public class LanguageResponse {
    private final float score;
    private final double magnitude;

    public LanguageResponse(double magnitude) {
        this.magnitude = magnitude;
        this.score = magnitude < 0 ? -1
                : magnitude > 0 ? 1
                : 0;
    }
}
