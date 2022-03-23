package com.dmm.task.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dmm.task.entity.Users;
import com.dmm.task.repository.UsersRepository;

@Service // Spring管理Beanであることを指定
public class AccountUserDetailsService implements UserDetailsService {

	@Autowired
	private UsersRepository repository;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		if (userName == null || "".equals(userName)) {
			throw new UsernameNotFoundException("ユーザー名が空です");
		}
		// データベースからアカウント情報を取得する
		Users user = repository.findById(userName).get();
		if (user != null) {
			// UserDetailsの実装クラスを生成して返す
			return new AccountUserDetails(user);
		}
		throw new UsernameNotFoundException(userName + "は見つかりません。");
	}
}