package darksky.page;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private final static By searchFieldSelector = By.cssSelector("#searchForm input");
    private final static By timeLineTimesSelector = By.cssSelector("#timeline .hour span");
    private final static By searchButtonSelector = By.cssSelector("#searchForm .searchButton");
    private final static By mapIframeSelector = By.id("embedded-map");
    private final static By currentTempSelector = By.cssSelector("#title .currently .summary.swap");
    private final static By timeLineTempSelector = By.cssSelector(".temps span span");

    public MainPage(WebDriver driver, WebDriverWait wait){
        this.driver = driver;
        this.wait = wait;
        waitForPageToLoad();
    }

    private void waitForPageToLoad(){
        wait.until(ExpectedConditions.presenceOfElementLocated(mapIframeSelector));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(timeLineTimesSelector));
        wait.until(ExpectedConditions.presenceOfElementLocated(searchFieldSelector));
    }

    public void enterSearchText(String searchText){
        WebElement searchField = wait.until(ExpectedConditions.presenceOfElementLocated(searchFieldSelector));
        searchField.clear();
        searchField.sendKeys(searchText);
    }

    public void clickSearch(){
        WebElement searchButton = wait.until(ExpectedConditions.presenceOfElementLocated(searchButtonSelector));
        searchButton.click();
        waitForPageToLoad();
    }

    public List<Date> getTimeLineTimes(){
        List<WebElement> times = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(timeLineTimesSelector));
        SimpleDateFormat sdf = new SimpleDateFormat("MM dd ha");
        List<Date> dateTimes = new ArrayList<>();
        String date = "01 01 ";
        for(WebElement element: times){
            String time = element.getText();
            if(!time.isEmpty() && !time.equals("Now")) {
                try {
                    if("12am".equals(time) || "1am".equals(time)){
                        date = "01 02 ";
                    }
                    dateTimes.add(sdf.parse(date+time));
                } catch (ParseException e) {
                    throw new WebDriverException(e);
                }
            }
        }
        return dateTimes;
    }

    public List<Integer> getTemps(){
        List<Integer> temperatures = new ArrayList<>();
        List<WebElement> timeLineTemps = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(timeLineTempSelector));
        for(WebElement element:timeLineTemps){
            String temp = element.getText().replace("°","");
            temperatures.add(Integer.parseInt(temp));
        }
        return temperatures;
    }

    public Integer getCurrentTemp(){
        WebElement currentTemp = wait.until(ExpectedConditions.presenceOfElementLocated(currentTempSelector));
        String[] fullTemp = currentTemp.getText().split("˚");
        if(fullTemp.length != 2){
            throw new WebDriverException("Failed to parse current temperature");
        }
        return Integer.valueOf(fullTemp[0]);
    }

    public String getSearchText(){
        return driver.findElement(searchFieldSelector).getText();
    }
}
