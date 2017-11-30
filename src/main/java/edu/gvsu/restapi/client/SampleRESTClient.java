package edu.gvsu.restapi.client;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.data.*;
import org.restlet.*;
import org.restlet.representation.Representation;

/**
 * Sample client program that uses the RESTlet framework to access a RESTful web service.
 *
 * @author Jonathan Engelsma (http://themobilemontage.com)
 */
public class SampleRESTClient implements PresenceService {

    // The base URL for all requests.
    public static final String APPLICATION_URI = "http://lab4-187600.appspot.com";


    public void register(RegistrationInfo reg) throws Exception {
        //Post to /users

        if(lookup(reg.getUserName())== null) {

            JSONObject registration = new JSONObject();
            registration.put("name", reg.getUserName());
            registration.put("ipAddress", reg.getHost());
            registration.put("port", reg.getPort());
            registration.put("status", true);
            String usersURL = APPLICATION_URI + "/users";
            Request request = new Request(Method.POST, usersURL);
            request.getClientInfo().getAcceptedMediaTypes().add(new Preference(MediaType.APPLICATION_JSON));
            request.setEntity(registration.toString(), MediaType.APPLICATION_JSON);
//            System.out.println(registration.toString());
            Response resp = new Client(Protocol.HTTP).handle(request);
            System.out.println(resp);
        }
        else{
            System.out.println("This user already exists");
            System.exit(1);
        }
    }

    public void unregister(String userName) throws Exception {
        // delete /users/{name}
        String usersResourceURL = APPLICATION_URI + "/users/" + userName;
        Request request = new Request(Method.DELETE, usersResourceURL);
        request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));
        Response resp = new Client(Protocol.HTTP).handle(request);
        System.out.println(resp);

    }

    public RegistrationInfo lookup(String name) throws Exception {
        //get users/{name}
        String usersResourceURL = APPLICATION_URI + "/users/" + name;
        Request request = new Request(Method.GET, usersResourceURL);
        RegistrationInfo regInfo = new RegistrationInfo();

        //asking for a JSON
        request.getClientInfo().getAcceptedMediaTypes().add(new Preference(MediaType.APPLICATION_JSON));

        //handle the response
        Response resp = new Client(Protocol.HTTP).handle(request);
        System.out.println(resp);

        if (resp.getStatus().getCode()== 200) {
            Representation responseData = resp.getEntity();
            System.out.println("Status = " + resp.getStatus());

            String jsonString = responseData.getText().toString();

            Map<String, Object> responseJSON = new JSONObject(jsonString).toMap();


            regInfo.setUserName(responseJSON.get("name").toString());
            regInfo.setHost(responseJSON.get("ipAddress").toString());
            regInfo.setPort(Integer.parseInt(responseJSON.get("port").toString()));
            regInfo.setStatus(Boolean.parseBoolean(responseJSON.get("status").toString()));
//            System.out.println(regInfo.toString());

        }
        else{
            return null;
        }

        return regInfo;
    }

    public void setStatus(String userName, boolean status) throws Exception {
        //put users/{name}
        JSONObject jobject = new JSONObject();
        jobject.put("name", userName);
        jobject.put("status", status);

        String usersResourceURL = APPLICATION_URI + "/users/" + userName;
        Request request = new Request(Method.PUT, usersResourceURL);
        request.getClientInfo().getAcceptedMediaTypes().add(new Preference(MediaType.APPLICATION_JSON));
        request.setEntity(jobject.toString(), MediaType.APPLICATION_JSON);
        Response resp = new Client(Protocol.HTTP).handle(request);
        System.out.println(resp);
    }


    //returns a list of all registered Users from the /users
    public RegistrationInfo[] listRegisteredUsers() {
        String usersResourceURL = APPLICATION_URI + "/users";
        Request request = new Request(Method.GET, usersResourceURL);
        RegistrationInfo[] registeredUsers = new RegistrationInfo[0];

        // We need to ask specifically for JSON
        request.getClientInfo().getAcceptedMediaTypes().add(new Preference(MediaType.APPLICATION_JSON));

        // Now we do the HTTP GET
        System.out.println("Sending an HTTP GET to " + usersResourceURL + ".");
        Response resp = new Client(Protocol.HTTP).handle(request);

        // Let's see what we got!
        if (resp.getStatus().equals(Status.SUCCESS_OK)) {

            Representation responseData = resp.getEntity();
            System.out.println("Status = " + resp.getStatus());
            try {

                String jsonString = responseData.getText().toString();

                JSONArray jArray = new JSONArray(jsonString);
                registeredUsers = new RegistrationInfo[jArray.length()];

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jobj = jArray.getJSONObject(i);
                    Map tempMap = jobj.toMap();
                    String IP = tempMap.get("ipAddress").toString();
                    String name = tempMap.get("name").toString();
                    int port = Integer.parseInt(tempMap.get("port").toString());
                    Boolean status = Boolean.parseBoolean(tempMap.get("status").toString());


                    //sets the valuse for created Registration Info Object.
                    RegistrationInfo userRegInfo = new RegistrationInfo();
                    userRegInfo.setUserName(name);
                    userRegInfo.setHost(IP);
                    userRegInfo.setStatus(status);
                    userRegInfo.setPort(port);
                    registeredUsers[i] = userRegInfo;
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }


        return registeredUsers;

    }
}
