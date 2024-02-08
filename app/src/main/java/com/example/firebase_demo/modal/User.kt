import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class User(
    val userKey: String? = null,
    val username: String? = null,
    val number: String? = null,
    val imgUrl: String
) : Serializable { // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
    constructor() : this("", "", "", "")
}