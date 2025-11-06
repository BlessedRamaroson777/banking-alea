package mg.itu.services;

import jakarta.ejb.Remote;
import mg.itu.models.Utilisateur;

@Remote
public interface RemoteUtilisateurService {

    Utilisateur authenticate(String nom, String motDePasse);
}
