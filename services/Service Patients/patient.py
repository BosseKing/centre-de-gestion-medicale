from spyne import Application, rpc, ServiceBase, Unicode, Integer, Array, ComplexModel
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication
import mysql.connector
from datetime import date

# Connexion à la base MySQL
def get_db_connection():
    return mysql.connector.connect(
        host="127.0.0.1",
        user="root",
        password="",
        database="patients"
    )

# Modèle Patient
class Patient(ComplexModel):
    id = Integer
    numero_cin = Unicode
    nom = Unicode
    prenom = Unicode
    date_naissance = Unicode   # format YYYY-MM-DD
    telephone = Unicode
    ville = Unicode
    adresse = Unicode
    email = Unicode

# Service SOAP
class PatientService(ServiceBase):

    # ➕ Ajouter un patient
    @rpc(Unicode, Unicode, Unicode, Unicode, Unicode, Unicode, Unicode, Unicode, _returns=Unicode)
    def ajouter_patient(ctx, numero_cin, nom, prenom, date_naissance,
                        telephone, ville, adresse, email):

        conn = get_db_connection()
        cursor = conn.cursor()

        cursor.execute("""
            INSERT INTO patients 
            (numero_cin, nom, prenom, date_naissance, telephone, ville, adresse, email)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
        """, (numero_cin, nom, prenom, date_naissance,
              telephone, ville, adresse, email))

        conn.commit()
        cursor.close()
        conn.close()

        return f"Patient {nom} {prenom} ajouté avec succès"

    # 📋 Lister tous les patients
    @rpc(_returns=Array(Patient))
    def lister_patients(ctx):
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)

        cursor.execute("SELECT * FROM patients")
        rows = cursor.fetchall()

        patients = []
        for r in rows:
            p = Patient()
            p.id = r['id']
            p.numero_cin = r['numero_cin']
            p.nom = r['nom']
            p.prenom = r['prenom']
            p.date_naissance = str(r['date_naissance'])
            p.telephone = r['telephone']
            p.ville = r['ville']
            p.adresse = r['adresse']
            p.email = r['email']
            patients.append(p)

        cursor.close()
        conn.close()
        return patients

    # ❌ Supprimer patient par ID
    @rpc(Unicode, _returns=Unicode)
    def supprimer_patient_par_cin(ctx, numero_cin):
        conn = get_db_connection()
        cursor = conn.cursor()

        cursor.execute(
            "DELETE FROM patients WHERE numero_cin = %s",
            (numero_cin,)
        )
        conn.commit()

        if cursor.rowcount > 0:
            message = f"Patient avec CIN {numero_cin} supprimé avec succès"
        else:
            message = f"Aucun patient trouvé avec le CIN {numero_cin}"

        cursor.close()
        conn.close()
        return message


    # 🔍 Rechercher par CIN
    @rpc(Unicode, _returns=Array(Patient))
    def rechercher_par_cin(ctx, numero_cin):
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)

        cursor.execute("SELECT * FROM patients WHERE numero_cin=%s", (numero_cin,))
        rows = cursor.fetchall()

        patients = []
        for r in rows:
            p = Patient()
            p.id = r['id']
            p.numero_cin = r['numero_cin']
            p.nom = r['nom']
            p.prenom = r['prenom']
            p.date_naissance = str(r['date_naissance'])
            p.telephone = r['telephone']
            p.ville = r['ville']
            p.adresse = r['adresse']
            p.email = r['email']
            patients.append(p)

        cursor.close()
        conn.close()
        return patients


# Création de l'application SOAP
application = Application(
    [PatientService],
    tns='spyne.patients.soap',
    in_protocol=Soap11(validator='lxml'),
    out_protocol=Soap11()
)

# Lancement du serveur
if __name__ == '__main__':
    from wsgiref.simple_server import make_server
    server = make_server('0.0.0.0', 8000, WsgiApplication(application))
    print("Service SOAP Patient actif sur http://0.0.0.0:8000")
    server.serve_forever()
