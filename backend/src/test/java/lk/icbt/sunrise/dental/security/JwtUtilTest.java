package lk.icbt.sunrise.dental.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private static final String TEST_SECRET = "test-only-secret-key-must-be-at-least-256-bits-long-for-hs512";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(TEST_SECRET, 3_600_000L);
    }

    @Test
    void generateToken_thenExtractUsername_returnsOriginalUsername() {
        String token = jwtUtil.generateToken("reception", "STAFF");

        assertEquals("reception", jwtUtil.extractUsername(token));
    }

    @Test
    void isTokenValid_returnsTrue_forFreshlyGeneratedToken() {
        String token = jwtUtil.generateToken("admin", "ADMIN");

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_returnsFalse_forGarbageToken() {
        assertFalse(jwtUtil.isTokenValid("this-is-not-a-valid-jwt"));
    }

    @Test
    void isTokenValid_returnsFalse_forExpiredToken() {
        JwtUtil shortLived = new JwtUtil(TEST_SECRET, -1_000L);
        String token = shortLived.generateToken("reception", "STAFF");

        assertFalse(shortLived.isTokenValid(token));
    }
}
