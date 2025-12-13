import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebService(
        endpointInterface = "PharmacieService",
        targetNamespace = "http://pharmacie.example.com/"
)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public class PharmacieServiceImpl implements PharmacieService {

    // ➕ Ajouter médicament
    @Override
    public String ajouterMedicament(Medicament m) {
        String sql = "INSERT INTO medicament (reference_medicament, nom_medicament, quantite) VALUES (?, ?, ?)";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, m.getReferenceMedicament());
            ps.setString(2, m.getNomMedicament());
            ps.setInt(3, m.getQuantite());
            ps.executeUpdate();

            return "Médicament ajouté : " + m.getNomMedicament();

        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur ajout médicament : " + e.getMessage();
        }
    }

   
    @Override
    public Medicament getMedicamentParReference(String referenceMedicament) {
        String sql = "SELECT * FROM medicament WHERE reference_medicament = ?";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, referenceMedicament);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Medicament(
                        rs.getInt("id"),
                        rs.getString("reference_medicament"),
                        rs.getString("nom_medicament"),
                        rs.getInt("quantite")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 📋 Lister tous les médicaments
    @Override
    public List<Medicament> listeMedicaments() {
        List<Medicament> liste = new ArrayList<>();
        String sql = "SELECT * FROM medicament";

        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(new Medicament(
                        rs.getInt("id"),
                        rs.getString("reference_medicament"),
                        rs.getString("nom_medicament"),
                        rs.getInt("quantite")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

  
    @Override
    public String supprimerMedicamentParReference(String referenceMedicament) {
        String sql = "DELETE FROM medicament WHERE reference_medicament = ?";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, referenceMedicament);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                return "Médicament supprimé (référence : " + referenceMedicament + ")";
            } else {
                return "Aucun médicament trouvé avec cette référence";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur suppression : " + e.getMessage();
        }
    }
}
