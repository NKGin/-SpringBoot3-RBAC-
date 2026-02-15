package com.ginwind.springrbac.security.domain;

import com.ginwind.springrbac.constant.StatusConstant;
import com.ginwind.springrbac.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
public class LoginUser implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String status;
    private final List<String> permissions; // 权限列表


    /**
     * Spring Security 真正使用的权限对象
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
    }

    /**
     * 密码（数据库里的）
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 用户名
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 账户是否未过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 凭证是否未过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 是否启用（可以绑定用户状态）
     */
    @Override
    public boolean isEnabled() {
        return Objects.equals(status, StatusConstant.ENABLE);
    }

}

