package com.hoanghiep.hust.security;

import java.util.*;
import java.util.stream.Collectors;

import com.hoanghiep.hust.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hoanghiep.hust.entity.User;

public class AuthenticatedUser implements UserDetails{

	private User user;

	public AuthenticatedUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = Set.of(Role.ROLE_USER).stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
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
