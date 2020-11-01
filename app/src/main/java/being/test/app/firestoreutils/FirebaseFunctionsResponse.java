package being.test.app.firestoreutils;

import org.json.JSONObject;

public interface FirebaseFunctionsResponse {
    public void firebaseFunctionsResponse(JSONObject jsonArray, String type);
    public void dataAddSuccess(boolean successful);
    public void dataUpdateSuccess(boolean successful);
}
