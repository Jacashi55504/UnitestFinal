package com.mayab.quality.functional;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.time.Duration;
public class SeleniumTest {
	private WebDriver driver;
	  private String baseUrl;
	  private boolean acceptNextAlert = true;
	  private StringBuffer verificationErrors = new StringBuffer();
	  JavascriptExecutor js;
	  
	  @Before
	  public void setUp() throws Exception {
		  WebDriverManager.chromedriver().setup();
		  ChromeOptions options = new ChromeOptions();
		  options.addArguments("--headless"); // Ejecutar en modo headless
		  options.addArguments("--disable-gpu"); // Deshabilitar GPU (opcional, mejora en headless)
		  options.addArguments("--no-sandbox"); // Recomendado para contenedores
		  options.addArguments("--disable-dev-shm-usage"); // Usar /tmp si falta memoria compartida
		  driver = new ChromeDriver(options);
		  baseUrl = "https://www.facebook.com/";
		  driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
		  js = (JavascriptExecutor) driver;
	  }
	  

	  @Test
	  public void testUntitledTestCase() throws Exception {
	      driver.get("https://www.facebook.com/");
	      pause(5000);
	      
	      driver.findElement(By.id("email")).clear();
	      driver.findElement(By.id("email")).sendKeys("fakemail@gmail.com");
	      
	      driver.findElement(By.id("pass")).clear();
	      driver.findElement(By.id("pass")).sendKeys("password");

	      driver.findElement(By.name("login")).click();
	      
	      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	      // Agarrar el mensaje capturado en el elemento 
	      WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class, '_9ay7')]")));

	      // Handling para error
	      String actualResult = errorElement.getText();
	      System.out.println("Mensaje de error encontrado: " + actualResult);

	      assertThat(actualResult, is("The password you’ve entered is incorrect.\nForgot Password?"));
	  }

	  @After
	  public void tearDown() throws Exception {
	    driver.quit();
	    String verificationErrorString = verificationErrors.toString();
	    if (!"".equals(verificationErrorString)) {
	      fail(verificationErrorString);
	    }
	  }
	  
	  private void pause(long mils) {
	      try {
	          Thread.sleep(mils);
	      } catch (InterruptedException e) {
	          e.printStackTrace();
	      }
	  }

	  private boolean isElementPresent(By by) {
	    try {
	      driver.findElement(by);
	      return true;
	    } catch (NoSuchElementException e) {
	      return false;
	    }
	  }

	  private boolean isAlertPresent() {
	    try {
	      driver.switchTo().alert();
	      return true;
	    } catch (NoAlertPresentException e) {
	      return false;
	    }
	  }

	  private String closeAlertAndGetItsText() {
	    try {
	      Alert alert = driver.switchTo().alert();
	      String alertText = alert.getText();
	      if (acceptNextAlert) {
	        alert.accept();
	      } else {
	        alert.dismiss();
	      }
	      return alertText;
	    } finally {
	      acceptNextAlert = true;
	    }
	  }
}