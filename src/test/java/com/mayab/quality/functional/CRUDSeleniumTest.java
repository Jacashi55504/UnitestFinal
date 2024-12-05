package com.mayab.quality.functional;

import org.junit.*;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //Seguir el orden de funciones
public class CRUDSeleniumTest {
	private static WebDriver driver;
	  private boolean verifyRecordExists(String string, String string2, String string3) {
			// TODO Auto-generated method stub
			return false;
		}
	  private String baseUrl;
	  private boolean acceptNextAlert = true;
	  private StringBuffer verificationErrors = new StringBuffer();
	  JavascriptExecutor js;
	  
	  @Before
	  public void setUp() throws Exception {
		  WebDriverManager.chromedriver().setup();
	        
	        // Set Chrome options for headless mode
	        ChromeOptions options = new ChromeOptions();
	        options.addArguments("--headless");
	        options.addArguments("--disable-gpu"); // Optional, helps with headless rendering
	        options.addArguments("--window-size=1920x1080"); // Optional, sets a screen resolution

	        // Initialize the driver with the headless options
	        driver = new ChromeDriver(options);
	        baseUrl = "https://mern-crud-mpfr.onrender.com/";
	        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
	        js = (JavascriptExecutor) driver;
	  }
	  @Test
		  public void ATest() throws Exception {
			  driver.get("https://mern-crud-mpfr.onrender.com/");
			  pause(5000);
			    driver.findElement(By.xpath("//div[@id='root']/div/div[2]/button")).click();
			    driver.findElement(By.name("name")).click();
			    driver.findElement(By.name("name")).clear();
			    driver.findElement(By.name("name")).sendKeys("CERVERA GONZALEZZ");
			    driver.findElement(By.name("email")).click();
			    driver.findElement(By.name("email")).clear();
			    driver.findElement(By.name("email")).sendKeys("INDUSTRIASCERVERAGONZALEZ@GMAIL.COM");
			    driver.findElement(By.name("age")).click();
			    driver.findElement(By.name("age")).clear();
			    driver.findElement(By.name("age")).sendKeys("21");
			    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Gender'])[2]/following::div[2]")).click();
			    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Male'])[1]/following::div[2]")).click();
			    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Woah!'])[1]/following::button[1]")).click();
			    pause(5000);
		      String actualResult = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[4]/div/p")).getText();
		      assertThat(actualResult, is("Successfully added!"));
		  }
	  
		  @Test
	  public void BTest() throws Exception {
		  driver.get("https://mern-crud-mpfr.onrender.com/");
		  pause(5000);
		    driver.findElement(By.xpath("//div[@id='root']/div/div[2]/button")).click();
		    driver.findElement(By.name("name")).click();
		    driver.findElement(By.name("name")).clear();
		    driver.findElement(By.name("name")).sendKeys("CERVERA GONZALEZZ");
		    driver.findElement(By.name("email")).click();
		    driver.findElement(By.name("email")).clear();
		    driver.findElement(By.name("email")).sendKeys("INDUSTRIASCERVERAGONZALEZ@GMAIL.COM");
		    driver.findElement(By.name("age")).click();
		    driver.findElement(By.name("age")).clear();
		    driver.findElement(By.name("age")).sendKeys("21");
		    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Gender'])[2]/following::div[2]")).click();
		    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Male'])[1]/following::div[2]")).click();
		    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Woah!'])[1]/following::button[1]")).click();
		    pause(5000);
	      String actualResult = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[5]/div/p")).getText();
	      assertThat(actualResult, is("That email is already taken."));
	  }
	  
	  @Test
	  public void CTest() throws Exception {
		  driver.get("https://mern-crud-mpfr.onrender.com/");
		  pause(5000);
		  driver.findElement(By.xpath("//div[@id='root']/div/div[2]/table/tbody/tr/td[5]/button")).click();
		    driver.findElement(By.name("name")).click();
		    driver.findElement(By.name("name")).clear();
		    driver.findElement(By.name("name")).sendKeys("esteban edit");
		    driver.findElement(By.name("email")).click();
		    driver.findElement(By.name("email")).clear();
		    driver.findElement(By.name("email")).sendKeys("estebanedt@gmail.com");
		    driver.findElement(By.name("age")).click();
		    driver.findElement(By.name("age")).click();
		    driver.findElement(By.name("age")).clear();
		    driver.findElement(By.name("age")).sendKeys("19");
		    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Gender'])[2]/following::div[2]")).click();
		    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Male'])[2]/following::span[1]")).click();
		    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Woah!'])[1]/following::button[1]")).click();
		    pause(5000);
	      String actualResult = driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/form/div[4]/div/p")).getText();
	      assertThat(actualResult, is("Successfully updated!"));
	  }
	  
	  
	  @Test
	  public void DTest() throws Exception {
		  driver.get("https://mern-crud-mpfr.onrender.com/");
		  pause(5000);
		  driver.findElement(By.xpath("//div[@id='root']/div/div[2]/table/tbody/tr/td[5]/button[2]")).click();
		    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Cervera Gonzalezz'])[2]/following::button[1]")).click();

		    pause(5000);
		    boolean recordExists = verifyRecordExists("esteban edit", "estebanedt@gmail.com", "19");
	        assertFalse("El registro no se elimino.", recordExists);
	  }
	  
	  @Test
	   public void ETest() throws Exception{
		   driver.get("https://mern-crud-mpfr.onrender.com/");
			  pause(5000);
			    driver.findElement(By.xpath("//div[@id='root']/div/div[2]/button")).click();
			    driver.findElement(By.name("name")).click();
			    driver.findElement(By.name("name")).clear();
			    driver.findElement(By.name("name")).sendKeys("CERVERA GONZALEZZ");
			    driver.findElement(By.name("email")).click();
			    driver.findElement(By.name("email")).clear();
			    driver.findElement(By.name("email")).sendKeys("INDUSTRIASCERVERAGONZALEZ@GMAIL.COM");
			    driver.findElement(By.name("age")).click();
			    driver.findElement(By.name("age")).clear();
			    driver.findElement(By.name("age")).sendKeys("21");
			    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Gender'])[2]/following::div[2]")).click();
			    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Male'])[1]/following::div[2]")).click();
			    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Woah!'])[1]/following::button[1]")).click();
			    driver.findElement(By.xpath("//i")).click();
			    pause(5000);
			    
			    String actualResult = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/table/tbody/tr[1]/td[1]")).getText();
			    assertThat(actualResult,is("Cervera Gonzalezz"));
			   
	   }
	  
	  @Test
	   public void FTest() throws Exception{
		   driver.get("https://mern-crud-mpfr.onrender.com/");
			  pause(5000);
			    driver.findElement(By.xpath("//div[@id='root']/div/div[2]/button")).click();
			    driver.findElement(By.name("name")).click();
			    driver.findElement(By.name("name")).clear();
			    driver.findElement(By.name("name")).sendKeys("CERVERA GONZALEZZ");
			    driver.findElement(By.name("email")).click();
			    driver.findElement(By.name("email")).clear();
			    driver.findElement(By.name("email")).sendKeys("cer@GMAIL.COM");
			    driver.findElement(By.name("age")).click();
			    driver.findElement(By.name("age")).clear();
			    driver.findElement(By.name("age")).sendKeys("21");
			    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Gender'])[2]/following::div[2]")).click();
			    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Male'])[1]/following::div[2]")).click();
			    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Woah!'])[1]/following::button[1]")).click();
			    driver.findElement(By.xpath("//i")).click();
			    pause(5000);
			    
			    String actualResult = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/table/tbody/tr[1]/td[1]")).getText();
			    assertThat(actualResult,is("Cervera Gonzalezz"));
			    
			    driver.findElement(By.xpath("//div[@id='root']/div/div[2]/button")).click();
			    driver.findElement(By.name("name")).click();
			    driver.findElement(By.name("name")).clear();
			    driver.findElement(By.name("name")).sendKeys("esteban gon");
			    driver.findElement(By.name("email")).click();
			    driver.findElement(By.name("email")).clear();
			    driver.findElement(By.name("email")).sendKeys("esasteban@GMAIL.COM");
			    driver.findElement(By.name("age")).click();
			    driver.findElement(By.name("age")).clear();
			    driver.findElement(By.name("age")).sendKeys("21");
			    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Gender'])[2]/following::div[2]")).click();
			    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Male'])[1]/following::div[2]")).click();
			    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Woah!'])[1]/following::button[1]")).click();
			    driver.findElement(By.xpath("//i")).click();
			    pause(5000);
			    
			    String actualResult2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/table/tbody/tr[1]/td[1]")).getText();
			    assertThat(actualResult2,is("Esteban Gon"));
			   
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
