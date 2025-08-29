package org.example.edusoft.service;

import org.example.edusoft.entity.UserSnapshot;

public interface UserSnapshotService {
	UserSnapshot ensureSnapshot(String token, String userId);
	UserSnapshot getByUserId(Long userId);
}
