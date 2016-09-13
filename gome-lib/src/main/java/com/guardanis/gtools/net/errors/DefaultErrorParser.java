package com.guardanis.gtools.net.errors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.guardanis.gtools.net.JsonHelper;
import com.guardanis.gtools.net.WebResult;

public class DefaultErrorParser implements ErrorParser {

    @Override
    public List<String> parseErrorMessages(WebResult result) {
        List<String> errorMessages = new ArrayList<String>();

        try{
            JSONObject potentialErrors = result.getResponseJson();

            if(potentialErrors != null)
                errorMessages = parseErrors(potentialErrors);
        }
        catch(Exception e){ e.printStackTrace(); }

        if(!result.isSuccessful() && errorMessages.size() < 1)
            errorMessages.add("Something went wrong!");

        return errorMessages;
    }

    private List<String> parseErrors(JSONObject obj) {
        List<String> errorMessages = new ArrayList<String>();

        if(obj.get("errors") != null)
            return parseErrorsList((JSONObject) obj.get("errors"));
        else if(obj.get("error") != null)
            errorMessages.add(JsonHelper.getString("error", obj, "Something went wrong!"));

        return errorMessages;
    }

    protected List<String> parseErrorsList(JSONObject errors) {
        List<String> errorMessages = new ArrayList<String>();

        try{
            Iterator i = errors.keySet()
                    .iterator();

            while(i.hasNext()){
                String title = String.valueOf(i.next());
                String messageTitle = getBaseErrorTitle(title);

                JSONArray messageArray = (JSONArray) errors.get(title);

                for(int j = 0; j < messageArray.size(); j++){
                    String message = (messageTitle.length() < 1 ? "" : (messageTitle + " ")) + messageArray.get(j);

                    if(Character.isLetter(message.charAt(message.length() - 1)))
                        message += ".";

                    errorMessages.add(message);
                }
            }
        }
        catch(Exception e){ e.printStackTrace(); }

        return errorMessages;
    }

    private static String getBaseErrorTitle(String title) {
        if(title.equals("base"))
            return "";

        String correctTitle = title.replace("_", " ")
                .replace(".", " ");

        return Character.toUpperCase(title.charAt(0)) + correctTitle.substring(1);
    }
}