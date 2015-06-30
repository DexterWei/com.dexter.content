package com.dexter.content;

import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


import com.dexter.content.dao.MongoDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

@Path("/")
public class RegistrationServer{
	
	@Path("/register")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Bootstrap(String objString) throws UnknownHostException, JSONException {
		MongoDAO dao = new MongoDAO();
		MongoDAO.Connect();
		JSONObject obj = new JSONObject(objString);
		if("Register".equals(obj.get("Request")) ){
			//Handle Register request
			obj.remove("Request");
			if(!MongoDAO.FindModel((String)obj.get("Manufacturer"),(String)obj.get("Model"))){
				//202 (Accepted) "The request has been accepted for processing, but the processing has not been completed"
				return Response.status(202).entity("unsupported manufacturer or model").build();
			}else{
				if(!MongoDAO.FindSubscriber(obj)){
					MongoDAO.InsertSubscriber(obj);
					return Response.status(201).entity("Registration Done").build();
				}else{
					return Response.status(202).entity("Subscriber already registered").build();
				}
			}
		
		}else if("Update".equals(obj.get("Request")) ){
			//Handle Update request
			obj.remove("Request");
			if(!MongoDAO.FindSubscriber(obj)){
					return Response.status(202).entity("Nonexist subscriber").build();
			}else{
				String rst = MongoDAO.UpdateSubscriber(obj);
				return Response.status(201).entity(rst).build();
			}
			
		}else if("De-register".equals(obj.get("Request")) ){
			//Handle De-register request
			obj.remove("Request");
			if(!MongoDAO.FindSubscriber(obj)){
					return Response.status(202).entity("Nonexist subscriber").build();
			}else{
				String rst = MongoDAO.DeregisterSubscriber(obj);
				return Response.status(201).entity(rst).build();
			}
		}else {
			//error code 400 (Bad Request) "The request could not be understood by the server due to malformed syntax"
			return Response.status(400).entity("request options: Register, Update or De-register").build();
		}
	}
	
}

