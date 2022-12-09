import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class MainPageTests {
    public static WebDriver driver;
    Actions builder = new Actions(driver);
    public static WebDriverWait wait;

    @BeforeAll
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @BeforeEach
    public void beforeEach() {
        driver.navigate().to("http://intershop5.skillbox.ru/");
    }

    @AfterAll
    public static void tearDown() throws IOException {
        var sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(sourceFile, new File("screenshots/scrnsht.png"));
        driver.quit();
    }


    //Поиск имеющегося в магазине товара
    @Test
    public void testSuccessSearchButton() {
        var search = By.cssSelector("input.search-field");
        var searchResultHeader = By.cssSelector("h1.entry-title");
        var searchKeys = "ХОЛОДИЛЬНИК";
        var buttonSearch = By.cssSelector("button.searchsubmit");
        var searchResultBreadcrumb = By.cssSelector("div.woocommerce-breadcrumb span");


        driver.findElement(search).sendKeys(searchKeys);
        driver.findElement(buttonSearch).click();
        var searchResultItems = driver.findElement(By.cssSelector("ul.products h3")).getText().toUpperCase();

        Assert.assertTrue("Заголовок поиска не соответствует поисковому запросу",
                driver.findElement(searchResultHeader).getText().contains(searchKeys));
        Assert.assertTrue("Заголовок 'хлебных крошек' не соответствует поисковому запросу",
                driver.findElement(searchResultBreadcrumb).getText().contains(searchKeys));
        Assert.assertTrue("Найденные товары не соответствуют поисковому запросу",
                searchResultItems.contains(searchKeys));
    }


    //поиск отсутствующего в магазине товара
    @Test
    public void testWrongSearchEnter() {
        var search = By.cssSelector("input.search-field");
        var searchResultHeader = By.cssSelector("h1.entry-title");
        var searchKeys = "RJVGJ";
        var searchResultBreadcrumb = By.cssSelector("div.woocommerce-breadcrumb span");
        var expextedResults = "По вашему запросу товары не найдены.";

        driver.findElement(search).sendKeys(searchKeys);
        driver.findElement(search).sendKeys(Keys.ENTER);
        var actualSearchResults = driver.findElement(By.cssSelector("p.woocommerce-info")).getText();

        Assert.assertTrue("Заголовок поиска не соответствует поисковому запросу",
                driver.findElement(searchResultHeader).getText().contains(searchKeys));
        Assert.assertTrue("Заголовок 'хлебных крошек' не соответствует поисковому запросу",
                driver.findElement(searchResultBreadcrumb).getText().contains(searchKeys));
        Assert.assertEquals("Неверное сообщение об отсутствии товаров, соответствующих поиску",
                expextedResults, actualSearchResults);

    }


    //Проверка добавления товара на главной странице в корзину и наличия кнопки "Подробнее"
    @Test
    public void testAddItemsToCartFromMain() {
        var item = By.cssSelector("aside#accesspress_store_product-3 div.slick-track>li:nth-child(6) a.add_to_cart_button");
        var moreInfo = By.cssSelector("a.added_to_cart");
        var itemName = By.xpath("//aside[@id='accesspress_store_product-3']//div[@class='slick-track']//li[6]//h3");

        builder.moveToElement(driver.findElement(itemName)).build().perform();
        wait.until(ExpectedConditions.presenceOfElementLocated(itemName));
        var expectedAddedName = driver.findElement(itemName).getAttribute("innerHTML").toUpperCase();
        wait.until(ExpectedConditions.presenceOfElementLocated(item));
        builder.moveToElement(driver.findElement(item)).build().perform();
        wait.until(ExpectedConditions.presenceOfElementLocated(item));
        driver.findElement(item).click();
        builder.moveToElement(driver.findElement(moreInfo)).build().perform();
        var addedElement = driver.findElement(moreInfo).getText();
        driver.navigate().to("http://intershop5.skillbox.ru/cart/");
        var actualAddedName = driver.findElement(By.cssSelector(("td.product-name a"))).getText().toUpperCase();

        Assert.assertTrue("Не появляется кнопка подробнее у добавленного в корзину товара",
                addedElement.contains("ПОДРОБНЕЕ"));
        Assert.assertEquals("Добавлен неверный элемент", expectedAddedName, actualAddedName);
    }


    //Проверка отображения в списке последних просмотров последнего просмотренного товара
    @Test
    public void testLastSeenItemList() {
        var firstItem = By.cssSelector("aside#accesspress_store_product-3 div.slick-track>li:nth-child(5) img");
        var secondItem = By.cssSelector("section.related.products li.product:nth-child(2) a.button.product_type_simple");
        var secondItemName = By.cssSelector("section.related.products li.product:nth-child(2) h3");
        var linkToMain = By.cssSelector("a.site-text");
        var lastItemsBlock = By.cssSelector("section.ap-cat-list.clear");
        var lastSeenItem = By.cssSelector("ul.product_list_widget li:first-child span.product-title");

        builder.moveToElement(driver.findElement(firstItem)).build().perform();
        driver.findElement(firstItem).click();
        var expectedLastSeenName = driver.findElement(secondItemName).getText();
        builder.moveToElement(driver.findElement(secondItem)).build().perform();
        driver.findElement(secondItem).click();
        driver.findElement(linkToMain).click();
        builder.moveToElement(driver.findElement(lastItemsBlock)).build().perform();
        var actualLastSeenItem = driver.findElement(lastSeenItem).getText();

        Assert.assertEquals("Последний просмотренный товар не соответствует последнему товару в списке 'Просмотренные товары'",
                expectedLastSeenName, actualLastSeenItem);
    }


    //Проверка перехода на страницу категории по промо-ссылке
    @Test
    public void testPromoLinksFirstCategory() {

        var firstPromo = By.xpath("//div[@class='promo-wrap1']//div//aside[1]");
        var firstPromoTitle = By.cssSelector("aside#accesspress_storemo-2 h4");
        var firstPromoFadeinElement = By.cssSelector("aside#accesspress_storemo-2 div.caption.wow.fadeIn");
        var searchResultBreadcrumb = By.cssSelector("div.woocommerce-breadcrumb span");

        wait.until(ExpectedConditions.presenceOfElementLocated(firstPromoFadeinElement));
        var firstPromoName = driver.findElement(firstPromoTitle).getAttribute("innerHTML");
        driver.findElement(firstPromo).click();
        var firstTitle = driver.findElement(By.cssSelector("h1.entry-title")).getText();

        boolean isItemBlockPresents;
        var categoryItemsBlock = By.cssSelector("div.wc-products");
        List<WebElement> categoryList = driver.findElements(categoryItemsBlock);
        if (categoryList.size() != 0) {
            isItemBlockPresents = true;
        } else {
            isItemBlockPresents = false;
        }

        Assert.assertEquals("Наименование открывшейся категории не соответствует наименованию промо категории",
                firstPromoName.toUpperCase(), firstTitle);
        Assert.assertTrue("Заголовок 'хлебных крошек' не соответствует наименованию категории",
                driver.findElement(searchResultBreadcrumb).getText().contains(firstPromoName));
        Assert.assertTrue("Отсутствует блок с товарами первой промо-категории при переходе по ссылке",
                isItemBlockPresents);
    }


    //проверка наличия лейбла "Скидка!" в категории "Распродажа"
    @Test
    public void testSaleLabel() {

        var saleLabel = By.cssSelector("div.slick-track span.onsale");

        wait.until(ExpectedConditions.presenceOfElementLocated(saleLabel));
        var saleLabelText = driver.findElement(saleLabel).getAttribute("innerHTML");

        Assert.assertEquals("Неверно обозначен текст на лейбле со скидкой", "Скидка!", saleLabelText);
    }


    //проверка наличия лейбла "Новый!" в категории "Новинки"
    @Test
    public void testNewLabel() {

        var newLabel = By.cssSelector("span.label-new");

        wait.until(ExpectedConditions.presenceOfElementLocated(newLabel));
        var newLabelText = driver.findElement(newLabel).getAttribute("innerHTML");

        Assert.assertEquals("Неверно обозначен текст на лейбле со скидкой", "Новый!", newLabelText);
    }
}
