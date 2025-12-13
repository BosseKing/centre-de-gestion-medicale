import java.io.Serializable;

public class Medicament implements Serializable {

    private int id; // auto-incrémenté
    private String referenceMedicament;
    private String nomMedicament;
    private int quantite;

    public Medicament() {}

    public Medicament(int id, String referenceMedicament, String nomMedicament, int quantite) {
        this.id = id;
        this.referenceMedicament = referenceMedicament;
        this.nomMedicament = nomMedicament;
        this.quantite = quantite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReferenceMedicament() {
        return referenceMedicament;
    }

    public void setReferenceMedicament(String referenceMedicament) {
        this.referenceMedicament = referenceMedicament;
    }

    public String getNomMedicament() {
        return nomMedicament;
    }

    public void setNomMedicament(String nomMedicament) {
        this.nomMedicament = nomMedicament;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
