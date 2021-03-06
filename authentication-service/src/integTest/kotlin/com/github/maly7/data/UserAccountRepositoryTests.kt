package com.github.maly7.data

import com.github.maly7.AuthIntegrationTest
import com.github.maly7.domain.UserAccount
import com.github.maly7.support.UserAccountCleanup
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(SpringRunner::class)
@AuthIntegrationTest
class UserAccountRepositoryTests : UserAccountCleanup() {

    @Test
    fun `new user account creation`() {
        val userAccount = userAccountRepository.save(UserAccount("email@email.com", "password", false))

        assertNotNull(userAccount.createdDate, "new user accounts should have created dates")
        assertNotNull(userAccount.id, "The id should be generated by hibernate")
    }

    @Test
    fun `user account updating`() {
        val originalAccount = userAccountRepository.save(UserAccount("email@email.com", "password", false))
        val newPassword = "newer password"
        val newEmail = "new@email.com"

        originalAccount.password = newPassword
        originalAccount.isVerified = true
        originalAccount.email = newEmail

        val updatedAccount = userAccountRepository.save(originalAccount)

        assertEquals(originalAccount.id, updatedAccount.id, "The account id should not change")
        assertEquals(originalAccount.createdDate, updatedAccount.createdDate, "The created date should not change")
        assertEquals(newPassword, updatedAccount.password, "The password should update")
        assertEquals(newEmail, updatedAccount.email, "The email should be updated")
        assertTrue(updatedAccount.isVerified, "The account should now be verified")
    }

    @Test
    fun `user account deletion`() {
        val originalAccount = userAccountRepository.save(UserAccount("email@foo.com", "p@ssw0rd", false))

        userAccountRepository.deleteById(originalAccount.id!!)

        assertFalse(
            userAccountRepository.findById(originalAccount.id!!).isPresent,
            "The user account should be deleted"
        )
    }
}
