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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private final static By searchFieldSelector = By.cssSelector("#searchForm input");
    private final static By timeLineTimesSelector = By.cssSelector("#timeline .hour span");
    private final static By searchButtonSelector = By.cssSelector("#searchForm .searchButton");
    private final static By currentTempSelector = By.cssSelector("#title .currently .summary.swap");
    private final static By timeLineTempsSelector = By.cssSelector("#timeline .temps span span");

    public MainPage(WebDriver driver, WebDriverWait wait){
        this.driver = driver;
        this.wait = wait;
        waitForPageToLoad();
    }

    private void waitForPageToLoad(){
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(timeLineTimesSelector));
        wait.until(ExpectedConditions.presenceOfElementLocated(searchFieldSelector));
        wait.until(ExpectedConditions.presenceOfElementLocated(searchButtonSelector));
    }

    public void enterSearchText(String searchText){
        WebElement searchField = driver.findElement(searchFieldSelector);
        searchField.clear();
        searchField.sendKeys(searchText);
    }

    public void clickSearch(){
        WebElement searchButton = driver.findElement(searchButtonSelector);
        searchButton.click();
        waitForPageToLoad();
    }

    public List<Date> getTimeLineTimes(){
        List<WebElement> times = driver.findElements(timeLineTimesSelector);
        SimpleDateFormat sdf = new SimpleDateFormat("MM dd yy ha");
        List<Date> dateTimes = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String dateString = (calendar.get(Calendar.MONTH)+1)+" "+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.YEAR);
        for(WebElement element: times){
            String time = element.getText();
            Date date;
            if(time.equals("Now")) {
                date = new Date();
            }else {
                try {
                    if ("12am".equals(time) || "1am".equals(time)) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                        dateString = (calendar.get(Calendar.MONTH)+1)+" "+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.YEAR);
                    }
                    date = sdf.parse(dateString+ " " + time);
                } catch (ParseException e) {
                    throw new WebDriverException(e);
                }
            }
            dateTimes.add(date);
        }
        return dateTimes;
    }

    public List<Integer> getTemps(){
        List<Integer> temperatures = new ArrayList<>();
        List<WebElement> timeLineTemps = driver.findElements(timeLineTempsSelector);
        for(WebElement element:timeLineTemps){
            String temp = element.getText().replace("°","");
            temperatures.add(Integer.parseInt(temp));
        }
        return temperatures;
    }

    public Integer getCurrentTemp(){
        WebElement currentTemp = driver.findElement(currentTempSelector);
        String[] fullTemp = currentTemp.getText().split("˚");
        if(fullTemp.length != 2){
            throw new WebDriverException("Failed to parse current temperature");
        }
        return Integer.valueOf(fullTemp[0]);
    }
}
