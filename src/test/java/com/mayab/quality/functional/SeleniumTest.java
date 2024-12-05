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
        options.addArguments("--disable-gpu"); // Deshabilitar GPU
        options.addArguments("--no-sandbox"); // Recomendado para contenedores
        options.addArguments("--disable-dev-shm-usage"); // Evitar problemas de memoria compartida
        options.addArguments("--window-size=1920,1080"); // Definir tamaño de ventana
        driver = new ChromeDriver(options);
        baseUrl = "https://www.facebook.com/";
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
        js = (JavascriptExecutor) driver;
    }

    @Test
    public void testUntitledTestCase() throws Exception {
        driver.get(baseUrl);
        pause(5000);

        // Verificar si el campo de email está presente antes de interactuar
        WebElement emailField = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        emailField.clear();
        emailField.sendKeys("fakemail@gmail.com");

        WebElement passwordField = driver.findElement(By.id("pass"));
        passwordField.clear();
        passwordField.sendKeys("password");

        driver.findElement(By.name("login")).click();

        // Esperar y validar el mensaje de error
        WebElement errorElement = new WebDriverWait(driver, Duration.ofSeconds(15))
            .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class, '_9ay7')]")));

        String actualResult = errorElement.getText();
        System.out.println("Mensaje de error encontrado: " + actualResult);

        Assert.assertEquals("The password you’ve entered is incorrect.\nForgot Password?", actualResult);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            Assert.fail(verificationErrorString);
        }
    }

    private void pause(long mils) {
        try {
            Thread.sleep(mils);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}