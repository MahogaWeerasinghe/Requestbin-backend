public class ResponseData {
    private int statusCode;
    private String statusMessage;
    private String body;

    public ResponseData(int statusCode, String statusMessage, String body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.body = body;
    }

    public String toJson() {
        return String.format(
                "{\"statusCode\":%d,\"statusMessage\":\"%s\",\"body\":\"%s\"}",
                statusCode,
                statusMessage.replace("\"", "\\\""),
                body.replace("\"", "\\\"")
        );
    }
}
