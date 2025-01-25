import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestData {
    private String url;
    private String method;
    private String body;

    public RequestData(String url, String method, String body) {
        this.url = url;
        this.method = method.toUpperCase();
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getBody() {
        return body;
    }

    public static RequestData fromJson(String json) {
        String url = extractField(json, "url");
        String method = extractField(json, "method");
        String body = extractField(json, "body");
        return new RequestData(url, method, body);
    }

    private static String extractField(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\"\\s*:\\s*\"(.*?)\"";
        Matcher matcher = Pattern.compile(pattern).matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }
}
