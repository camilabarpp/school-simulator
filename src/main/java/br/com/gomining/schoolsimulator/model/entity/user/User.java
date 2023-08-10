package br.com.gomining.schoolsimulator.model.entity.user;

import br.com.gomining.schoolsimulator.enun.ERole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@Document(collection = "Users")
public class User implements UserDetails {

    @Id
    @ApiModelProperty(notes = "The database generated user ID")
    private String id;
    @ApiModelProperty(notes = "The username of the user")
    private String username;
    @ApiModelProperty(notes = "The password of the user")
    private String password;
    @ApiModelProperty(notes = "The roles of the user")
    private ERole roles;

    public User(String username, String encryptedPassword, ERole role) {
        this.username = username;
        this.password = encryptedPassword;
        this.roles = role;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.roles == ERole.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
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
