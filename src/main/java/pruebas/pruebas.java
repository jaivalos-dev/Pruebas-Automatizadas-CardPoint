package pruebas;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.JavascriptExecutor;

import javax.mail.*;
import javax.mail.search.*;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.mail.Flags.Flag;


public class pruebas {

    private String urlInicio = "https://compiladores.demo.gt/login/?redirect_to=https%3A%2F%2Fcompiladores.demo.gt%2F";
    private String urlRegistro = "https://compiladores.demo.gt/register/";
    WebDriver driver;

    // ExtentReports variables
    ExtentReports extent;
    ExtentSparkReporter spark;
    ExtentTest test;

    // usuario y mail para registro
    String username = "userp";
    String correo = "mail16@mail.com";

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

        String nombre = "Test";
        String apellidos = "Test";
        String password = "Test123.";

        // Llamada a la función de registro
        boolean registrationSuccess = registerUser(username, nombre, apellidos, correo, password);

        // Inserción del log en el reporte en base al resultado
        if (registrationSuccess) {
            System.out.println("La redirección fue exitosa.");
            test.log(Status.PASS, "La redirección fue exitosa. URL actual: " + driver.getCurrentUrl());
        } else {
            System.out.println("La redirección falló.");
            test.log(Status.FAIL, "La redirección falló. URL actual: " + driver.getCurrentUrl());
        }
    }

    @Test(priority = 3)
    public void LoginCorrecto() throws InterruptedException{

        test = extent.createTest("Login Correcto");

        String username = "Jaivalos";
        String password = "Test123.";

        // Llamada a la función login
        login(username, password);

        // Validación del login y reporte
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

        String usernameReset = "jaivalos";

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

            // Validar la URL actual con la URL esperada
            String expectedUrl = "https://compiladores.demo.gt/password-reset/";
            String actualUrl = driver.getCurrentUrl();

            if (actualUrl.equals(expectedUrl)) {
                System.out.println("La redirección fue exitosa. URL actual: " + actualUrl);
                test.log(Status.INFO, "La redirección fue exitosa. URL actual: " + actualUrl);

                driver.findElement(By.id("username_b")).sendKeys(usernameReset);
                driver.findElement(By.id("um-submit-btn")).click();

                // Esperar unos segundos para que el correo sea enviado
                Thread.sleep(10000);
                ValidarCorreo validar = new ValidarCorreo("javalos18j2@gmail.com", "lwkf unss udkh kcam", "reset your password");

                correo = validar.validarCorreo();

                //Verificar que el correo se haya enviado
                if (correo.equals("Correo no encontrado")) {
                    System.out.println("El correo no se ha encontrado");
                    test.log(Status.FAIL, "El correo no se ha encontrado");
                } else {
                    System.out.println(correo);
                    test.log(Status.PASS, "Se encontró el correo: " + correo);
                }

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

    @Test(priority = 5)
    public void chatbot() throws InterruptedException {
        test = extent.createTest("Chatbot");

        driver.get(urlInicio);
        driver.manage().window().maximize();

        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Esperar a que la animación desaparezca
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".wp-chatbot-ball-animator")
        ));

        // Ahora si precionar
        WebElement chatbot = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"wp-chatbot-ball\"]/div/img")
        ));

        chatbot.click();

        Thread.sleep(6000);

        try {
            WebElement chatbotInput2 = driver.findElement(By.id("wp-chatbot-editor"));
            chatbotInput2.sendKeys("Selenium");

            driver.findElement(By.id("wp-chatbot-send-message")).click();

            test.log(Status.PASS, "Se encuentra el chatbot y el campo para escribirle");

        } catch (Exception e){
            test.log(Status.INFO, "No se puede enviar la respuesta");
            test.log(Status.FAIL, "Error: " + e);
        }


    }

    @Test(priority = 6)
    public void cambiarFotoPerfil() throws InterruptedException {
        String username = "Jaivalos";
        String password = "Test123.";

        login(username, password);
        test = extent.createTest("Cambiar Foto de Perfil");

        if (validateCurrentUrl("https://compiladores.demo.gt/")) {
            navigateToProfilePhotoChange();
            test.log(Status.PASS, "Se muestran las opciones necesarias para cambiar foto de perfil.");
        } else {
            test.log(Status.FAIL, "Error en el proceso de cambio de foto de perfil.");
        }
    }

    @Test(priority = 7)
    public void cambiarFotoPortada() throws InterruptedException {
        String username = "Jaivalos";
        String password = "Test123.";

        login(username, password);
        test = extent.createTest("Cambiar Foto de Portada");

        if (validateCurrentUrl("https://compiladores.demo.gt/")) {
            navigateToProfilePhotoChange();  // Navegar sin generar logs duplicados

            try {
                Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(5));

                // Primero obtener el elemento y hacer clic usando JavascriptExecutor
                WebElement coverAddButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[@id=\"um_upload_single\"]/div[2]/div[6]/div[1]/a[2]")
                ));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", coverAddButton);

                WebElement portada = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"post-197\"]/div/div/div/div/div/div/div[1]/div/div/div/div[1]/div[3]/a")));
                portada.click();

                WebElement subirPortada = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"post-197\"]/div/div/div/div/div/div/div[1]/div/div/div/div[1]/div[1]/div/ul/li[1]/a")));
                subirPortada.click();

                test.log(Status.PASS, "Se muestran las opciones necesarias para cambiar foto de portada.");
            } catch (Exception e) {
                System.out.println("Error " + e);
                test.log(Status.FAIL, "Error en el proceso de cambio de foto de portada: " + e);
            }
        }
    }

    @Test(priority = 8)
    public void puntosPorRegistro()throws InterruptedException{

        test = extent.createTest("Puntos por Registro de usuario");

        try{
            login(username, "Test123.");

            navigateToSection(By.xpath("//*[@id=\"menu-menu-principal\"]/li[3]/a"), "https://compiladores.demo.gt/account/");
            navigateToSection(By.xpath("//*[@id=\"post-202\"]/div/div/div/form/div[2]/div/div[2]/a"), "https://compiladores.demo.gt/user/"+username+"/");

            WebElement puntos = driver.findElement(By.xpath("//*[@id=\"post-197\"]/div/div/div/div/div/div/div[3]/div/div"));
            String puntosText = puntos.getText();

            if (puntosText.equals("Tu punto actual 2500")) {
                test.log(Status.PASS, "Se agregan los 2,500 puntos por registrarse");
            } else {
                test.log(Status.FAIL, "No se agregan los puntos");
            }

        }catch (Exception e){
            test.log(Status.FAIL, "Fallo al realizar la prueba: " + e);
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



    public void login(String username, String password) throws InterruptedException {
        driver.get(urlInicio);
        driver.manage().window().maximize();

        WebElement usernameInput = driver.findElement(By.id("username-194"));
        WebElement passwordInput = driver.findElement(By.id("user_password-194"));
        WebElement loginButton = driver.findElement(By.id("um-submit-btn"));

        // Proceso de login
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        loginButton.click();

        // Espera de 5 segundos
        Thread.sleep(5000);
    }

    private boolean validateCurrentUrl(String expectedUrl) {
        String actualUrl = driver.getCurrentUrl();
        if (!actualUrl.equals(expectedUrl)) {
            System.out.println("La URL no es la correcta: Esperado - " + expectedUrl + ", Actual - " + actualUrl);
            test.log(Status.FAIL, "Error, la URL no es la correcta: " + actualUrl);
            return false;
        }
        return true;
    }

    private void navigateToSection(By elementLocator, String expectedUrl) throws InterruptedException {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(elementLocator));
        element.click();
        Thread.sleep(1000); // Espera adicional opcional para asegurar la carga
        validateCurrentUrl(expectedUrl);
    }

    private void navigateToProfilePhotoChange() throws InterruptedException {
        navigateToSection(By.xpath("//*[@id=\"menu-menu-principal\"]/li[3]/a"), "https://compiladores.demo.gt/account/");
        navigateToSection(By.xpath("//*[@id=\"post-202\"]/div/div/div/form/div[2]/div/div[2]/a"), "https://compiladores.demo.gt/user/jaivalos/");

        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement photo = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"post-197\"]/div/div/div/div/div/div/div[1]/div/div/div/div[2]/div[2]/a")));
        photo.click();

        WebElement subirPhoto = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"post-197\"]/div/div/div/div/div/div/div[1]/div/div/div/div[2]/div[2]/div[2]/div/ul/li[1]/a")));
        subirPhoto.click();
    }

    public boolean registerUser(@org.jetbrains.annotations.NotNull String username, String nombre, String apellidos, String correo, String password) throws InterruptedException {
        driver.get(urlRegistro);
        driver.manage().window().maximize();

        // Localizar los elementos de entrada
        WebElement usernameInput = driver.findElement(By.id("user_login-193"));
        WebElement nombreInput = driver.findElement(By.id("first_name-193"));
        WebElement apellidosInput = driver.findElement(By.id("last_name-193"));
        WebElement correoInput = driver.findElement(By.id("user_email-193"));
        WebElement passwordInput = driver.findElement(By.id("user_password-193"));
        WebElement passwordInput2 = driver.findElement(By.id("confirm_user_password-193"));
        WebElement loginButton = driver.findElement(By.id("um-submit-btn"));

        // Rellenar los campos del formulario
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

        // Validar la URL de redirección
        String expectedUrl = "https://compiladores.demo.gt/user/" + username.toLowerCase() + "/";
        String actualUrl = driver.getCurrentUrl();

        // Retorna true si la URL es correcta, false de lo contrario
        return actualUrl.equals(expectedUrl);
    }

}
