package util;

public class Constants {
    //Google
    public static final String GOOGLE_CLIENT_ID = "506908692355-hjo25b3i4fn65okidkdsc3qr3sn60pcl.apps.googleusercontent.com";
    public static final String GOOGLE_CLIENT_SECRET = "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";
    public static final String GOOGLE_REDIRECT_URI = "http://localhost:8080/DevQuery/auth/google/callback";
    
    public static final String GOOGLE_LINK_GET_TOKEN = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_LINK_GET_USER_INFO = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json";
    public static final String GOOGLE_GRANT_TYPE = "authorization_code";
    
    //Github
    public static final String GITHUB_CLIENT_ID = "Ov23livFRVqSbJOw4A8F";
    public static final String GITHUB_CLIENT_SECRET = "84819341d711fa551c51c31d2e030485c9377166";
    public static final String GITHUB_REDIRECT_URI = "http://localhost:8080/DevQuery/auth/github/callback";
    
    public static final String GITHUB_LINK_GET_TOKEN = "https://github.com/login/oauth/access_token";
    public static final String GITHUB_LINK_GET_USER_INFO = "https://api.github.com/user";
}