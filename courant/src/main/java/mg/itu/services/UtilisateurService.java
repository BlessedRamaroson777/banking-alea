package mg.itu.services;

import jakarta.ejb.Stateless;
import mg.itu.database.utils.DB;
import mg.itu.models.Utilisateur;

import java.sql.Connection;

@Stateless
public class UtilisateurService implements RemoteUtilisateurService {

    @Override
    public Utilisateur authenticate(String nom, String motDePasse) {
        Connection connection = DB.getConnection();

        Utilisateur utilisateur = new Utilisateur(nom, motDePasse);
        utilisateur.authenticate(connection);

        return utilisateur;
    }
}
