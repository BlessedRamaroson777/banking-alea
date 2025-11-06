<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Accueil - Gestion Bancaire</title>
</head>
<body>
    <h1>Bienvenue dans le système de gestion bancaire</h1>
    
    <div>
        <h3>Informations utilisateur</h3>
        <p><strong>Nom:</strong> ${sessionScope.utilisateurNom}</p>
        <p><strong>Niveau:</strong> ${sessionScope.utilisateurNiveau}</p>
        <p><strong>ID:</strong> ${sessionScope.utilisateurId}</p>
        
        <h4>ActionRoles assignés:</h4>
        <c:choose>
            <c:when test="${not empty sessionScope.actionRoles}">
                <table border="1" cellpadding="5" cellspacing="0">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Table</th>
                            <th>Action</th>
                            <th>Rôle Minimum</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="role" items="${sessionScope.actionRoles}">
                            <tr>
                                <td>${role.id}</td>
                                <td>${role.nomTable}</td>
                                <td>${role.nomAction}</td>
                                <td>${role.roleMinimum}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p><em>Aucun ActionRole assigné</em></p>
            </c:otherwise>
        </c:choose>
        
        <p style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/auth/logout">Déconnexion</a>
        </p>
    </div>
    
    <div style="margin: 20px 0;">
        <h3>Navigation</h3>
        <a href="${pageContext.request.contextPath}/virements" >
            Virements
        </a>
        
        <a href="${pageContext.request.contextPath}/devises" >
            Devises
        </a>
    </div>
</body>
</html>
