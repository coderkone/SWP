<%-- 
    Document   : footer.jsp
    Created on : Feb 13, 2026
    Author     : DevQuery Team
    Description: Footer component for DevQuery
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .footer-devquery {
        background-color: #242729;
        color: #ffffff;
        margin-top: 40px;
        padding: 25px 0 15px 0;
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
        margin-bottom: 15px;
    }

    .footer-section h5 {
        color: #f48024;
        font-size: 14px;
        font-weight: 600;
        text-transform: uppercase;
        margin-bottom: 10px;
        letter-spacing: 0.5px;
    }

    .footer-section ul {
        list-style: none;
        padding: 0;
        margin: 0;
    }

    .footer-section ul li {
        margin-bottom: 6px;
    }

    .footer-section ul li span {
        color: #ffffff;
        font-size: 13px;
    }

    .footer-bottom {
        border-top: 1px solid #34495e;
        padding-top: 15px;
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
        font-size: 16px;
    }

    .footer-logo img {
        width: 28px;
        height: 28px;
    }

    .footer-copyright {
        font-size: 12px;
        color: #ffffff;
    }

    .footer-social {
        display: flex;
        gap: 15px;
    }

    .footer-social span {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 36px;
        height: 36px;
        background-color: #34495e;
        color: #ffffff;
        border-radius: 4px;
        font-size: 16px;
    }

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
                    <li><span>About</span></li>
                    <li><span>Blog</span></li>
                    <li><span>Careers</span></li>
                    <li><span>Press</span></li>
                    <li><span>Contact</span></li>
                </ul>
            </div>

            <!-- Products Section -->
            <div class="footer-section">
                <h5>Products</h5>
                <ul>
                    <li><span>DevQuery</span></li>
                    <li><span>Documentation</span></li>
                    <li><span>API Reference</span></li>
                    <li><span>Status Page</span></li>
                    <li><span>Roadmap</span></li>
                </ul>
            </div>

            <!-- Community Section -->
            <div class="footer-section">
                <h5>Community</h5>
                <ul>
                    <li><span>Questions</span></li>
                    <li><span>Tags</span></li>
                    <li><span>Users</span></li>
                    <li><span>Badges</span></li>
                    <li><span>Moderators</span></li>
                </ul>
            </div>

            <!-- Resources Section -->
            <div class="footer-section">
                <h5>Resources</h5>
                <ul>
                    <li><span>Help Center</span></li>
                    <li><span>Privacy Policy</span></li>
                    <li><span>Terms of Service</span></li>
                    <li><span>Code of Conduct</span></li>
                    <li><span>Sitemap</span></li>
                </ul>
            </div>
        </div>

        <!-- Footer Bottom -->
        <div class="footer-bottom">
            <div class="footer-logo">
                <img src="${pageContext.request.contextPath}/assets/img/LogoDQ.png" 
                 alt="DevQuery" width="30" height="30" class="d-inline-block align-text-top me-2">
                <span style="color: #f48024; font-weight: bold;">DevQuery</span>
            </div>

            <div class="footer-copyright">
                &copy; 2026 DevQuery. All rights reserved. | Made with <i class="fa-solid fa-heart" style="color: #f48024;"></i> by DevQuery Team
            </div>

            <div class="footer-social">
                <span title="GitHub"><i class="fa-brands fa-github"></i></span>
                <span title="Twitter"><i class="fa-brands fa-twitter"></i></span>
                <span title="Facebook"><i class="fa-brands fa-facebook"></i></span>
                <span title="LinkedIn"><i class="fa-brands fa-linkedin"></i></span>
            </div>
        </div>
    </div>
</footer>
