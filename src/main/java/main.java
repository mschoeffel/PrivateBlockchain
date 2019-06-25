import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.File;

/**
 * Represents the starting point of the blockchain with a web interface
 */
public class main {

    public static void main(String[] args) {
        try {

            //Creates new Tomcat webserver:
            Tomcat tomcat = new Tomcat();

            //Locates the webapp directory:
            String webappDirectory = new File("src/main/webapp").getAbsolutePath();

            //Sets the port and the webapp directory of the Apache webserver
            tomcat.setPort(8080);
            Context context = tomcat.addWebapp("", webappDirectory);

            //Adds the Servlet application and url mapping to the servlet to Apache
            Tomcat.addServlet(context, "blockchain", new ServletContainer(new Application()));
            context.addServletMappingDecoded("/blockchain/api/*", "blockchain");

            //Starts the Apache webserver
            tomcat.start();
            tomcat.getServer().await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
