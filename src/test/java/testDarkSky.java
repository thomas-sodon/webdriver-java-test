import darksky.page.MainPage;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class testDarkSky {

    private static WebDriver driver;
    private static MainPage mainPage;

    @BeforeClass
    public static void configureTest(){
        //TODO update path to match location of downloaded driver
        System.setProperty("webdriver.chrome.driver", "/Users/thomas/sourcecode/common/chromedriver/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://darksky.net");
        mainPage = new MainPage(driver,wait);
        mainPage.enterSearchText("10001");
        mainPage.clickSearch();
    }

    @Test
    public void testTimeLineTimes(){
        List<Date> dateList = mainPage.getTimeLineTimes();
        Assert.assertTrue(!dateList.isEmpty());

        Date previousDate = dateList.get(0);
        for(int i=1;i<dateList.size();i++){
            long diffInMillies = Math.abs(dateList.get(i).getTime() - previousDate.getTime());
            long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            Assert.assertEquals(2,diff);
            previousDate = dateList.get(i);
        }
    }

    @Test
    public void testCurrentTemperature(){
        List<Integer> temps = mainPage.getTemps();
        Collections.sort(temps);
        Integer currentTemp = mainPage.getCurrentTemp();
        Assert.assertTrue(currentTemp >= temps.get(0));
        Assert.assertTrue(currentTemp <= temps.get(temps.size()-1));
    }

    @AfterClass
    public static void tearDown(){
        try {
            driver.quit();
        }catch(NullPointerException e){
            // noop
        }
    }
}
