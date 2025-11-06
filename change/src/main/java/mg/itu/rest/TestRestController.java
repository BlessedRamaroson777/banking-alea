package mg.itu.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/test")
public class TestRestController {
    
    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() {
        return Response.ok()
            .entity("{\"status\":\"ok\",\"message\":\"API is working\"}")
            .build();
    }
    
    @GET
    @Path("/db")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testDb() {
        try {
            mg.itu.database.utils.DB.getConnection();
            return Response.ok()
                .entity("{\"status\":\"ok\",\"message\":\"Database connection successful\"}")
                .build();
        } catch (Exception e) {
            return Response.status(500)
                .entity("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\",\"cause\":\"" + 
                    (e.getCause() != null ? e.getCause().getMessage() : "null") + "\"}")
                .build();
        }
    }
    
    @GET
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testQuery() {
        try {
            java.sql.Connection conn = mg.itu.database.utils.DB.getConnection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM devises LIMIT 2");
            
            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {
                if (json.length() > 1) json.append(",");
                json.append("{");
                json.append("\"id\":").append(rs.getInt("id")).append(",");
                json.append("\"code\":\"").append(rs.getString("code")).append("\",");
                json.append("\"cours\":").append(rs.getBigDecimal("cours"));
                json.append("}");
            }
            json.append("]");
            
            rs.close();
            stmt.close();
            
            return Response.ok().entity(json.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500)
                .entity("{\"status\":\"error\",\"message\":\"" + e.getMessage().replace("\"", "'") + "\"}")
                .build();
        }
    }
}
