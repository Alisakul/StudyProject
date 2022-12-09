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


public class CatalogTests {

    public static WebDriver driver;
    public static WebDriverWait wait;

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

    //проверка переходов в разделы каталога
    @Test
    public void testCatalogSections() {

        driver.navigate().to("http://intershop5.skillbox.ru/");

        var catalogButton = By.cssSelector("ul#menu-primary-menu li#menu-item-46");

        var сategory1 = By.cssSelector("ul.product-categories li:nth-of-type(1) a");
        var сategory2 = By.cssSelector("ul.product-categories li:nth-of-type(2) a");
        
          //остальные категории для диплома не делать, достаточно двух первых
        var сategory3 = By.cssSelector("ul.product-categories li:nth-of-type(3) a");
        var сategory4 = By.cssSelector("ul.product-categories li:nth-of-type(4) a");
        var сategory5 = By.cssSelector("ul.product-categories li:nth-of-type(5) a");
        var сategory6 = By.cssSelector("ul.product-categories li:nth-of-type(6) a");
        var сategory7 = By.cssSelector("ul.product-categories li:nth-of-type(7) a");
        var сategory8 = By.cssSelector("ul.product-categories li:nth-of-type(8) a");
        var сategory9 = By.cssSelector("ul.product-categories li:nth-of-type(9) a");
        var сategory10 = By.cssSelector("ul.product-categories li:nth-of-type(10) a");
        var сategory11 = By.cssSelector("ul.product-categories li:nth-of-type(11) a");
        var сategory12 = By.cssSelector("ul.product-categories li:nth-of-type(12) a");
        var сategory13 = By.cssSelector("ul.product-categories li:nth-of-type(13) a");


        driver.findElement(catalogButton).click();
        var actualtittle = driver.findElement(By.cssSelector("h1.entry-title")).getText();
        var breadcrumpTittleCatalog = driver.findElement(By.cssSelector("div.woocommerce-breadcrumb span")).getText();

        Assert.assertEquals("Наименование заголовка h1 не соответствует выбранной категории товаров при переходе в раздел каталога",
                "КАТАЛОГ", actualtittle);
        Assert.assertEquals("Заголовок 'хлебных крошек' не соответствует наименованию категории при переходе в раздел каталога",
                "Каталог", breadcrumpTittleCatalog);

        var categoryName1 = driver.findElement(By.cssSelector("ul.product-categories li:nth-of-type(1) a")).getText();
        driver.findElement(сategory1).click();
        var actualtittle1 = driver.findElement(By.cssSelector("h1.entry-title")).getText();
        var breadcrumpTittle1 = driver.findElement(By.cssSelector("div.woocommerce-breadcrumb span")).getText();
        Assert.assertEquals("Наименование заголовка h1 не соответствует выбранной категории товаров при переходе в раздел каталога",
                categoryName1.toUpperCase(), actualtittle1);
        Assert.assertEquals("Заголовок 'хлебных крошек' не соответствует наименованию категории при переходе в раздел каталога",
                categoryName1.toUpperCase(), breadcrumpTittle1.toUpperCase());

        var categoryName2 = driver.findElement(By.cssSelector("ul.product-categories li:nth-of-type(2) a")).getText();
        driver.findElement(сategory2).click();
        var actualtittle2 = driver.findElement(By.cssSelector("h1.entry-title")).getText();
        var breadcrumpTittle2 = driver.findElement(By.cssSelector("div.woocommerce-breadcrumb span")).getText();
        Assert.assertEquals("Наименование заголовка h1 не соответствует выбранной категории товаров при переходе в раздел каталога",
                categoryName2.toUpperCase(), actualtittle2);
        Assert.assertEquals("Заголовок 'хлебных крошек' не соответствует наименованию категории при переходе в раздел каталога",
                categoryName2.toUpperCase(), breadcrumpTittle2.toUpperCase());

    }


    //проверка сортировки по возрастанию цены в разделе "Бытовая техника"
    @Test
    public void testSortOptionByPriceAppliances() {

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/appliances/");
        driver.findElement(By.name("orderby")).click();
        WebElement dropdown = driver.findElement(By.name("orderby"));
        dropdown.findElement(By.xpath("//option[. = 'По возрастанию цены']")).click();
        var firstPrice = driver.findElement(By.cssSelector("ul.products.columns-4 li:first-child bdi")).getText();
        var lastPrice = driver.findElement(By.cssSelector("ul.products.columns-4 li:last-child bdi")).getText();

        var actualFirstPrice = (Float.valueOf(firstPrice.substring(0, firstPrice.length() - 4))).floatValue();
        var actualLastPrice = (Float.valueOf(lastPrice.substring(0, lastPrice.length() - 4))).floatValue();
        boolean isPriceGrow;
        if (actualFirstPrice < actualLastPrice) {
            isPriceGrow = true;
        } else {
            isPriceGrow = false;
        }

        Assert.assertTrue("Стоимость последнего товара в разделе 'Бытовая техника' меньше,чем стоимость первого товара при сортировке товаров по возрастанию цены",
                isPriceGrow);
    }


    //проверка правильности добавления товара в корзину из каталога
    @Test
    public void testAddItemsToCartFromCatalog() {

        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");

        var item = By.cssSelector("ul.products li:nth-child(6) a.add_to_cart_button");
        var moreInfo = By.cssSelector("a.added_to_cart");

        var expectedName = driver.findElement(By.xpath("//div[@class='wc-products']//li[6]//h3")).getText();
        var expectedPrice = driver.findElement(By.xpath("//div[@class='wc-products']//li[6]//span[@class='price']")).getText();
        driver.findElement(item).click();
        driver.findElement(moreInfo).click();
        var actualName = driver.findElement(By.cssSelector("td.product-name a")).getText();
        var actualPrice = driver.findElement(By.cssSelector("td.product-price bdi")).getText();

        Assert.assertEquals("Наименование товара, добавленного в корзину из каталога, отличается от товара в корзине", expectedName, actualName);
        Assert.assertEquals("Стоимость товара, добавленного в корзину из каталога, отличается от стоимости товара в корзине", expectedPrice, actualPrice);

    }


    //открытие карточки товара из каталога
    @Test
    public void testOpenProductCard() {
        var item = By.cssSelector("li:nth-of-type(5) div.inner-img a");
        var expectedName = driver.findElement(By.xpath("//div[@class='wc-products']//li[5]//h3")).getText();
        var expectedPrice = driver.findElement(By.xpath("//div[@class='wc-products']//li[5]//span[@class='price']")).getText();

        driver.findElement(item).click();
        var actualName = driver.findElement(By.cssSelector("h1.product_title")).getText();
        var actualPrice = driver.findElement(By.cssSelector("p.price bdi")).getText();
        var buttonAddToCart = driver.findElement(By.cssSelector("button.button"));
        var descriptionBlock = driver.findElement(By.cssSelector("div#tab-description"));

        Assert.assertEquals("Наименование товара в каталоге не совпадает с наименованием в карточке товара",
                expectedName, actualName);
        Assert.assertEquals("Стоимость товара в каталоге не совпадает со стоимостью в карточке товара",
                expectedPrice, actualPrice);
        Assert.assertTrue("Не отображается кнопка добавления в корзину", buttonAddToCart.isDisplayed());
        Assert.assertTrue("Не отображается блок с описанием товара", descriptionBlock.isDisplayed());

    }

}
