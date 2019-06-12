import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.File;

public class mainSecond {

    public static void main(String[] args){
        try{
            Tomcat tomcat = new Tomcat();

            String webappDirectory = new File("src/main/webapp").getAbsolutePath();

            tomcat.setPort(8081);
            Context context = tomcat.addWebapp("", webappDirectory);

            Tomcat.addServlet(context, "blockchain", new ServletContainer(new Application()));
            context.addServletMappingDecoded("/blockchain/api/*", "blockchain");

            tomcat.start();
            tomcat.getServer().await();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
