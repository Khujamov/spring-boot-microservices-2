package com.github.maly7.web

import com.beust.klaxon.Klaxon
import com.github.maly7.AuthIntegrationTest
import com.github.maly7.domain.UserAccount
import com.github.maly7.support.UserAccountCleanup
import com.github.maly7.tokens.TokenIssuer
import com.github.maly7.web.resource.TokenResource
import com.github.maly7.web.resource.UserAccountResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@AuthIntegrationTest
@RunWith(SpringRunner::class)
class UserLoginTests : UserAccountCleanup() {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var tokenIssuer: TokenIssuer

    private val userEmail = "login-user@email.com"
    private val userPassword = "login-password"

    @Before
    fun setup() {
        restTemplate.postForEntity("/user", UserAccount(userEmail, userPassword), String::class.java)
    }

    @Test
    fun `Attempting to login as a non-existing user should fail`() {
        val failedResponse =
            restTemplate.postForEntity("/login", UserAccount("bad-email", "does not matter"), String::class.java)

        assertTrue(failedResponse.statusCode.is4xxClientError, "The login request should fail")
        assertEquals(HttpStatus.BAD_REQUEST, failedResponse.statusCode)
    }

    @Test
    fun `Attempting to login with a bad password should fail`() {
        val failedResponse =
            restTemplate.postForEntity("/login", UserAccount(userEmail, "bad password"), String::class.java)

        assertTrue(failedResponse.statusCode.is4xxClientError, "The login request should fail")
        assertEquals(HttpStatus.UNAUTHORIZED, failedResponse.statusCode)
    }

    @Test
    fun `Using the right credentials should login successfully`() {
        val successfulResponse =
            restTemplate.postForEntity("/login", UserAccount(userEmail, userPassword), String::class.java)

        assertTrue(successfulResponse.statusCode.is2xxSuccessful, "The login request should succeed")
        assertEquals(HttpStatus.OK, successfulResponse.statusCode)

        val token: String = Klaxon().parse<TokenResource>(successfulResponse.body!!)?.token ?: ""

        assertEquals(userEmail, tokenIssuer.getEmailFromToken(token))
        assertEquals(userAccountRepository.findByEmail(userEmail).get().id, tokenIssuer.getIdFromToken(token))
    }

    @Test
    fun `Users can be retrieved with their JWT tokens`() {
        val successfulResponse =
            restTemplate.postForEntity("/login", UserAccount(userEmail, userPassword), String::class.java)

        assertTrue(successfulResponse.statusCode.is2xxSuccessful, "The login request should succeed")
        assertEquals(HttpStatus.OK, successfulResponse.statusCode)

        val token: String = Klaxon().parse<TokenResource>(successfulResponse.body!!)?.token ?: ""

        val userLookupResponse =
            restTemplate.postForEntity("/authenticate", TokenResource(token), UserAccountResource::class.java)

        assertTrue(userLookupResponse.statusCode.is2xxSuccessful, "The lookup request should succeed")
        assertEquals(HttpStatus.OK, userLookupResponse.statusCode)

        val user = userLookupResponse.body

        assertEquals(userEmail, user?.email, "The token user email should be the same as what we used to login with")
    }

    @Test
    fun `Users can be retrieved with their JWT token in a header`() {
        val successfulResponse =
            restTemplate.postForEntity("/login", UserAccount(userEmail, userPassword), String::class.java)

        assertTrue(successfulResponse.statusCode.is2xxSuccessful, "The login request should succeed")
        assertEquals(HttpStatus.OK, successfulResponse.statusCode)
        val token: String = Klaxon().parse<TokenResource>(successfulResponse.body!!)?.token ?: ""

        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON_UTF8)
        headers[HttpHeaders.AUTHORIZATION] = listOf(token)

        val entity = HttpEntity("", headers)

        val userLookupResponse =
            restTemplate.exchange("/authenticate", HttpMethod.GET, entity, UserAccountResource::class.java)

        assertTrue(userLookupResponse.statusCode.is2xxSuccessful, "The lookup request should succeed")
        assertEquals(HttpStatus.OK, userLookupResponse.statusCode)

        val user = userLookupResponse.body

        assertEquals(userEmail, user?.email, "The token user email should be the same as what we used to login with")
    }
}
