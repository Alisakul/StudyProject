import net.bytebuddy.asm.Advice;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class CartTests {

    public static WebDriver driver;
    public static WebDriverWait wait;
    Actions builder = new Actions(driver);

    @BeforeAll
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }


    @AfterAll
    public static void tearDown() throws IOException {
        var sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(sourceFile, new File("screenshots/scrnsht.png"));
        driver.quit();
    }


    //проверка правильности расчета общей суммы за товар в корзине
    @Test
    public void resultPrice() {
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        var addItem = By.cssSelector("ul.products li:nth-child(6) a.add_to_cart_button");
        var sendKeys = String.valueOf(10);
        driver.findElement(addItem).click();
        driver.findElement(By.cssSelector("li#menu-item-29 a")).click();
        driver.findElement(By.cssSelector("div.quantity input")).clear();
        driver.findElement(By.cssSelector("div.quantity input")).sendKeys(sendKeys);
        driver.findElement(By.cssSelector("div.quantity input")).sendKeys(Keys.ENTER);
        var itemPrice = driver.findElement(By.cssSelector("td.product-price span.woocommerce-Price-amount")).getText();
        var expectedPrice = String.valueOf((Float.valueOf(itemPrice.substring(0, itemPrice.length() - 4))).floatValue() * (Float.valueOf(sendKeys)));
        var actualPrice = driver.findElement(By.cssSelector("td.product-subtotal bdi")).getText();

        Assert.assertEquals("Расчет общей стоимости 10 единиц товара в корзине не соответствует расчёту",
                expectedPrice.substring(0, expectedPrice.length()-2), actualPrice.substring(0, actualPrice.length()-4));
    }

    //проверка правильности расчета общей суммы за товар в корзине
    @Test
    public void totalPrice() {
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        var addItem1 = By.cssSelector("ul.products li:nth-child(6) a.add_to_cart_button");
        var addItem2 = By.cssSelector("ul.products li:nth-child(5) a.add_to_cart_button");
        var sendKeys = String.valueOf(10);
        driver.findElement(addItem1).click();
        driver.findElement(addItem2).click();
        var moreInfo1 = By.cssSelector("ul.products li:nth-child(5) a.added_to_cart");
        driver.findElement(moreInfo1).click();

        var itemPrice1 = driver.findElement(By.cssSelector("td.product-price span.woocommerce-Price-amount")).getText();

        var expectedPrice = String.valueOf((Float.valueOf(itemPrice1.substring(0, itemPrice1.length() - 4))).floatValue() * (Float.valueOf(sendKeys)));
        var actualPrice = driver.findElement(By.cssSelector("td.product-subtotal bdi")).getText();

        Assert.assertEquals("Расчет общей стоимости 10 единиц товара в корзине не соответствует расчёту",
                expectedPrice.substring(0, expectedPrice.length()-2), actualPrice.substring(0, actualPrice.length()-4));



    }
}
