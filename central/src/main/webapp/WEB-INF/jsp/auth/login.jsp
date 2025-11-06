<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Connexion - Centralisateur</title>
</head>
<body>
    <h1>Connexion au système</h1>
    
    <c:if test="${not empty param.error}">
        <p><strong>Erreur:</strong> Échec de la connexion. Veuillez vérifier vos identifiants.</p>
    </c:if>
    
    <c:if test="${not empty error}">
        <p><strong>Erreur:</strong> ${error}</p>
    </c:if>
    
    <form action="${pageContext.request.contextPath}/auth/login" method="post">
        <div>
            <label for="name">Nom d'utilisateur:</label>
            <input type="text" id="name" name="name" value="charlie" required>
        </div>
        
        <div>
            <label for="password">Mot de passe:</label>
            <input type="password" id="password" name="password" value="password123" required>
        </div>
        
        <div>
            <button type="submit">Se connecter</button>
        </div>
    </form>
</body>
</html>
