import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.SystemColor.text;

public class ebayRelatedProductVerification {

    WebDriver driver;

    @BeforeMethod
    public void openLinkTest(){
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void verifyMainProductPageLoadingSuccessfully(){
        driver.get("https://www.ebay.com/");
        WebElement searchText = driver.findElement(By.xpath("//input[@type='text']"));
        searchText.sendKeys("Wallet" + Keys.ENTER);

        WebElement mainProductTile = driver.findElement(By.xpath("//img[@alt='Mens Wallet, RFID Blocking Card Holder, Slim Leather Wallet with ID Window']"));
        mainProductTile.click();

        Set<String> windows =driver.getWindowHandles();
        for (String newWindow: windows){
            System.out.println(newWindow);
            driver.switchTo().window(newWindow);
        }
        Assert.assertEquals(driver.findElement(By.xpath("//h1[@class='x-item-title__mainTitle']")).getText(),"Mens Wallet, RFID Blocking Card Holder, Slim Leather Wallet with ID Window");

    }

    @Test
    public void verifyRelatedProductFunctionality() throws InterruptedException {
        driver.get("https://www.ebay.com/itm/125682967979?_skw=Wallet&itmmeta=01JH0MGDK89N82EY5V9QTFMPCX&hash=item1d4349e5ab:g:ilEAAOSwTORi-3JD&itmprp=enc%3AAQAJAAAA4HoV3kP08IDx%2BKZ9MfhVJKlOF32zKFs33pNn22dXQXbDEyN3i4ruqLPT0ggOr0GvKVTip%2BYGPD8urTV%2FchQiO8ZEBzfDe3%2Bwj8Vd0mHUiFsUe3HLLc%2FsD%2F3k2ug3xFkzvnMo%2FzUHr8vqJk9%2FMJXwIp1F31i1DYsaeSYmLd73cm0lgrzzGAABwZG5MplY9Tb66RQ3GG6%2FI5PZTOO%2B6Dq3UaH%2F7jeO1d4M%2FYIrhJbH1o8VPDXnCREcPAGCSdzLRom5gQeZ1HxWO%2FTR%2BYVAkNmsdR8ca4VDAfvNN1L5P%2Fa58jRa%7Ctkp%3ABFBM2tnBlIhl");

        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("javascript:window.scrollBy(250,570)");
        WebElement similarItemText= driver.findElement(By.xpath("//h2[contains(text(), 'Similar')]"));
        String text1 = similarItemText.getText();
        System.out.println(text1);

        List<WebElement> relatedProducts =driver.findElements(By.xpath("//div[@class='Ihl- lA6f']//div[@class='_3hIg f17z']"));
        Assert.assertTrue(relatedProducts.size() <= 6, "Test failed: More than 6 related products found!");
        int productCount = relatedProducts.size();
        System.out.println("Product Count: " +productCount);

        for (WebElement productElement: relatedProducts){
           String relatedProductText = productElement.getText();
           System.out.println("Related product Details are: " +relatedProductText);
        }
        //h2[normalize-space()='Similar items']

    }

    @Test
    public void verifyPriceRangeOfMainProductsAndRelatedProducts() throws InterruptedException{
        driver.get("https://www.ebay.com/itm/125682967979?_skw=Wallet&itmmeta=01JH0MGDK89N82EY5V9QTFMPCX&hash=item1d4349e5ab:g:ilEAAOSwTORi-3JD&itmprp=enc%3AAQAJAAAA4HoV3kP08IDx%2BKZ9MfhVJKlOF32zKFs33pNn22dXQXbDEyN3i4ruqLPT0ggOr0GvKVTip%2BYGPD8urTV%2FchQiO8ZEBzfDe3%2Bwj8Vd0mHUiFsUe3HLLc%2FsD%2F3k2ug3xFkzvnMo%2FzUHr8vqJk9%2FMJXwIp1F31i1DYsaeSYmLd73cm0lgrzzGAABwZG5MplY9Tb66RQ3GG6%2FI5PZTOO%2B6Dq3UaH%2F7jeO1d4M%2FYIrhJbH1o8VPDXnCREcPAGCSdzLRom5gQeZ1HxWO%2FTR%2BYVAkNmsdR8ca4VDAfvNN1L5P%2Fa58jRa%7Ctkp%3ABFBM2tnBlIhl");
        WebElement mainProductPriceTextField = driver.findElement(By.cssSelector("span[class='x-price-approx__price'] span[class='ux-textspans ux-textspans--SECONDARY ux-textspans--BOLD']"));
        String mainProductPriceText =mainProductPriceTextField.getText();
        double mainPrice = convertPriceToDouble(mainProductPriceText);
        System.out.println("Main Product price: " +mainPrice);

        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("javascript:window.scrollBy(250,570)");
        WebElement similarItemText= driver.findElement(By.xpath("//h2[contains(text(), 'Similar')]"));
        String text1 = similarItemText.getText();
        System.out.println(text1);

        List<WebElement> relatedProducts =driver.findElements(By.xpath("//div[@class='Ihl- lA6f']//div[@class='_3hIg f17z']"));
        int index = 0;
        for (WebElement productElement: relatedProducts){
            index++;
            String relatedProductPriceText = productElement.findElement(By.xpath("(//div[@class='g2jY'])[" + index + "]")).getText();
            double relatedProductPrice = convertPriceToDouble(relatedProductPriceText);

            // Check if the item price falls within ±1 USD of the main item's price
            Assert.assertTrue(Math.abs(mainPrice - relatedProductPrice) <= 1.0,"Price is not with in the range of main product");
            if (Math.abs(mainPrice - relatedProductPrice) <= 1.0) {
                System.out.println("Item Price: " + relatedProductPrice + " is within ±1 USD of the main product price.");
            } else {
                System.out.println("Item Price: " + relatedProductPrice + " is NOT within ±1 USD of the main product price.");
            }
        }
        }
    private double convertPriceToDouble(String priceText) {
        priceText = priceText.replaceAll("[^0-9.]", ""); // Remove non-numeric characters
        return Double.parseDouble(priceText);
    }

    @AfterMethod
    public void closeBrowser(){
        driver.quit();
    }
}
