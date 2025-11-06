<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="com.google.gson.JsonArray, com.google.gson.JsonElement, com.google.gson.JsonObject" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gestion des Virements</title>
</head>
<body>
    <h1>Gestion des Virements</h1>
    
    <div>
        <a href="${pageContext.request.contextPath}/home">← Retour à l'accueil</a> | 
        <a href="${pageContext.request.contextPath}/auth/logout">Déconnexion</a>
    </div>
    
    <hr>
    
    <!-- Messages de succès ou d'erreur -->
    <c:if test="${not empty sessionScope.successMessage}">
        <div style="color: green; padding: 10px; border: 1px solid green; margin-bottom: 20px;">
            ${sessionScope.successMessage}
        </div>
        <c:remove var="successMessage" scope="session" />
    </c:if>
    
    <c:if test="${not empty sessionScope.error}">
        <div style="color: red; padding: 10px; border: 1px solid red; margin-bottom: 20px;">
            ${sessionScope.error}
        </div>
        <c:remove var="error" scope="session" />
    </c:if>
    
    <!-- Formulaire de création de virement -->
    <h2>Créer un virement</h2>
    <form action="${pageContext.request.contextPath}/virements/creer" method="post">
        <div>
            <label for="compteEnvoyeurId">Compte Envoyeur:</label>
            <select id="compteEnvoyeurId" name="compteEnvoyeurId" required>
                <option value="">-- Sélectionner un compte --</option>
                <%
                    JsonElement comptesElement = (JsonElement) request.getAttribute("comptes");
                    if (comptesElement != null && comptesElement.isJsonArray()) {
                        JsonArray comptes = comptesElement.getAsJsonArray();
                        for (JsonElement element : comptes) {
                            JsonObject compte = element.getAsJsonObject();
                            int id = compte.get("id").getAsInt();
                            double solde = compte.has("solde") ? compte.get("solde").getAsDouble() : 0.0;
                %>
                <option value="<%= id %>">Compte #<%= id %> - (Solde: <%= solde %>)</option>
                <%
                        }
                    }
                %>
            </select>
        </div>
        
        <div>
            <label for="compteDestinataireId">Compte Destinataire:</label>
            <select id="compteDestinataireId" name="compteDestinataireId" required>
                <option value="">-- Sélectionner un compte --</option>
                <%
                    if (comptesElement != null && comptesElement.isJsonArray()) {
                        JsonArray comptes = comptesElement.getAsJsonArray();
                        for (JsonElement element : comptes) {
                            JsonObject compte = element.getAsJsonObject();
                            int id = compte.get("id").getAsInt();
                            double solde = compte.has("solde") ? compte.get("solde").getAsDouble() : 0.0;
                %>
                <option value="<%= id %>">Compte #<%= id %> - (Solde: <%= solde %>)</option>
                <%
                        }
                    }
                %>
            </select>
        </div>
        
        <div>
            <label for="montant">Montant:</label>
            <input type="number" id="montant" name="montant" step="0.01" required>
        </div>
        
        <div>
            <label for="dateCreation">Date de Création:</label>
            <input type="datetime-local" id="dateCreation" name="dateCreation">
        </div>
        
        <div>
            <label for="changeId">Change:</label>
            <select id="changeId" name="changeId">
                <option value="1">-- Défaut (Change 1) --</option>
                <%
                    JsonElement devisesActivesElement = (JsonElement) request.getAttribute("devisesActives");
                    if (devisesActivesElement != null && devisesActivesElement.isJsonArray()) {
                        JsonArray devisesActives = devisesActivesElement.getAsJsonArray();
                        for (JsonElement element : devisesActives) {
                            JsonObject devise = element.getAsJsonObject();
                            int id = devise.get("id").getAsInt();
                            String code = devise.has("code") ? devise.get("code").getAsString() : "N/A";
                            double cours = devise.has("cours") ? devise.get("cours").getAsDouble() : 0.0;
                %>
                <option value="<%= id %>"><%= code %> (Cours: <%= cours %>)</option>
                <%
                        }
                    }
                %>
            </select>
        </div>
        
        <div>
            <button type="submit">Créer le virement</button>
        </div>
    </form>
    
    <!-- Liste des virements par statut -->
    <h2>Liste des Virements</h2>
    
    <!-- Virements en attente (statut = 1) -->
    <h3>Virements en attente</h3>
    <%
        JsonElement virementsEnAttenteElement = (JsonElement) request.getAttribute("virementsEnAttente");
        if (virementsEnAttenteElement != null && virementsEnAttenteElement.isJsonArray()) {
            JsonArray virementsEnAttente = virementsEnAttenteElement.getAsJsonArray();
            if (virementsEnAttente.size() > 0) {
    %>
    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>Montant</th>
                <th>Compte Envoyeur</th>
                <th>Compte Destinataire</th>
                <th>Date Création</th>
                <th>Date Effet</th>
                <th>Change ID</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
                for (JsonElement element : virementsEnAttente) {
                    JsonObject virement = element.getAsJsonObject();
                    int virementId = virement.get("id").getAsInt();
            %>
            <tr>
                <td><%= virement.get("id").getAsInt() %></td>
                <td><%= virement.get("montant").getAsBigDecimal() %></td>
                <td><%= virement.get("compteEnvoyeur").getAsInt() %></td>
                <td><%= virement.get("compteDestinataire").getAsInt() %></td>
                <td><%= virement.get("dateCreation").getAsString() %></td>
                <td><%= virement.get("dateEffet").getAsString() %></td>
                <td><%= virement.get("changeId").getAsInt() %></td>
                <td>
                    <form action="${pageContext.request.contextPath}/virements/<%= virementId %>/valider" method="post" style="display:inline;">
                        <label>Date validation:</label>
                        <input type="datetime-local" name="dateValidation" required>
                        <button type="submit">Valider</button>
                    </form>
                    
                    <form action="${pageContext.request.contextPath}/virements/<%= virementId %>/refuser" method="post" style="display:inline;">
                        <label>Date refus:</label>
                        <input type="datetime-local" name="dateValidation" required>
                        <button type="submit">Refuser</button>
                    </form>
                </td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
    <%
            } else {
    %>
    <p>Aucun virement en attente.</p>
    <%
            }
        } else {
    %>
    <p>Aucun virement en attente.</p>
    <%
        }
    %>
    
    <!-- Virements validés (statut = 11) -->
    <h3>Virements validés</h3>
    <%
        JsonElement virementsValidesElement = (JsonElement) request.getAttribute("virementsValides");
        if (virementsValidesElement != null && virementsValidesElement.isJsonArray()) {
            JsonArray virementsValides = virementsValidesElement.getAsJsonArray();
            if (virementsValides.size() > 0) {
    %>
    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>Montant</th>
                <th>Compte Envoyeur</th>
                <th>Compte Destinataire</th>
                <th>Date Création</th>
                <th>Date Effet</th>
                <th>Change ID</th>
            </tr>
        </thead>
        <tbody>
            <%
                for (JsonElement element : virementsValides) {
                    JsonObject virement = element.getAsJsonObject();
            %>
            <tr>
                <td><%= virement.get("id").getAsInt() %></td>
                <td><%= virement.get("montant").getAsBigDecimal() %></td>
                <td><%= virement.get("compteEnvoyeur").getAsInt() %></td>
                <td><%= virement.get("compteDestinataire").getAsInt() %></td>
                <td><%= virement.get("dateCreation").getAsString() %></td>
                <td><%= virement.get("dateEffet").getAsString() %></td>
                <td><%= virement.get("changeId").getAsInt() %></td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
    <%
            } else {
    %>
    <p>Aucun virement validé.</p>
    <%
            }
        } else {
    %>
    <p>Aucun virement validé.</p>
    <%
        }
    %>
</body>
</html>
