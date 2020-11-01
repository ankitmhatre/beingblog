package being.test.app.firestoreutils

import org.json.JSONObject

interface FirebaseFunctionsResponse {
    fun firebaseFunctionsResponse(jsonArray: JSONObject?, type: String?)
    fun dataAddSuccess(successful: Boolean)
    fun dataUpdateSuccess(successful: Boolean)
    fun checkFavorite(isFavorite: Boolean)
    fun toggleFavorite(isFavorite: Boolean)
}