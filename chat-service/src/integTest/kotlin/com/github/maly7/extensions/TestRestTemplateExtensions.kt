package com.github.maly7.extensions

import com.github.maly7.domain.Conversation
import com.github.maly7.domain.ConversationMessage
import com.github.maly7.domain.User
import com.github.maly7.support.authHeaders
import com.github.maly7.support.generateContactRequest
import com.github.maly7.web.resource.ContactRequest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

fun <T> TestRestTemplate.getForEntity(uri: String, headers: HttpHeaders, responseType: Class<T>): ResponseEntity<T> {
    val entity = HttpEntity("", headers)
    return this.exchange(uri, HttpMethod.GET, entity, responseType)
}

fun TestRestTemplate.deleteEntity(uri: String, headers: HttpHeaders): ResponseEntity<Void> {
    val entity = HttpEntity("", headers)
    return this.exchange(uri, org.springframework.http.HttpMethod.DELETE, entity, Void::class.java)
}

fun TestRestTemplate.createUser(user: User) = this.postForEntity("/user", user, User::class.java).body!!

fun TestRestTemplate.addContact(authToken: String, contact: User): ResponseEntity<String> {
    val requestEntity = HttpEntity(ContactRequest(contact.email), authHeaders(authToken))
    return this.postForEntity("/contacts", requestEntity, String::class.java)
}

fun TestRestTemplate.createUserAsContact(
    authToken: String,
    contact: ContactRequest = generateContactRequest()
): String {
    this.createUser(User(contact.email!!))

    val requestEntity = HttpEntity(contact, authHeaders(authToken))
    this.postForEntity("/contacts", requestEntity, String::class.java)
    return contact.email!!
}

fun TestRestTemplate.getContacts(authToken: String) =
    this.getForEntity("/contacts", authHeaders(authToken), List::class.java)

fun TestRestTemplate.deleteContact(authToken: String, email: String) {
    val requestEntity = HttpEntity(ContactRequest(email), authHeaders(authToken))
    this.exchange("/contacts", HttpMethod.DELETE, requestEntity, Any::class.java)
}

fun TestRestTemplate.sendConversationMessage(
    authToken: String,
    toId: String,
    message: String
): ResponseEntity<Conversation> {
    val chatMessage = ConversationMessage(message = message, toId = toId)
    val requestEntity = HttpEntity(chatMessage, authHeaders(authToken))
    return this.postForEntity("/conversation/message", requestEntity, Conversation::class.java)
}

fun TestRestTemplate.deleteConversation(authToken: String, conversation: Conversation) =
    this.deleteEntity("/conversation/${conversation.id}", authHeaders(authToken))

fun TestRestTemplate.deleteMessage(authToken: String, message: ConversationMessage) =
    this.deleteEntity("/conversation/message/${message.id}", authHeaders(authToken))
