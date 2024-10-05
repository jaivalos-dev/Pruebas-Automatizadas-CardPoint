package pruebas;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.JavascriptExecutor;

public class pruebas {

    private String urlInicio = "https://compiladores.demo.gt/login/?redirect_to=https%3A%2F%2Fcompiladores.demo.gt%2F";
    private String urlRegistro = "https://compiladores.demo.gt/register/";
    WebDriver driver;

    // ExtentReports variables
    ExtentReports extent;
    ExtentSparkReporter spark;
    ExtentTest test;

    // Se ejecuta al iniciar la suite
    @BeforeSuite
    public void setUpReport() {
        // Inicializar el ExtentSparkReporter y configurar el reporte
        spark = new ExtentSparkReporter("reportePruebas.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    // Se realiza al inicio de cualquier test
    @BeforeMethod
    public void setBaseUrl(){
        driver = new ChromeDriver();
    }

    // Prueba 1: Login Incorrecto
    @Test(priority = 1)
    public void LoginIncorrecto() throws InterruptedException{
        test = extent.createTest("Login Incorrecto");

        driver.get(urlInicio);
        driver.manage().window().maximize();

        String username = "test";
        String password = "test";

        WebElement usernameInput = driver.findElement(By.id("username-194"));
        WebElement passwordInput = driver.findElement(By.id("user_password-194"));
        WebElement loginButton = driver.findElement(By.id("um-submit-btn"));

        //Login incorrecto
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        loginButton.click();

        // Espera de 5 segundos
        Thread.sleep(5000);

        // Validar el texto del mensaje de error
        String pageSource = driver.getPageSource();
        if (pageSource.contains("La contraseña es incorrecta. Por favor, inténtalo de nuevo.")) {
            System.out.println("El mensaje de error está presente.");
            test.log(Status.PASS, "El mensaje de error está presente.");
        } else {
            System.out.println("El mensaje de error NO está presente.");
            test.log(Status.FAIL, "El mensaje de error NO está presente.");
        }

    }

    @Test(priority = 2)
    public void RegistroUsuario() throws InterruptedException {

        test = extent.createTest("Registro de Usuario");

        driver.get(urlRegistro);
        driver.manage().window().maximize();

        String username = "userB";
        String nombre = "Javier Alejandro";
        String apellidos = "Avalos Galindo";
        String correo = "mail2@gmail.com";
        String password = "Test123.";

        WebElement usernameInput = driver.findElement(By.id("user_login-193"));
        WebElement nombreInput = driver.findElement(By.id("first_name-193"));
        WebElement apellidosInput = driver.findElement(By.id("last_name-193"));
        WebElement correoInput = driver.findElement(By.id("user_email-193"));
        WebElement passwordInput = driver.findElement(By.id("user_password-193"));
        WebElement passwordInput2 = driver.findElement(By.id("confirm_user_password-193"));
        WebElement loginButton = driver.findElement(By.id("um-submit-btn"));

        // Usar sendKeys() para rellenar cada campo
        usernameInput.sendKeys(username.toLowerCase());
        nombreInput.sendKeys(nombre);
        apellidosInput.sendKeys(apellidos);
        correoInput.sendKeys(correo);
        passwordInput.sendKeys(password);
        passwordInput2.sendKeys(password);

        // Hacer clic en el botón de enviar
        loginButton.click();

        // Esperar unos segundos hasta que la página redirija
        Thread.sleep(5000);

        // Validar la URL actual con la URL esperada
        String expectedUrl = "https://compiladores.demo.gt/user/" + username.toLowerCase() + "/";
        String actualUrl = driver.getCurrentUrl();

        if (actualUrl.equals(expectedUrl)) {
            System.out.println("La redirección fue exitosa. URL actual: " + actualUrl);
            test.log(Status.PASS, "La redirección fue exitosa. URL actual: " + actualUrl);
        } else {
            System.out.println("La redirección falló. URL actual: " + actualUrl);
            test.log(Status.FAIL, "La redirección falló. URL actual: " + actualUrl);
        }
    }

    @Test(priority = 3)
    public void LoginCorrecto() throws InterruptedException{

        test = extent.createTest("Login Correcto");

        driver.get(urlInicio);
        driver.manage().window().maximize();

        String username = "Jaivalos";
        String password = "Test123.";

        WebElement usernameInput = driver.findElement(By.id("username-194"));
        WebElement passwordInput = driver.findElement(By.id("user_password-194"));
        WebElement loginButton = driver.findElement(By.id("um-submit-btn"));

        //Login incorrecto
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        loginButton.click();

        // Espera de 5 segundos
        Thread.sleep(5000);

        // Validar el texto del mensaje de error
        String pageSource = driver.getPageSource();
        if (pageSource.contains("La contraseña es incorrecta. Por favor, inténtalo de nuevo.")) {
            System.out.println("El mensaje de error está presente.");
            test.log(Status.FAIL, "El mensaje de error está presente. El login falló.");
        } else {
            System.out.println("Login correcto con el usuario: " + username);
            test.log(Status.PASS, "Login correcto");
        }

    }

    @Test(priority = 4)
    public void OlvidePass() throws InterruptedException {

        try {
            test = extent.createTest("Olvidé Contraseña");

            driver.get(urlInicio);
            driver.manage().window().maximize();

            // Ajustar zoom de la página
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript("document.body.style.zoom='100%'");

            // Buscar el anchor y desplazarme hacia él
            WebElement anchor = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/article/div/div/div/div/div/div[2]/div[2]/div/div/div/form/div[3]/a"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", anchor);

            // Presionar el anchor
            anchor.click();

            // Espera de 5 segundos
            Thread.sleep(5000);

            // Validar la URL actual con la URL esperada
            String expectedUrl = "https://compiladores.demo.gt/password-reset/";
            String actualUrl = driver.getCurrentUrl();

            if (actualUrl.equals(expectedUrl)) {
                System.out.println("La redirección fue exitosa. URL actual: " + actualUrl);
                test.log(Status.PASS, "La redirección fue exitosa. URL actual: " + actualUrl);

            } else {
                System.out.println("La redirección falló. URL actual: " + actualUrl);
                test.log(Status.FAIL, "La redirección falló. URL actual: " + actualUrl);
            }

        } catch (Exception e) {
            // Registrar la excepción
            System.out.println("Se produjo un error: " + e.getMessage());
            test.log(Status.FAIL, "Error al momento de ejecutar la prueba.");
            test.log(Status.WARNING, "Se produjo un error: " + e.getMessage());
        }

    }

    // Se ejecuta después de cada prueba
    @AfterMethod
    public void tearDown() {
        //driver.quit();
    }

    // Se ejecuta al finalizar la suite de pruebas
    @AfterSuite
    public void tearDownReport() {
        extent.flush();
    }


}
