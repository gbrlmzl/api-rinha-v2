package rinhacampusiv.api.v2.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String password;


    // camelCase → snake_case: profilePic → profile_pic
    @Column(name = "profile_pic", length = 255)
    private String profilePic;

    // camelCase → snake_case: createdAt → created_at
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
        // USER  → "ROLE_USER"
        // ADMIN → "ROLE_ADMIN"
    }

    public User(RegisterData registerData){
        this.active = true;
        this.role   = UserRole.USER;
        this.nickname = registerData.email().substring(0, registerData.email().indexOf("@"));;
        this.username = registerData.username();
        this.password = registerData.password();
        this.email    = registerData.email();
        this.profilePic = null;
    }

    public void update(UserDataUpdateDTO data, PasswordEncoder encoder) {
        if (data.nickname() != null) {
            this.nickname = data.nickname();
        }

        if (data.newPassword() != null) {
            // A validação de currentPassword é feita no controller
            this.password = encoder.encode(data.newPassword());
        }

        if (data.profilePic() != null){
            this.profilePic = data.profilePic();
        }
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