import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

@WebService(targetNamespace = "http://pharmacie.example.com/")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public interface PharmacieService {

    @WebMethod
    String ajouterMedicament(Medicament m);

    @WebMethod
    Medicament getMedicamentParReference(String referenceMedicament);

    @WebMethod
    List<Medicament> listeMedicaments();

    @WebMethod
    String supprimerMedicamentParReference(String referenceMedicament);
}
