import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val username: String? = null,
    val number: String? = null,
    val imgUrl: String
) { // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
    constructor() : this("", "", "")
}