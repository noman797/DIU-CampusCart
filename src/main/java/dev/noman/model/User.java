package dev.noman.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Entity
@Table(name = "users")
public class User {

    // Getters / Setters
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String fullName;
    @Getter
    private String email;
    @Getter
    private String password;

    // Set status as PENDING or VERIFIED
    // Add status field (can be "PENDING" or "VERIFIED")
    @Setter
    @Getter
    private String status;

    // Add verification token field
    @Setter
    @Getter
    @Column(name = "verification_token")
    private String verificationToken;

    // Constructors
    public User() {}

    public User(String fullName, String email, String password, String status, String verificationToken) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.status = status;  // PENDING or VERIFIED
        this.verificationToken = verificationToken;
    }

    // Methods to set and get the user's verification status
    public boolean isVerified() {
        return "VERIFIED".equals(this.status);  // Status based verification
    }

    public void setVerified(boolean verified) {
        if (verified) {
            this.status = "VERIFIED";
        } else {
            this.status = "PENDING";
        }
    }

    public String getVerificationToken() {
        return this.verificationToken;
    }

    public Object getName() {
        return fullName;
    }

    // You can add other getters and setters as needed.
}
