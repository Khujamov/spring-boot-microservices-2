package io.echoseven.kryption.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
class User(var email: String,
           var name: String = "Tap to update your name",
           var onlineStatus: Boolean = false,
           var profileImageUrl: String = "noPhoto") {
    @Id
    var id: String? = null
}
