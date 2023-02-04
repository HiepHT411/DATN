package com.hoanghiep.hust.security;

import java.util.*;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hoanghiep.hust.entity.User;

@Data
public class AuthenticatedUser implements UserDetails{

	private User user;

	public User getUser() {
		return user;
	}

	public AuthenticatedUser(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String userRole = user.getRoles().iterator().next().toString();
		List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(userRole));
		return authorities;
	}

	public Long getId() {
		return user.getId();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
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
		return user.isEnabled();
	}


}
