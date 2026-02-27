<%-- 
    Document   : footer.jsp
    Created on : Feb 13, 2026
    Author     : DevQuery Team
    Description: Footer component for DevQuery
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    /* FOOTER STYLES */
    .footer-devquery {
        background-color: #2c3e50;
        color: #ecf0f1;
        margin-top: 60px;
        padding: 40px 0 20px 0;
        border-top: 1px solid #34495e;
    }

    .footer-content {
        max-width: 1264px;
        margin: 0 auto;
        padding: 0 20px;
    }

    .footer-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 30px;
        margin-bottom: 30px;
    }

    .footer-section h5 {
        font-size: 14px;
        font-weight: 600;
        text-transform: uppercase;
        color: #f48024;
        margin-bottom: 15px;
        letter-spacing: 0.5px;
    }

    .footer-section ul {
        list-style: none;
        padding: 0;
        margin: 0;
    }

    .footer-section ul li {
        margin-bottom: 10px;
    }

    .footer-section ul li a {
        color: #bdc3c7;
        text-decoration: none;
        font-size: 13px;
        transition: color 0.3s ease;
    }

    .footer-section ul li a:hover {
        color: #f48024;
    }

    .footer-bottom {
        border-top: 1px solid #34495e;
        padding-top: 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
        gap: 20px;
    }

    .footer-logo {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: bold;
        color: #f48024;
        font-size: 16px;
    }

    .footer-logo img {
        width: 28px;
        height: 28px;
    }

    .footer-copyright {
        font-size: 12px;
        color: #95a5a6;
    }

    .footer-social {
        display: flex;
        gap: 15px;
    }

    .footer-social a {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 36px;
        height: 36px;
        background-color: #34495e;
        color: #ecf0f1;
        border-radius: 4px;
        text-decoration: none;
        transition: all 0.3s ease;
        font-size: 16px;
    }

    .footer-social a:hover {
        background-color: #f48024;
        color: #ffffff;
    }

    /* Mobile Responsive */
    @media (max-width: 768px) {
        .footer-grid {
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
        }

        .footer-bottom {
            flex-direction: column;
            text-align: center;
        }

        .footer-social {
            justify-content: center;
        }
    }

    @media (max-width: 480px) {
        .footer-grid {
            grid-template-columns: 1fr;
        }

        .footer-bottom {
            flex-direction: column;
        }
    }
</style>

<footer class="footer-devquery">
    <div class="footer-content">
        <div class="footer-grid">
            <!-- Company Section -->
            <div class="footer-section">
                <h5>Company</h5>
                <ul>
                    <li><a href="#">About</a></li>
                    <li><a href="#">Blog</a></li>
                    <li><a href="#">Careers</a></li>
                    <li><a href="#">Press</a></li>
                    <li><a href="#">Contact</a></li>
                </ul>
            </div>

            <!-- Products Section -->
            <div class="footer-section">
                <h5>Products</h5>
                <ul>
                    <li><a href="#">DevQuery</a></li>
                    <li><a href="#">Documentation</a></li>
                    <li><a href="#">API Reference</a></li>
                    <li><a href="#">Status Page</a></li>
                    <li><a href="#">Roadmap</a></li>
                </ul>
            </div>

            <!-- Community Section -->
            <div class="footer-section">
                <h5>Community</h5>
                <ul>
                    <li><a href="#">Questions</a></li>
                    <li><a href="#">Tags</a></li>
                    <li><a href="#">Users</a></li>
                    <li><a href="#">Badges</a></li>
                    <li><a href="#">Moderators</a></li>
                </ul>
            </div>

            <!-- Resources Section -->
            <div class="footer-section">
                <h5>Resources</h5>
                <ul>
                    <li><a href="#">Help Center</a></li>
                    <li><a href="#">Privacy Policy</a></li>
                    <li><a href="#">Terms of Service</a></li>
                    <li><a href="#">Code of Conduct</a></li>
                    <li><a href="#">Sitemap</a></li>
                </ul>
            </div>
        </div>

        <!-- Footer Bottom -->
        <div class="footer-bottom">
            <div class="footer-logo">
                <img src="${pageContext.request.contextPath}/assets/img/logo.png" alt="DevQuery">
                <span>DevQuery</span>
            </div>

            <div class="footer-copyright">
                &copy; 2026 DevQuery. All rights reserved. | Made with <i class="fa-solid fa-heart" style="color: #f48024;"></i> by DevQuery Team
            </div>

            <div class="footer-social">
                <a href="#" title="GitHub" target="_blank">
                    <i class="fa-brands fa-github"></i>
                </a>
                <a href="#" title="Twitter" target="_blank">
                    <i class="fa-brands fa-twitter"></i>
                </a>
                <a href="#" title="Facebook" target="_blank">
                    <i class="fa-brands fa-facebook"></i>
                </a>
                <a href="#" title="LinkedIn" target="_blank">
                    <i class="fa-brands fa-linkedin"></i>
                </a>
            </div>
        </div>
    </div>
</footer>
