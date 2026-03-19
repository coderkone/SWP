package util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple HTML Sanitizer for answer content.
 * Removes dangerous tags and attributes while preserving safe formatting.
 * 
 * Allowed tags: p, br, strong, b, em, i, u, h1, h2, h3, h4, h5, h6,
 *               ul, ol, li, code, pre, blockquote, hr, div, span
 * Allowed attributes: title, class
 */
public class HtmlSanitizer {
    
    // Define allowed tags
    private static final Set<String> ALLOWED_TAGS = new HashSet<>();
    static {
        ALLOWED_TAGS.addAll(java.util.Arrays.asList(
            "p", "br", "strong", "b", "em", "i", "u", 
            "h1", "h2", "h3", "h4", "h5", "h6",
            "ul", "ol", "li", "code", "pre", "blockquote", 
            "hr", "div", "span"
        ));
    }
    
    // Define allowed attributes for specific tags
    private static final Set<String> COMMON_ATTRS = new HashSet<>(java.util.Arrays.asList("title", "class"));
    
    /**
     * Sanitize HTML content to prevent XSS attacks
     * @param html The HTML content to sanitize
     * @return Sanitized HTML content
     */
    public static String sanitize(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }
        
        // Remove script tags and their content
        html = html.replaceAll("(?i)<script[^>]*>.*?</script>", "");
        
        // Remove other dangerous tags
        html = html.replaceAll("(?i)<iframe[^>]*>.*?</iframe>", "");
        html = html.replaceAll("(?i)<object[^>]*>.*?</object>", "");
        html = html.replaceAll("(?i)<embed[^>]*>", "");
        html = html.replaceAll("(?i)<style[^>]*>.*?</style>", "");
        
        // Remove event handler attributes (onclick, onload, etc.)
        html = html.replaceAll("(?i)on\\w+\\s*=\\s*[\"'][^\"']*[\"']", "");
        html = html.replaceAll("(?i)on\\w+\\s*=\\s*[^\\s>]+", "");
        
        // Remove dangerous protocols (javascript:, data:, vbscript:)
        html = html.replaceAll("(?i)(href|src|data)\\s*=\\s*[\"']\\s*(javascript|data|vbscript)", "$1=\"");
        html = html.replaceAll("(?i)(href|src|data)\\s*=\\s*([^\\s>]*javascript)", "$1=\"");
        
        // Clean up remaining tags
        html = cleanupTags(html);
        
        return html.trim();
    }
    
    /**
     * Remove disallowed tags but keep their content
     */
    private static String cleanupTags(String html) {
        // Match all HTML tags
        Pattern pattern = Pattern.compile("<(/?)([a-zA-Z][a-zA-Z0-9]*)([^>]*)>");
        Matcher matcher = pattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String isClosing = matcher.group(1);
            String tagName = matcher.group(2).toLowerCase();
            String attributes = matcher.group(3);
            
            if (ALLOWED_TAGS.contains(tagName)) {
                // Sanitize attributes for allowed tags
                String sanitizedAttrs = sanitizeAttributes(attributes);
                String replacement = "<" + isClosing + tagName + sanitizedAttrs + ">";
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            } else {
                // Remove disallowed tags but keep content
                matcher.appendReplacement(sb, "");
            }
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }
    
    /**
     * Sanitize attributes for a specific tag
     */
    private static String sanitizeAttributes(String attributes) {
        if (attributes == null || attributes.trim().isEmpty()) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        
        // Parse attributes
        Pattern attrPattern = Pattern.compile("(\\w+)\\s*=\\s*[\"']?([^\"'\\s>]+)[\"']?");
        Matcher matcher = attrPattern.matcher(attributes);
        
        while (matcher.find()) {
            String attrName = matcher.group(1).toLowerCase();
            String attrValue = matcher.group(2);
            
            // Check if attribute is allowed for this tag
            boolean allowed = false;
            
            if (COMMON_ATTRS.contains(attrName)) {
                allowed = true;
            }
            
            if (allowed) {
                result.append(" ").append(attrName).append("=\"").append(escapeAttribute(attrValue)).append("\"");
            }
        }
        
        return result.toString();
    }
    
    /**
     * Escape special characters in attribute values
     */
    private static String escapeAttribute(String value) {
        if (value == null) {
            return "";
        }
        
        return value.replace("&", "&amp;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
    }
}
