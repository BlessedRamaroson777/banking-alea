<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestion des Devises</title>
</head>
<body>
    <h1>Gestion des Devises</h1>
    
    <div>
        <a href="${pageContext.request.contextPath}/home">← Retour à l'accueil</a> | 
        <a href="${pageContext.request.contextPath}/auth/logout">Déconnexion</a>
    </div>
    
    <hr>
    
    <!-- Messages d'erreur et de succès -->
    <c:if test="${not empty error}">
        <div style="color: red; border: 1px solid red; padding: 10px; margin-bottom: 15px;">
            <strong>Erreur:</strong> ${error}
        </div>
    </c:if>
    
    <c:if test="${not empty success}">
        <div style="color: green; border: 1px solid green; padding: 10px; margin-bottom: 15px;">
            <strong>Succès:</strong> ${success}
        </div>
    </c:if>
    
    <!-- Section 1: Formulaire de création de devise -->
    <section>
        <h2>Créer une nouvelle devise</h2>
        <form action="${pageContext.request.contextPath}/devises" method="post">
            <div>
                <label for="code">Code:</label>
                <input type="text" id="code" name="code" required>
            </div>
            
            <div>
                <label for="dateDebut">Date Début:</label>
                <input type="date" id="dateDebut" name="dateDebut" required>
            </div>
            
            <div>
                <label for="dateFin">Date Fin (optionnel):</label>
                <input type="date" id="dateFin" name="dateFin">
            </div>
            
            <div>
                <label for="cours">Cours:</label>
                <input type="number" id="cours" name="cours" step="0.01" required>
            </div>
            
            <input type="hidden" name="statutCode" value="1">
            
            <div>
                <button type="submit">Créer</button>
            </div>
        </form>
    </section>
    
    <hr>
    
    <!-- Section 2: Liste des devises actives -->
    <section>
        <h2>Devises Actives</h2>
        <c:choose>
            <c:when test="${not empty devisesActives}">
                <table border="1">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Code</th>
                            <th>Date Début</th>
                            <th>Date Fin</th>
                            <th>Cours</th>
                            <th>Date Validation</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="devise" items="${devisesActives}">
                            <tr>
                                <td>${devise.id}</td>
                                <td>${devise.code}</td>
                                <td>${devise.dateDebut}</td>
                                <td>${devise.dateFin}</td>
                                <td>${devise.cours}</td>
                                <td>${devise.dateModification}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p>Aucune devise active.</p>
            </c:otherwise>
        </c:choose>
    </section>
    
    <hr>
    
    <!-- Section 3: Liste des demandes de création (en attente) -->
    <section>
        <h2>Demandes de création en attente</h2>
        <c:choose>
            <c:when test="${not empty demandesEnAttente}">
                <table border="1">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Code</th>
                            <th>Date Début</th>
                            <th>Cours</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="demande" items="${demandesEnAttente}">
                            <tr>
                                <td>${demande.id}</td>
                                <td>${demande.code}</td>
                                <td>${demande.dateDebut}</td>
                                <td>${demande.cours}</td>
                                <td>
                                    <!-- Formulaire de validation -->
                                    <form action="${pageContext.request.contextPath}/devises/valider" method="post" style="display:inline;">
                                        <input type="hidden" name="id" value="${demande.id}">
                                        <label for="dateValidation_${demande.id}">Date Validation:</label>
                                        <input type="date" id="dateValidation_${demande.id}" name="dateValidation" required>
                                        <button type="submit">Valider</button>
                                    </form>
                                    
                                    <!-- Formulaire de refus -->
                                    <form action="${pageContext.request.contextPath}/devises/refuser" method="post" style="display:inline;">
                                        <input type="hidden" name="id" value="${demande.id}">
                                        <label for="dateRefus_${demande.id}">Date Refus:</label>
                                        <input type="date" id="dateRefus_${demande.id}" name="dateRefus" required>
                                        <button type="submit">Refuser</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p>Aucune demande en attente.</p>
            </c:otherwise>
        </c:choose>
    </section>
</body>
</html>
