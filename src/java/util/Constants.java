package util;

public class Constants {
    // ==================== GOOGLE OAUTH ====================
    public static final String GOOGLE_CLIENT_ID = "506908692355-hjo25b3i4fn65okidkdsc3qr3sn60pcl.apps.googleusercontent.com";
    
    // Load from environment variable (NEVER hardcode secrets in production)
    public static final String GOOGLE_CLIENT_SECRET = getGoogleClientSecret();
    
    // Redirect URI configurable via environment variable, defaults to localhost for development
    public static final String GOOGLE_REDIRECT_URI = getGoogleRedirectUri();
    
    public static final String GOOGLE_LINK_GET_TOKEN = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_LINK_GET_USER_INFO = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json";
    public static final String GOOGLE_GRANT_TYPE = "authorization_code";
    
    // ==================== GITHUB OAUTH ====================
    public static final String GITHUB_CLIENT_ID = "Ov23livFRVqSbJOw4A8F";
    
    // Load from environment variable (NEVER hardcode secrets in production)
    public static final String GITHUB_CLIENT_SECRET = getGithubClientSecret();
    
    // Redirect URI configurable via environment variable, defaults to localhost for development
    public static final String GITHUB_REDIRECT_URI = getGithubRedirectUri();
    
    public static final String GITHUB_LINK_GET_TOKEN = "https://github.com/login/oauth/access_token";
    public static final String GITHUB_LINK_GET_USER_INFO = "https://api.github.com/user";
    
    
    // ==================== CONFIGURATION LOADERS ====================
    
    /**
     * Load Google OAuth client secret from environment variable.
     * CRITICAL SECURITY: Secrets should NEVER be hardcoded in source code.
     * Must be set via GOOGLE_CLIENT_SECRET environment variable in production.
     */
    private static String getGoogleClientSecret() {
        String secret = System.getenv("GOOGLE_CLIENT_SECRET");
        if (secret != null && !secret.trim().isEmpty()) {
            return secret.trim();
        }
        
        // Development fallback (ONLY for local development with private repos)
        // In production, you MUST set GOOGLE_CLIENT_SECRET environment variable
        System.out.println("WARNING: GOOGLE_CLIENT_SECRET environment variable not set - using development default (SECURITY RISK)");
        return "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";
    }
    
    /**
     * Load GitHub OAuth client secret from environment variable.
     * CRITICAL SECURITY: Secrets should NEVER be hardcoded in source code.
     * Must be set via GITHUB_CLIENT_SECRET environment variable in production.
     */
    private static String getGithubClientSecret() {
        String secret = System.getenv("GITHUB_CLIENT_SECRET");
        if (secret != null && !secret.trim().isEmpty()) {
            return secret.trim();
        }
        
        // Development fallback (ONLY for local development with private repos)
        // In production, you MUST set GITHUB_CLIENT_SECRET environment variable
        System.out.println("WARNING: GITHUB_CLIENT_SECRET environment variable not set - using development default (SECURITY RISK)");
        return "84819341d711fa551c51c31d2e030485c9377166";
    }
    
    /**
     * Load Google OAuth redirect URI from environment variable.
     * Allows deployment on different servers/ports/domains without code changes.
     */
    private static String getGoogleRedirectUri() {
        String redirectUri = System.getenv("GOOGLE_REDIRECT_URI");
        if (redirectUri != null && !redirectUri.trim().isEmpty()) {
            return redirectUri.trim();
        }
        
        // Development default (must match Google OAuth settings)
        return "http://localhost:8080/DevQuery/auth/google/callback";
    }
    
    /**
     * Load GitHub OAuth redirect URI from environment variable.
     * Allows deployment on different servers/ports/domains without code changes.
     */
    private static String getGithubRedirectUri() {
        String redirectUri = System.getenv("GITHUB_REDIRECT_URI");
        if (redirectUri != null && !redirectUri.trim().isEmpty()) {
            return redirectUri.trim();
        }
        
        // Development default (must match GitHub OAuth settings)
        return "http://localhost:8080/DevQuery/auth/github/callback";
    }
}