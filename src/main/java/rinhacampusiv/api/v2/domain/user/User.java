package rinhacampusiv.api.v2.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table(name = "users")
@Entity(name = "User")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private boolean active;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String password;
    @Column(length = 255)
    private String profilePic;

    @Column(nullable = false, name = "createdAt")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Implementação obrigatória do UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Aqui você pode trocar por roles vindas do banco
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public User(RegisterData registerData){
        this.active = true;
        this.nickname = registerData.nickname();
        this.username = registerData.username();
        this.password = registerData.password();
        this.email    = registerData.email();
        this.profilePic = registerData.profilePic();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}