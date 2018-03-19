package io.echoseven.kryption.domain

import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@EntityListeners(AuditingEntityListener::class)
data class UserAccount(@NotBlank @Column(name = "email", nullable = false) var email: String? = null,
                       @NotBlank @Column(name = "password", nullable = false) var password: String? = null,
                       @Column(name = "verified") var isVerified: Boolean = false) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: String? = null

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    var createdDate: LocalDateTime? = null
}