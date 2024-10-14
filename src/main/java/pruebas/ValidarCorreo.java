package pruebas;

import java.util.Properties;
import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.Calendar;
import java.util.Date;

public class ValidarCorreo {
    private String email;
    private String password;
    private String asunto;

    // Constructor
    public ValidarCorreo(String email, String password, String asunto) {
        this.email = email;
        this.password = password;
        this.asunto = asunto;
    }

    public String validarCorreo() {
        Properties properties = new Properties();
        properties.put("mail.imap.host", "imap.gmail.com");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        try {
            System.out.println("Iniciando conexión al servidor de correo...");

            // Conectarse al servidor de correo IMAP
            Store store = session.getStore("imap");
            store.connect();

            System.out.println("Conexión exitosa al correo.");

            // Abrir la bandeja de entrada (INBOX)
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Calcular la fecha de hace 10 minutos
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -10);
            Date hace10Minutos = cal.getTime();

            // Obtener los últimos 50 correos no leídos (ajustar este valor según lo que necesites)
            int totalMensajes = inbox.getMessageCount();  // Número total de correos en el inbox
            int limiteCorreos = 50;  // Ajusta el límite para no procesar demasiados correos
            int inicioBusqueda = Math.max(totalMensajes - limiteCorreos, 1);  // Evitar que el índice sea negativo

            // Obtener solo los últimos correos para buscar entre ellos
            Message[] mensajesRecientes = inbox.getMessages(inicioBusqueda, totalMensajes);

            // Filtro para buscar solo correos no leídos
            FlagTerm filtroNoLeidos = new FlagTerm(new Flags(Flags.Flag.SEEN), false);

            // Filtrar solo los correos no leídos de los últimos mensajes
            Message[] mensajesNoLeidos = inbox.search(filtroNoLeidos, mensajesRecientes);

            //System.out.println("Correos no leídos recientes obtenidos: " + mensajesNoLeidos.length);

            // Iterar sobre los mensajes no leídos recientes y verificar fecha y asunto
            for (Message mensaje : mensajesNoLeidos) {
                //System.out.println(mensaje.getSubject() + " " + mensaje.getSentDate().toString());
                try {
                    // Verificar si el asunto del mensaje contiene el texto proporcionado
                    if (mensaje.getSubject().toLowerCase().contains(asunto.toLowerCase())) {
                        //System.out.println("Encontrado!");
                        String remitente = mensaje.getFrom()[0].toString();
                        String asuntoCorreo = mensaje.getSubject();
                        String fechaEnvioTexto = mensaje.getSentDate().toString();

                        System.out.println("Correo encontrado con el asunto: " + asuntoCorreo);
                        return "¡Encontrado! \nAsunto: " + asuntoCorreo + "\nRemitente: " + remitente + "\nFecha: " + fechaEnvioTexto;
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            // Si no se encontró ningún mensaje que cumpla los criterios
            //System.out.println("Correo no encontrado.");
            return "Correo no encontrado";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al verificar el correo: " + e.getMessage();
        }
    }
    /*public static void main (String[] args){
        pruebas.ValidarCorreo val = new pruebas.ValidarCorreo("javalos18j2@gmail.com", "lwkf unss udkh kcam", "reset");
        System.out.println(val.validarCorreo());
    }*/
}



