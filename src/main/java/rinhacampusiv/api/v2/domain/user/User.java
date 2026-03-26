package rinhacampusiv.api.v2.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Entity(name = "User")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean active;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String password;
    @Column(length = 255)
    private String profilePic;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Implementação obrigatória do UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Aqui você pode trocar por roles vindas do banco
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public User(RegisterData registerData){
        this.active = true;
        this.nickname = registerData.email().substring(0, registerData.email().indexOf("@"));;
        this.username = registerData.username();
        this.password = registerData.password();
        this.email    = registerData.email();
        this.profilePic = null;
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