package http;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpParserTest {
    private HttpParser httpParser;

    @BeforeAll
    public void beforeClass() {
        httpParser = new HttpParser();
    }

    @Test
    void parseHttpRequest() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateValidGetTestCase());
        } catch (HttpParsingException e) {
            fail(e);
        }
        assertNotNull(httpRequest);
        assertEquals(httpRequest.getMethod(), HttpMethod.GET);
        assertEquals(httpRequest.getRequestTarget(), "/");
        assertEquals(httpRequest.getOriginalHttpVersion(), "HTTP/1.1");
        assertEquals(httpRequest.getMostCompatibleHttpVersion(), HttpVersion.HTTP_1_1);
    }

    @Test
    void parseHttpBadRequest() {
        try {
            HttpRequest httpRequest = httpParser.parseHttpRequest(generateBadRequestGetTestCase());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
        }
    }

    @Test
    void parseHttpBadRequestLongMethodName() {
        try {
            HttpRequest httpRequest = httpParser.parseHttpRequest(generateBadRequestLongMethodNameGetTestCase());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
        }
    }

    @Test
    void parseHttpBadRequestMoreThanThreeRequestLines() {
        try {
            HttpRequest httpRequest = httpParser.parseHttpRequest(generateBadRequestMoreThanThreeRequestLinesGetTestCase());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    @Test
    void parseHttpBadRequestEmptyRequestLine() {
        try {
            HttpRequest httpRequest = httpParser.parseHttpRequest(generateBadRequestEmptyRequestLinesGetTestCase());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    @Test
    void parseHttpBadRequestNoLineFeed() {
        try {
            HttpRequest httpRequest = httpParser.parseHttpRequest(generateBadRequestNoLineFeedGetTestCase());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    @Test
    void parseHttpVersionBadRequest() {
        try {
            HttpRequest httpRequest = httpParser.parseHttpRequest(generateBadHttpVersionGetTestCase());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    @Test
    void parseHttpVersionUnsupportedRequest() {
        try {
            HttpRequest httpRequest = httpParser.parseHttpRequest(generateUnsupportedHttpVersionGetTestCase());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }
    }

    @Test
    void parseHigherHttpVersionRequest() {
        try {
            HttpRequest httpRequest = httpParser.parseHttpRequest(generateHigherHttpVersionGetTestCase());
            assertNotNull(httpRequest);
            assertEquals(httpRequest.getMostCompatibleHttpVersion(), HttpVersion.HTTP_1_1);
            assertEquals(httpRequest.getOriginalHttpVersion(), "HTTP/1.2");
        } catch (HttpParsingException e) {
            fail();
        }
    }

    private InputStream generateValidGetTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" + "Host: localhost:8080\r\n" + "Connection: keep-alive\r\n"
                + "sec-ch-ua: \"Google Chrome\";v=\"125\", \"Chromium\";v=\"125\", \"Not.A/Brand\";v=\"24\"\r\n"
                + "sec-ch-ua-mobile: ?0\r\n" + "sec-ch-ua-platform: \"macOS\"\r\n" + "Upgrade-Insecure-Requests: 1\r\n"
                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36\r\n"
                + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n"
                + "Sec-Fetch-Site: none\r\n" + "Sec-Fetch-Mode: navigate\r\n" + "Sec-Fetch-User: ?1\r\n"
                + "Sec-Fetch-Dest: document\r\n" + "Accept-Encoding: gzip, deflate, br, zstd\r\n"
                + "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8\r\n"
                + "Cookie: csrftoken=fFv6qMtTw76aKb0JQuKMBlkP0kkBaLWc; sessionid=vst659rrtvkc4ln30ek9kl2yocv2s3cj\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(StandardCharsets.US_ASCII)
        );

        return inputStream;
    }

    private InputStream generateBadRequestGetTestCase() {
        String rawData = "Get / HTTP/1.1\r\n" + "Host: localhost:8080\r\n" + "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8\r\n"
                + "Cookie: csrftoken=fFv6qMtTw76aKb0JQuKMBlkP0kkBaLWc; sessionid=vst659rrtvkc4ln30ek9kl2yocv2s3cj\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(StandardCharsets.US_ASCII)
        );

        return inputStream;
    }

    private InputStream generateBadRequestLongMethodNameGetTestCase() {
        String rawData = "GETTT / HTTP/1.1\r\n" + "Host: localhost:8080\r\n" + "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8\r\n"
                + "Cookie: csrftoken=fFv6qMtTw76aKb0JQuKMBlkP0kkBaLWc; sessionid=vst659rrtvkc4ln30ek9kl2yocv2s3cj\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(StandardCharsets.US_ASCII)
        );

        return inputStream;
    }

    private InputStream generateBadRequestMoreThanThreeRequestLinesGetTestCase() {
        String rawData = "GET / AAAA HTTP/1.1\r\n" + "Host: localhost:8080\r\n" + "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8\r\n"
                + "Cookie: csrftoken=fFv6qMtTw76aKb0JQuKMBlkP0kkBaLWc; sessionid=vst659rrtvkc4ln30ek9kl2yocv2s3cj\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(StandardCharsets.US_ASCII)
        );

        return inputStream;
    }

    private InputStream generateBadRequestEmptyRequestLinesGetTestCase() {
        String rawData = "\r\n" + "Host: localhost:8080\r\n" + "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8\r\n"
                + "Cookie: csrftoken=fFv6qMtTw76aKb0JQuKMBlkP0kkBaLWc; sessionid=vst659rrtvkc4ln30ek9kl2yocv2s3cj\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(StandardCharsets.US_ASCII)
        );

        return inputStream;
    }

    private InputStream generateBadRequestNoLineFeedGetTestCase() {
        String rawData = "GET / HTTP/1.1\r" + "Host: localhost:8080\r\n" + "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8\r\n"
                + "Cookie: csrftoken=fFv6qMtTw76aKb0JQuKMBlkP0kkBaLWc; sessionid=vst659rrtvkc4ln30ek9kl2yocv2s3cj\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(StandardCharsets.US_ASCII)
        );

        return inputStream;
    }

    private InputStream generateBadHttpVersionGetTestCase() {
        String rawData = "GET / HTP/1.1\r\n" + "Host: localhost:8080\r\n" + "Connection: keep-alive\r\n"
                + "sec-ch-ua: \"Google Chrome\";v=\"125\", \"Chromium\";v=\"125\", \"Not.A/Brand\";v=\"24\"\r\n"
                + "sec-ch-ua-mobile: ?0\r\n" + "sec-ch-ua-platform: \"macOS\"\r\n" + "Upgrade-Insecure-Requests: 1\r\n"
                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36\r\n"
                + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n"
                + "Sec-Fetch-Site: none\r\n" + "Sec-Fetch-Mode: navigate\r\n" + "Sec-Fetch-User: ?1\r\n"
                + "Sec-Fetch-Dest: document\r\n" + "Accept-Encoding: gzip, deflate, br, zstd\r\n"
                + "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8\r\n"
                + "Cookie: csrftoken=fFv6qMtTw76aKb0JQuKMBlkP0kkBaLWc; sessionid=vst659rrtvkc4ln30ek9kl2yocv2s3cj\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(StandardCharsets.US_ASCII)
        );

        return inputStream;
    }

    private InputStream generateUnsupportedHttpVersionGetTestCase() {
        String rawData = "GET / HTTP/2.1\r\n" + "Host: localhost:8080\r\n" + "Connection: keep-alive\r\n"
                + "sec-ch-ua: \"Google Chrome\";v=\"125\", \"Chromium\";v=\"125\", \"Not.A/Brand\";v=\"24\"\r\n"
                + "sec-ch-ua-mobile: ?0\r\n" + "sec-ch-ua-platform: \"macOS\"\r\n" + "Upgrade-Insecure-Requests: 1\r\n"
                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36\r\n"
                + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n"
                + "Sec-Fetch-Site: none\r\n" + "Sec-Fetch-Mode: navigate\r\n" + "Sec-Fetch-User: ?1\r\n"
                + "Sec-Fetch-Dest: document\r\n" + "Accept-Encoding: gzip, deflate, br, zstd\r\n"
                + "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8\r\n"
                + "Cookie: csrftoken=fFv6qMtTw76aKb0JQuKMBlkP0kkBaLWc; sessionid=vst659rrtvkc4ln30ek9kl2yocv2s3cj\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(StandardCharsets.US_ASCII)
        );

        return inputStream;
    }

    private InputStream generateHigherHttpVersionGetTestCase() {
        String rawData = "GET / HTTP/1.2\r\n" + "Host: localhost:8080\r\n" + "Connection: keep-alive\r\n"
                + "sec-ch-ua: \"Google Chrome\";v=\"125\", \"Chromium\";v=\"125\", \"Not.A/Brand\";v=\"24\"\r\n"
                + "sec-ch-ua-mobile: ?0\r\n" + "sec-ch-ua-platform: \"macOS\"\r\n" + "Upgrade-Insecure-Requests: 1\r\n"
                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36\r\n"
                + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n"
                + "Sec-Fetch-Site: none\r\n" + "Sec-Fetch-Mode: navigate\r\n" + "Sec-Fetch-User: ?1\r\n"
                + "Sec-Fetch-Dest: document\r\n" + "Accept-Encoding: gzip, deflate, br, zstd\r\n"
                + "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8\r\n"
                + "Cookie: csrftoken=fFv6qMtTw76aKb0JQuKMBlkP0kkBaLWc; sessionid=vst659rrtvkc4ln30ek9kl2yocv2s3cj\r\n" + "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(StandardCharsets.US_ASCII)
        );

        return inputStream;
    }
}