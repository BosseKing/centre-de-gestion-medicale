import javax.xml.ws.Endpoint;

public class Publish {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:9090/pharmacie", new PharmacieServiceImpl());
        System.out.println("Service SOAP Pharmacie lancé !");
        System.out.println("WSDL : http://localhost:9090/pharmacie?wsdl");
    }
}
