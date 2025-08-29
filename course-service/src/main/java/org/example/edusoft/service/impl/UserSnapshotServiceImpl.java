package org.example.edusoft.service.impl;

import org.example.edusoft.client.UserServiceClient;
import org.example.edusoft.entity.UserSnapshot;
import org.example.edusoft.mapper.UserSnapshotMapper;
import org.example.edusoft.service.UserSnapshotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Service
public class UserSnapshotServiceImpl implements UserSnapshotService {
	private final UserSnapshotMapper userSnapshotMapper;
	private final UserServiceClient userServiceClient;

	@Value("${services.user.base-url:http://localhost:8081}")
	private String userServiceBaseUrl;

	public UserSnapshotServiceImpl(UserSnapshotMapper userSnapshotMapper, UserServiceClient userServiceClient) {
		this.userSnapshotMapper = userSnapshotMapper;
		this.userServiceClient = userServiceClient;
	}

	@Override
	public UserSnapshot ensureSnapshot(String token, String userId) {
		Long uid = Long.parseLong(userId);
		UserSnapshot existing = userSnapshotMapper.selectOne(
			new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserSnapshot>().eq("user_id", uid)
		);
		if (existing != null) {
			return existing;
		}
		Map<String, Object> body = userServiceClient.fetchUserById(userServiceBaseUrl, token, userId);
		if (ObjectUtils.isEmpty(body)) {
			throw new IllegalStateException("无法从用户服务获取用户信息");
		}
		UserSnapshot snapshot = new UserSnapshot();
		snapshot.setUserId(uid);
		snapshot.setUsername(String.valueOf(body.getOrDefault("username", "")));
		snapshot.setRole(String.valueOf(body.getOrDefault("role", "")));
		snapshot.setEmail(String.valueOf(body.getOrDefault("email", "")));
		userSnapshotMapper.insert(snapshot);
		return snapshot;
	}

	@Override
	public UserSnapshot getByUserId(Long userId) {
		return userSnapshotMapper.selectOne(
			new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserSnapshot>().eq("user_id", userId)
		);
	}
}
