package mg.itu.utils;

import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import mg.itu.services.RemoteCompteService;
import mg.itu.services.RemoteUtilisateurService;
import mg.itu.services.RemoteVirementService;
import mg.itu.ejb.remote.DeviseRemote;
import mg.itu.ejb.remote.DeviseModificationRemote;
import mg.itu.ejb.remote.AuthRemote;
import mg.itu.ejb.remote.CodeStatutDeviseRemote;
import mg.itu.ejb.remote.ActionRoleRemote;

/**
 * Utilitaire pour effectuer des lookups JNDI des EJB distants
 */
public class LookupJNDIHelper {
    private static final Logger LOG = Logger.getLogger(LookupJNDIHelper.class.getName());
    
    // ===========================================
    // Services Courant (local - même conteneur)
    // ===========================================
    
    public static RemoteCompteService getCompteService() throws NamingException {
        String jndi = ConfigProperties.getProperty("jndi.courant.compte");
        LOG.info(() -> "Looking up CompteService via JNDI: " + jndi);
        return (RemoteCompteService) new InitialContext().lookup(jndi);
    }
    
    public static RemoteVirementService getVirementService() throws NamingException {
        String jndi = ConfigProperties.getProperty("jndi.courant.virement");
        LOG.info(() -> "Looking up VirementService via JNDI: " + jndi);
        return (RemoteVirementService) new InitialContext().lookup(jndi);
    }
    
    public static RemoteUtilisateurService getUtilisateurService() throws NamingException {
        String jndi = ConfigProperties.getProperty("jndi.courant.utilisateur");
        LOG.info(() -> "Looking up UtilisateurService via JNDI: " + jndi);
        return (RemoteUtilisateurService) new InitialContext().lookup(jndi);
    }
    
    // ===========================================
    // Services Change (remote - Docker)
    // ===========================================
    
    public static DeviseRemote getDeviseService() throws NamingException {
        // Lookup distant EJB sur le conteneur Docker Wildfly
        String jndi = ConfigProperties.getProperty("jndi.change.devise");
        LOG.info(() -> "Remote lookup DeviseService via JNDI: " + jndi);
        
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, ConfigProperties.getProperty("remote.change.provider.url"));
        props.put(Context.SECURITY_PRINCIPAL, ConfigProperties.getProperty("remote.change.security.principal"));
        props.put(Context.SECURITY_CREDENTIALS, ConfigProperties.getProperty("remote.change.security.credentials"));
        props.put("jboss.naming.client.ejb.context", true);
        
        InitialContext ctx = new InitialContext(props);
        return (DeviseRemote) ctx.lookup(jndi);
    }
    
    public static DeviseModificationRemote getDeviseModificationService() throws NamingException {
        // Lookup distant EJB sur le conteneur Docker Wildfly
        String jndi = ConfigProperties.getProperty("jndi.change.devisemodification");
        LOG.info(() -> "Remote lookup DeviseModificationService via JNDI: " + jndi);
        
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, ConfigProperties.getProperty("remote.change.provider.url"));
        props.put(Context.SECURITY_PRINCIPAL, ConfigProperties.getProperty("remote.change.security.principal"));
        props.put(Context.SECURITY_CREDENTIALS, ConfigProperties.getProperty("remote.change.security.credentials"));
        props.put("jboss.naming.client.ejb.context", true);
        
        InitialContext ctx = new InitialContext(props);
        return (DeviseModificationRemote) ctx.lookup(jndi);
    }
    
    public static AuthRemote getAuthService() throws NamingException {
        // Lookup distant EJB sur le conteneur Docker Wildfly
        String jndi = ConfigProperties.getProperty("jndi.change.auth");
        LOG.info(() -> "Remote lookup AuthService via JNDI: " + jndi);
        
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, ConfigProperties.getProperty("remote.change.provider.url"));
        props.put(Context.SECURITY_PRINCIPAL, ConfigProperties.getProperty("remote.change.security.principal"));
        props.put(Context.SECURITY_CREDENTIALS, ConfigProperties.getProperty("remote.change.security.credentials"));
        props.put("jboss.naming.client.ejb.context", true);
        
        InitialContext ctx = new InitialContext(props);
        return (AuthRemote) ctx.lookup(jndi);
    }
    
    public static CodeStatutDeviseRemote getCodeStatutDeviseService() throws NamingException {
        // Lookup distant EJB sur le conteneur Docker Wildfly
        String jndi = ConfigProperties.getProperty("jndi.change.codestatutdevise");
        LOG.info(() -> "Remote lookup CodeStatutDeviseService via JNDI: " + jndi);
        
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, ConfigProperties.getProperty("remote.change.provider.url"));
        props.put(Context.SECURITY_PRINCIPAL, ConfigProperties.getProperty("remote.change.security.principal"));
        props.put(Context.SECURITY_CREDENTIALS, ConfigProperties.getProperty("remote.change.security.credentials"));
        props.put("jboss.naming.client.ejb.context", true);
        
        InitialContext ctx = new InitialContext(props);
        return (CodeStatutDeviseRemote) ctx.lookup(jndi);
    }
    
    public static ActionRoleRemote getActionRoleService() throws NamingException {
        // Lookup distant EJB sur le conteneur Docker Wildfly
        String jndi = ConfigProperties.getProperty("jndi.change.actionrole");
        LOG.info(() -> "Remote lookup ActionRoleService via JNDI: " + jndi);
        
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, ConfigProperties.getProperty("remote.change.provider.url"));
        props.put(Context.SECURITY_PRINCIPAL, ConfigProperties.getProperty("remote.change.security.principal"));
        props.put(Context.SECURITY_CREDENTIALS, ConfigProperties.getProperty("remote.change.security.credentials"));
        props.put("jboss.naming.client.ejb.context", true);
        
        InitialContext ctx = new InitialContext(props);
        return (ActionRoleRemote) ctx.lookup(jndi);
    }
}
