package http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class HttpVersionTest {
    @Test
    void getMostCompatibleVersionExactMatch() {
        HttpVersion httpVersion = null;
        try {
            httpVersion = HttpVersion.getMostCompatibleVersion("HTTP/1.1");
        } catch (BadHttpVersionException e) {
            fail();
        }
        assertNotNull(httpVersion);
        assertEquals(httpVersion, HttpVersion.HTTP_1_1);
    }

    @Test
    void getMostCompatibleVersionBadFormat() {
        HttpVersion httpVersion = null;
        try {
            httpVersion = HttpVersion.getMostCompatibleVersion("http/1.1");
            fail();
        } catch (BadHttpVersionException e) {

        }
    }

    @Test
    void getMostCompatibleVersionHigherVersion() {
        HttpVersion httpVersion = null;
        try {
            httpVersion = HttpVersion.getMostCompatibleVersion("HTTP/1.2");
            assertNotNull(httpVersion);
            assertEquals(httpVersion, HttpVersion.HTTP_1_1);
        } catch (BadHttpVersionException e) {
            fail();
        }
    }
}
