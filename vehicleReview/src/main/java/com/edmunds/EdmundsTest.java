package com.edmunds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//Uncomment this if you are uncommenting the below code for detailed review
//import java.util.Iterator;

/**
 * This class fetches the vehicle details, ratings and reviews for the given VIN and API Key using 
 * Edmunds.com's Vehicle API and Editorial API.
 */
public class EdmundsTest extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Fetches full Vehicle Details for the given VIN
	 */
	void getVehicleMakeModelYr(String vin, String apiKey,
			HttpServletResponse resp) throws ClientProtocolException,
			IOException {

		PrintWriter pw = resp.getWriter();
		String htmlresp = "<html><h2 style=\"color:Blue\"><br/>Vehicle Details:<br/></h2></html>";
		pw.println(htmlresp);

		HttpClient client = new DefaultHttpClient();
		StringBuffer uri = new StringBuffer();
		String str = "https://api.edmunds.com/api/vehicle/v2/vins/";
		uri.append(str);
		uri.append(vin);
		uri.append("?fmt=json&api_key=");
		uri.append(apiKey);
		HttpGet request = new HttpGet(uri.toString());
		HttpResponse response = client.execute(request); //Making a call to the API and receiving the response.

		if ((response.getStatusLine()).getStatusCode() == 400) { //Check for any error or bad request received in the response
			System.out
					.println("Invalid or bad request. Please provide valid vin.");
			String msg = "<html><h4><br/>Invalid or bad request. Please provide valid VIN.<br/></h4></html>";
			pw.println(msg);
			return;
		}
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		JSONParser jPar = new JSONParser();
		try {
			line = rd.readLine();
			if (line != null) {
				Object obj = jPar.parse(line);
				JSONObject jObj = (JSONObject) obj;
				Object year, years, make, model, makeName, modelName, type, engine, subModel, styles, subModelName;
				years = jObj.get("years");
				make = jObj.get("make");
				model = jObj.get("model");
				engine = jObj.get("engine");
				makeName = ((JSONObject) make).get("niceName");
				modelName = ((JSONObject) model).get("niceName");
				if (engine != null)
					type = ((JSONObject) engine).get("type");
				else
					type = "Fuel type not available";
				years = ((JSONArray) years).get(0);
				year = ((JSONObject) years).get("year");
				styles = ((JSONObject) years).get("styles");
				styles = ((JSONArray) styles).get(0);
				subModel = ((JSONObject) styles).get("submodel");
				subModelName = ((JSONObject) subModel).get("niceName");
				System.out.println("Year: " + year + " \nMake: " + makeName
						+ " \nModel: " + modelName + " \nFuel Type: " + type
						+ "\nsubModel: " + subModelName); //For server logs
				String vDetails = "<html><body><h4>Make: " + makeName
						+ "<br/>Model: " + modelName + "<br/>Year: " + year
						+ "<br/>Fuel Type: " + type + "<br/>subModel: "
						+ subModelName + "</h4></body></html>";
				pw.print(vDetails);
				//Fetch mileage details
				Object mpg, mpgHwy, mpgCity;
				mpg = jObj.get("MPG");
				mpgHwy = ((JSONObject) mpg).get("highway");
				mpgCity = ((JSONObject) mpg).get("city");
				System.out.println("MPG Highway: " + mpgHwy + " \nMPG City: "
						+ mpgCity); //For server logs

				String mpgDetails = "<html><h2 style=\"color:Blue\">Mileage Details:<br/></h2><body><h4>MPG Highway: "
						+ mpgHwy
						+ "<br/>MPG City: "
						+ mpgCity
						+ "</h4></body></html>";
				pw.println(mpgDetails);

				avgConsumerRating(year, makeName, modelName, apiKey, resp); //make a call to fetch the consumer rating
				getVehEdmundsRev(year, makeName, modelName, apiKey, resp); // make a call to obtain the editorial review
			}
		} catch (ParseException pe) {
			System.out.println(pe.getPosition()); //For server logs
			System.out.println(pe);
		} finally {
			rd.close();
			client.getConnectionManager().shutdown();
		}
	}
	/*
	 * Fetches the average consumer rating available in the Vehicle API
	 */
	void avgConsumerRating(Object year, Object makeName, Object modelName,
			String apiKey, HttpServletResponse resp)
			throws ClientProtocolException, IOException {
		
		HttpClient client = new DefaultHttpClient();
		String str2 = "https://api.edmunds.com/api/vehiclereviews/v2/"
				+ makeName + "/" + modelName + "/" + year + "?api_key="
				+ apiKey;		
		HttpGet request = new HttpGet(str2);
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";		
		try {
			line = rd.readLine();
			JSONParser jPar = new JSONParser();
			if (line != null) 
			{
				Object obj = jPar.parse(line);
				JSONObject jObj = (JSONObject) obj;
				Object avgRating;
				avgRating = jObj.get("averageRating");
				if (avgRating == null)
					avgRating = "Consumer rating not available";
				System.out.println("Average Consumer Rating: " + avgRating); //For server logs

				PrintWriter pw = resp.getWriter();
				String cmrRating = "<html><h2 style=\"color:Blue\">Average Consumer Rating: </h2><h4>"
						+ avgRating + "</h4></html>";
				pw.println(cmrRating);

			}
		} catch (ParseException pe) {
			System.out.println(pe.getPosition());
			System.out.println(pe);
		} finally {
			rd.close();
			client.getConnectionManager().shutdown();
		}
	}
	/*
	 * Fetches the Edmunds.com's editor review available in the Editorial API
	 */
	void getVehEdmundsRev(Object year, Object makeName, Object modelName,
			String apiKey, HttpServletResponse resp)
			throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		StringBuffer uri = new StringBuffer();
		String str = "https://api.edmunds.com/v1/content/editorreviews?make=";
		uri.append(str);
		uri.append(makeName + "&model=");
		uri.append(modelName + "&year=");// year=2014;
		uri.append(year + "&fmt=json&api_key=");
		uri.append(apiKey);
		HttpGet request = new HttpGet(uri.toString());
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		JSONParser jPar = new JSONParser();
		try {
			line = rd.readLine();
			if (line != null) 
			{
				Object obj = jPar.parse(line);
				JSONObject jObj = (JSONObject) obj;
				Object editorial, edmundsSays;
				editorial = jObj.get("editorial");
				edmundsSays = ((JSONObject) editorial).get("edmundsSays");
				if (edmundsSays == null) 
					edmundsSays = "Edmunds.com editor review is not available";
				System.out.println("Edmunds Says: " + edmundsSays); //For server logs
				PrintWriter pw = resp.getWriter();
				String edRating = "<html><h2 style=\"color:Blue\">Edmunds.com Editor Review: </h2><h4 width=\"60%\">"
						+ edmundsSays + "</h4></html>";
				pw.println(edRating);
				
				//To obtain detailed review, uncomment the below section
				
				/*JSONObject edObj = (JSONObject)editorial;
				StringBuffer review = new StringBuffer();
				review.append("<html><h3 style=\"color:Blue\">Edmunds.com Detailed Review: </h3>");		
				for (Iterator itr=edObj.keySet().iterator();itr.hasNext();)
				{
					String key=(String)itr.next();
					Object value= (edObj).get(key);
					review.append("<h4>"+ key +":</h4><h5> "+value+"</h5><br/>");
				}
				review.append("</html>");
				pw.println(review);*/

			}
		} catch (ParseException pe) {
			System.out.println(pe.getPosition()); //For server logs
			System.out.println(pe);
		} finally {
			rd.close();
			client.getConnectionManager().shutdown();
		}
	}
	/*
	 * This method fetches the parameters from the request and presents to the user.
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ClientProtocolException, IOException {

		String vin = req.getParameter("vin");
		String apiKey = req.getParameter("apiKey");
		String zc = req.getParameter("zc");

		PrintWriter pw = resp.getWriter();
		String htmlresp = "<html><h3>Entered vin: " + vin + "<br/>Entered Zip Code: " + zc 
				+ "<br/>Your API Key: " + apiKey + "</h3></html>";
		pw.println(htmlresp);

		EdmundsTest testObj = new EdmundsTest();
		testObj.getVehicleMakeModelYr(vin, apiKey, resp);

	}
	/* 
	 * This method directs the request to doPost method
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ClientProtocolException, IOException {

		doPost(req, resp);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
}