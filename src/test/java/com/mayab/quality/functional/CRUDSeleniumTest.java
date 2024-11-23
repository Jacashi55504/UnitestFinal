package com.mayab.quality.functional;

import org.junit.*;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //Seguir el orden de funciones
public class CRUDSeleniumTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        baseUrl = "https://mern-crud-mpfr.onrender.com/";
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60)); 
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
    }

    @Test
    public void testACreateNewRecord() throws InterruptedException {
        navigateToBaseUrl();
        
        waitAndClick(By.xpath("//div[@id='root']/div/div[2]/button")); // Add user
        
        Thread.sleep(1000);

        driver.findElement(By.name("name")).sendKeys("Javi");
        driver.findElement(By.name("email")).sendKeys("javi@hotmail.com");
        driver.findElement(By.name("age")).sendKeys("21");
        
        Thread.sleep(1000);
        
        // No encontré otra manera para el boton añadir
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Woah!'])[1]/following::button[1]")).click(); // Clic en el botón Save

        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='content']//p[contains(text(),'Successfully added!')]")));
        assertNotNull("No se mostró el mensaje de éxito después de crear el registro.", successMessage);

        boolean recordExists = verifyRecordExists("Javi", "javi@hotmail.com", "21");
        assertTrue("El registro no fue creado correctamente en la tabla.", recordExists);
    }

    @Test
    public void testBDuplicateEmailError() throws InterruptedException {
        navigateToBaseUrl();

        waitAndClick(By.xpath("//div[@id='root']/div/div[2]/button"));
        
        Thread.sleep(1000);
        
        driver.findElement(By.name("name")).sendKeys("Duplicate");
        driver.findElement(By.name("email")).sendKeys("javi@hotmail.com"); 
        driver.findElement(By.name("age")).sendKeys("25");
        
        Thread.sleep(1000);
        
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Woah!'])[1]/following::button[1]")).click(); 

        // Validar el mensaje de error que arroja la página
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='content']//p[contains(text(),'That email is already taken.')]")));
        assertNotNull("No se mostró el mensaje de error para correo duplicado.", errorMessage);

        // Para debug
        String actualErrorMessage = errorMessage.getText();
        assertEquals("El mensaje de error no es el esperado.",
                "That email is already taken.", actualErrorMessage);
    }

    @Test
    public void testCEditRecord() throws InterruptedException {
        navigateToBaseUrl();

        WebElement editButton = findEditButton("Javi");
        wait.until(ExpectedConditions.elementToBeClickable(editButton)).click();
        
        Thread.sleep(1000); // Necesario para el funcionamiento de edit
        
        driver.findElement(By.name("age")).click();
        driver.findElement(By.name("age")).clear();
        driver.findElement(By.name("age")).sendKeys("22");
        
        Thread.sleep(1000);
        
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Woah!'])[1]/following::button[1]")).click();

        // Validar el mensaje
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='content']//p[contains(text(),'Successfully updated!')]")));
        assertNotNull("No se mostró el mensaje de éxito después de actualizar el registro.", successMessage);

        String actualSuccessMessage = successMessage.getText();
        assertEquals("El mensaje de éxito no es el esperado.",
                "Successfully updated!", actualSuccessMessage);

        // Encontrar el usuario con la nueva edad
        boolean recordUpdated = verifyRecordExists("Javi", "javi@hotmail.com", "22");
        assertTrue("El registro no fue actualizado correctamente en la tabla.", recordUpdated);
    }

    @Test
    public void testDeleteRecord() throws InterruptedException {
        navigateToBaseUrl();

        WebElement deleteButton = findDeleteButton("Javi");
        wait.until(ExpectedConditions.elementToBeClickable(deleteButton)).click(); 

        WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'ui red button') and text()='Yes']")));
        yesButton.click();

        Thread.sleep(1000); 
        
        boolean recordExists = verifyRecordExists("Javi", "javi@hotmail.com", "21");
        assertFalse("El registro no fue eliminado correctamente.", recordExists);
    }
    
    @Test
    public void testSearchRecordByName() {
        navigateToBaseUrl();

        // Obtener todas las filas de la tabla
        List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr"));

        boolean recordFound = false;
        for (WebElement row : rows) {
            if (row.getText().contains("Javi")) {
                recordFound = true;
                break;
            }
        }
        assertTrue("No se encontró el registro con el nombre 'Javi'.", recordFound);
    }

    @Test
    public void testSearchAllRecords() {
        navigateToBaseUrl();

        List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr"));
        assertTrue("No se encontraron registros en la tabla.", rows.size() > 0);

        // Print de las filas
        for (WebElement row : rows) {
            System.out.println("Registro: " + row.getText());
        }
    }

    @After
    public void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }

    private void navigateToBaseUrl() {
        driver.get(baseUrl);
        waitForPageLoad(); // Esperar a que la página termine de cargar para evitar error
    }

    private void waitForPageLoad() {
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }

    // Esperar a entrar a la página
    private void waitAndClick(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    // Comparar elementos de la tabla del CRUD
    private boolean verifyRecordExists(String name, String email, String age) {
        List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr"));
        for (WebElement row : rows) {
            String rowText = row.getText();
            if (rowText.contains(name) && rowText.contains(email) && rowText.contains(age)) {
                return true;
            }
        }
        return false;
    }

    // Web Scrapping
    private WebElement findEditButton(String name) {
        return driver.findElement(By.xpath("//tbody/tr[td[text()='" + name + "']]//button[text()='Edit']"));
    }

    private WebElement findDeleteButton(String name) {
        return driver.findElement(By.xpath("//tbody/tr[td[text()='" + name + "']]//button[text()='Delete']"));
    }
}
