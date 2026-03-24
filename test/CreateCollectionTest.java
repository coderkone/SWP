import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateCollectionTest {

    private WebDriver driver;
    private WebDriverWait wait;
    
    // Thay đổi port và domain cho phù hợp với môi trường local của bạn
    private final String BASE_URL = "http://localhost:8080/DevQuery"; 

    @BeforeEach
    public void setUp() {
        // Thiết lập đường dẫn đến file chromedriver.exe nếu chưa cấu hình biến môi trường
        // System.setProperty("webdriver.chrome.driver", "path/to/chromedriver.exe");
        
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // ⚠️ BƯỚC QUAN TRỌNG: Đăng nhập trước khi vào trang Saves
        // Do CreateCollectionController yêu cầu session("user")
        driver.get(BASE_URL + "/auth/login.jsp");
        driver.findElement(By.name("username")).sendKeys("testuser"); // Đổi user test của bạn
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.id("btnLogin")).click();

        // Sau khi đăng nhập, điều hướng đến trang Saves
        driver.get(BASE_URL + "/saves");
    }

    @Test
    @Order(1)
    @DisplayName("Tạo Collection thành công với tên hợp lệ")
    public void testCreateCollectionSuccess() {
        String testCollectionName = "Auto Test Java Tips " + System.currentTimeMillis();

        // 1. Nhấn nút "Create new list" để mở Modal
        WebElement createBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Create new list')]")));
        createBtn.click();

        // 2. Đợi Modal hiển thị và nhập tên Collection
        WebElement inputName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("listName")));
        inputName.sendKeys(testCollectionName);

        // 3. Nhấn nút Submit trong Form
        WebElement submitBtn = driver.findElement(By.xpath("//form[contains(@action, '/saves/create')]//button[@type='submit']"));
        submitBtn.click();

        // 4. Đợi trang reload và kiểm tra xem tên Collection mới có nằm trong Sidebar không
        wait.until(ExpectedConditions.urlContains("/saves"));
        List<WebElement> collectionItems = driver.findElements(By.className("collection-name-text"));
        
        boolean isCreated = collectionItems.stream()
                .anyMatch(item -> item.getText().trim().equals(testCollectionName));

        assertTrue(isCreated, "Collection mới tạo không xuất hiện trên Sidebar!");
    }

    @Test
    @Order(2)
    @DisplayName("Kiểm tra chặn tạo Collection khi để trống tên")
    public void testCreateCollectionEmptyName() {
        // 1. Mở Modal
        WebElement createBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Create new list')]")));
        createBtn.click();

        // 2. Đảm bảo ô input đang trống
        WebElement inputName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("listName")));
        inputName.clear();

        // 3. Nhấn Submit
        WebElement submitBtn = driver.findElement(By.xpath("//form[contains(@action, '/saves/create')]//button[@type='submit']"));
        submitBtn.click();

        // 4. Kiểm tra HTML5 Validation (Do thuộc tính 'required' trong JSP)
        // Trình duyệt sẽ chặn không cho submit form, nên URL vẫn giữ nguyên là /saves thay vì nhảy sang action
        String validationMessage = inputName.getAttribute("validationMessage");
        
        assertFalse(validationMessage.isEmpty(), "Trình duyệt không hiển thị cảnh báo validation!");
        assertTrue(driver.getCurrentUrl().endsWith("/saves"), "Form đã bị submit mặc dù tên rỗng!");
    }

    @Test
    @Order(3)
    @DisplayName("Kiểm tra chức năng đóng Modal bằng nút Cancel")
    public void testCancelCreateCollection() {
        // 1. Mở Modal
        WebElement createBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Create new list')]")));
        createBtn.click();

        // 2. Nhấn nút Cancel
        WebElement cancelBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//form[contains(@action, '/saves/create')]//button[contains(text(), 'Cancel')]")));
        cancelBtn.click();

        // 3. Kiểm tra Modal đã bị ẩn (display = none)
        WebElement modal = driver.findElement(By.id("createListModal"));
        wait.until(ExpectedConditions.invisibilityOf(modal));

        assertFalse(modal.isDisplayed(), "Modal không bị đóng sau khi nhấn Cancel!");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit(); // Đóng trình duyệt sau mỗi test
        }
    }
}