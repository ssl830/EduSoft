<template>
  <div class="profile-container min-h-screen">
    <!-- 顶部导航条 -->
    <div class="top-nav">
      <div class="nav-links">
        <a href="/">首页</a>
        <a href="/course">教学空间</a>
        <a href="#" class="dropdown-toggle">优质课程 <span class="arrow-down">▼</span></a>
        <a href="/help">帮助中心</a>
        <a href="/feedback">意见反馈</a>
      </div>
    </div>
    
    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 页面标题 -->
      <h1 class="page-title">维护个人信息</h1>
      
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
      </div>
      
      <div v-else-if="error" class="error-message">
        <p class="error-title">获取信息失败</p>
        <p>{{ error }}</p>
      </div>
        <div v-else class="profile-content">
        <!-- 左侧信息卡片 -->
        <div class="profile-sidebar">
          <div class="profile-card">
            <div class="avatar-container">
              <div class="avatar-wrapper">
                <img 
                  :src="userProfile.avatar || defaultAvatar" 
                  alt="用户头像" 
                  class="avatar-image"
                  @error="handleAvatarError"
                />
              </div>              <div class="file-input-container">
                <input 
                  type="file" 
                  @change="handleFileChange" 
                  accept="image/*" 
                  class="file-input" 
                  id="avatarUpload"
                />
                <label for="avatarUpload" class="upload-label">选择文件</label>
              </div>
            </div>
            <h2 class="user-name">{{ userProfile.username }}</h2>
            <span class="user-role">
              {{ roleText }}
            </span>
            <div class="user-info">
              <div class="info-item">
                <svg xmlns="http://www.w3.org/2000/svg" class="info-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                <span class="info-text">{{ userProfile.email }}</span>
              </div>
              
              <div class="info-item">
                <svg xmlns="http://www.w3.org/2000/svg" class="info-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                <span class="info-text">注册时间: {{ formattedDate }}</span>
              </div>
            </div>
          </div>
        </div>
          <!-- 右侧表单部分 -->
        <div class="profile-main">
          <!-- 标签切换 -->
          <div class="form-container">
            <div class="tabs-container">
              <nav class="tabs-nav" aria-label="Tabs">
                <button 
                  @click="activeTab = 'profile'" 
                  :class="[
                    'tab-button',
                    activeTab === 'profile' ? 'active' : ''
                  ]"
                >
                  个人资料
                </button>
                <button 
                  @click="activeTab = 'password'" 
                  :class="[
                    'tab-button',
                    activeTab === 'password' ? 'active' : ''
                  ]"
                >
                  修改密码
                </button>
              </nav>
            </div>
              <!-- 个人资料表单 -->
            <div v-if="activeTab === 'profile'" class="tab-content">
              <form @submit.prevent="updateProfile">
                <div class="form-group">
                  <label for="username" class="form-label">姓名</label>
                  <input
                    type="text"
                    id="username"
                    v-model="userProfile.username"
                    class="form-input disabled"
                    disabled
                  />
                  <p class="form-help-text">用户名注册后不可更改</p>
                </div>
                
                <div class="form-group">
                  <label for="email" class="form-label">邮箱</label>
                  <input
                    type="email"
                    id="email"
                    v-model="userProfile.email"
                    class="form-input"
                    required
                  />
                </div>
                
                <div class="form-group">
                  <label for="role" class="form-label">学院</label>
                  <input
                    type="text"
                    id="role"
                    :value="roleText"
                    class="form-input disabled"
                    disabled
                  />
                </div>
                
                <div class="form-group">
                  <label for="bio" class="form-label">简介</label>
                  <textarea
                    id="bio"
                    v-model="userProfile.bio"
                    rows="4"
                    class="form-textarea"
                    placeholder="介绍一下自己..."
                  ></textarea>
                </div>
                  <div class="form-group" v-if="updateMessage">
                  <div :class="[
                    'message-alert', 
                    updateSuccess ? 'success' : 'error'
                  ]">
                    {{ updateMessage }}
                  </div>
                </div>
                
                <div class="form-actions">
                  <button
                    type="submit"
                    class="submit-button"
                    :disabled="updating"
                  >
                    <svg v-if="updating" class="spinner-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    {{ updating ? '保存中...' : '保存' }}
                  </button>
                  <button type="button" class="cancel-button">取消</button>
                </div>
              </form>
            </div>
              <!-- 修改密码表单 -->
            <div v-else-if="activeTab === 'password'" class="tab-content">
              <form @submit.prevent="changePassword">
                <div class="form-group">
                  <label for="currentPassword" class="form-label">当前密码</label>
                  <input
                    type="password"
                    id="currentPassword"
                    v-model="passwordData.oldPassword"
                    class="form-input"
                    required
                  />
                </div>
                
                <div class="form-group">
                  <label for="newPassword" class="form-label">新密码</label>
                  <input
                    type="password"
                    id="newPassword"
                    v-model="passwordData.newPassword"
                    class="form-input"
                    required
                  />
                </div>
                
                <div class="form-group">
                  <label for="confirmPassword" class="form-label">确认新密码</label>
                  <input
                    type="password"
                    id="confirmPassword"
                    v-model="passwordData.confirmPassword"
                    class="form-input"
                    required
                  />
                  <p v-if="passwordMismatch" class="error-text">两次输入的密码不一致</p>
                </div>
                  <div class="form-group" v-if="passwordMessage">
                  <div :class="[
                    'message-alert', 
                    passwordSuccess ? 'success' : 'error'
                  ]">
                    {{ passwordMessage }}
                  </div>
                </div>
                
                <div class="form-actions">
                  <button
                    type="submit"
                    class="submit-button"
                    :disabled="updatingPassword || passwordMismatch"
                  >
                    <svg v-if="updatingPassword" class="spinner-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    {{ updatingPassword ? '更新中...' : '更新密码' }}
                  </button>
                  <button type="button" class="cancel-button">取消</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue';
import { useAuthStore } from '../../stores/auth';

// 获取Auth Store
const authStore = useAuthStore();

// 默认头像def
const defaultAvatar = '/assets/defaultProfile.jpg';

// 状态管理
const loading = ref(true);
const error = ref<string | null>(null);
const activeTab = ref('profile');
const updating = ref(false);
const updatingPassword = ref(false);
const updateMessage = ref('');
const updateSuccess = ref(false);
const passwordMessage = ref('');
const passwordSuccess = ref(false);

// 用户信息接口
interface UserProfileData {
  username: string;
  email: string;
  bio: string;
  avatar: string;
  role: string;
  createdAt: string;
}

// 用户个人信息
const userProfile = reactive<UserProfileData>({
  username: '',
  email: '',
  bio: '',
  avatar: '',
  role: '',
  createdAt: ''
});

// 密码修改数据
const passwordData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 计算属性
const roleText = computed(() => {
  switch(userProfile.role) {
    case 'teacher': return '教师';
    case 'assistant': return '助教';
    case 'student': return '学生';
    default: return '用户';
  }
});

const formattedDate = computed(() => {
  if (!userProfile.createdAt) return '未知';
  const date = new Date(userProfile.createdAt);
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric'
  });
});

const passwordMismatch = computed(() => {
  return passwordData.newPassword !== passwordData.confirmPassword &&
         passwordData.confirmPassword !== '';
});

// 方法
const fetchUserProfile = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    // 使用auth store获取用户信息
    const userData = await authStore.fetchUserInfo();
    
    // 更新本地数据，使用默认值处理可能缺失的字段
    userProfile.username = userData.username || '';
    userProfile.email = userData.email || '';
    userProfile.bio = ''; // 暂时使用空字符串，后续可从后端获取
    userProfile.avatar = ''; // 暂时使用空字符串，后续可从后端获取
    userProfile.role = userData.role || '';
    userProfile.createdAt = userData.createdAt || '';
  } catch (err: any) {
    console.error('获取用户信息失败:', err);
    error.value = err.message || '无法加载用户信息，请稍后再试';
  } finally {
    loading.value = false;
  }
};

const updateProfile = async () => {
  updating.value = true;
  updateMessage.value = '';
  
  try {
    // 只更新邮箱和用户名
    await authStore.updateProfile({
      email: userProfile.email,
      username: userProfile.username
    });
    
    // 更新成功
    updateSuccess.value = true;
    updateMessage.value = '个人资料已成功更新';
    
    // 3秒后清除消息
    setTimeout(() => {
      updateMessage.value = '';
    }, 3000);
  } catch (err: any) {
    console.error('更新个人资料失败:', err);
    updateSuccess.value = false;
    updateMessage.value = err.message || '更新个人资料失败，请稍后再试';
  } finally {
    updating.value = false;
  }
};

const changePassword = async () => {
  if (passwordMismatch.value) {
    passwordSuccess.value = false;
    passwordMessage.value = '两次输入的密码不一致';
    return;
  }
  
  updatingPassword.value = true;
  passwordMessage.value = '';
  
  try {
    // 调用auth store的修改密码方法
    await authStore.changePassword(passwordData.oldPassword, passwordData.newPassword);
    
    passwordSuccess.value = true;
    passwordMessage.value = '密码已成功更新';
    
    // 重置表单
    passwordData.oldPassword = '';
    passwordData.newPassword = '';
    passwordData.confirmPassword = '';
    
    // 3秒后清除消息
    setTimeout(() => {
      passwordMessage.value = '';
    }, 3000);
  } catch (err: any) {
    console.error('修改密码失败:', err);
    passwordSuccess.value = false;
    passwordMessage.value = err.message || '密码修改失败，请确认当前密码是否正确';
  } finally {
    updatingPassword.value = false;
  }
};

const handleFileChange = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (!target) return;
  
  // 立即重置文件输入以避免重复选择同一文件的问题
  target.value = '';
  
  try {
    updateSuccess.value = false;
    updateMessage.value = '头像上传功能暂未开放';
    
    // 3秒后清除消息
    setTimeout(() => {
      updateMessage.value = '';
    }, 3000);
  } catch (error) {
    console.error('处理文件选择时出错:', error);
    updateSuccess.value = false;
    updateMessage.value = '处理文件时出错，请稍后再试';
  }
};

const handleAvatarError = () => {
  userProfile.avatar = defaultAvatar;
};

// 生命周期钩子
onMounted(() => {
  fetchUserProfile();
});
</script>

<style scoped>
/* CSS变量定义 */
:root {
  --bg-primary: #ffffff;
  --bg-secondary: #f8f9fa;
  --bg-tertiary: #e9ecef;
  --text-primary: #333333;
  --text-secondary: #666666;
  --text-tertiary: #999999;
  --primary: #0055a2;
  --primary-dark: #003c72;
  --primary-light: #e1f5fe;
  --border-color: #e0e0e0;
  --transition-normal: 0.3s ease;
}

/* 全局样式 */
.profile-container {
  font-family: 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f0f2f5;
}

/* 顶部导航栏 */
.top-nav {
  background-color: #0055a2;
  color: white;
  padding: 0;
  display: flex;
  justify-content: center;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.nav-links {
  display: flex;
  max-width: 1200px;
  width: 100%;
  justify-content: flex-start;
  padding: 0 20px;
}

.nav-links a {
  color: white;
  padding: 15px 20px;
  text-decoration: none;
  font-weight: 400;
  font-size: 16px;
  position: relative;
  transition: background-color 0.3s;
}

.nav-links a:hover {
  background-color: rgba(255,255,255,0.1);
}

.dropdown-toggle {
  display: flex;
  align-items: center;
}

.arrow-down {
  font-size: 10px;
  margin-left: 5px;
}

/* 主体内容 */
.main-content {
  max-width: 1000px;
  margin: 30px auto;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0,0,0,0.05);
  padding: 30px;
}

.page-title {
  margin: 0 0 30px;
  padding-bottom: 15px;
  font-size: 22px;
  color: #0055a2;
  font-weight: 500;
  border-bottom: 1px solid #e0e0e0;
  text-align: left;
}

/* 加载和错误状态 */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 85, 162, 0.3);
  border-radius: 50%;
  border-top: 4px solid #0055a2;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  background-color: #ffebee;
  border-left: 4px solid #f44336;
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 4px;
}

.error-title {
  font-weight: bold;
  color: #d32f2f;
  margin-bottom: 5px;
}

/* 个人资料内容布局 */
.profile-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

@media (min-width: 768px) {
  .profile-content {
    flex-direction: row;
  }
}

/* 侧边栏 */
.profile-sidebar {
  width: 100%;
}

@media (min-width: 768px) {
  .profile-sidebar {
    width: 30%;
  }
}

.profile-card {
  background-color: var(--bg-primary);
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 1px solid var(--border-color);
  transition: background-color var(--transition-normal), border-color var(--transition-normal);
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 1rem;
}

.avatar-wrapper {
  width: 100px;
  height: 100px;
  overflow: hidden;
  border-radius: 50%;
  border: 4px solid #e1f5fe;
  margin-bottom: 0.5rem;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-input-container {
  text-align: center;
}

.file-input {
  display: block;
  width: 0.1px;
  height: 0.1px;
  opacity: 0;
  overflow: hidden;
  position: absolute;
  z-index: -1;
}

.file-input + label {
  display: inline-block;
  padding: 10px 20px;
  background-color: #0055a2;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.file-input + label:hover {
  background-color: #003c72;
}

.upload-label {
  display: inline-block;
  padding: 10px 20px;
  background-color: #0055a2;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 14px;
  margin: 10px 0;
}

.upload-label:hover {
  background-color: #003c72;
}

.user-name {
  font-size: 1.5rem;
  font-weight: 500;
  color: var(--text-primary);
  margin: 10px 0 5px;
}

.user-role {
  display: inline-block;
  background-color: var(--primary-light);
  color: var(--primary);
  padding: 4px 12px;
  border-radius: 50px;
  font-size: 0.9rem;
  margin-bottom: 15px;
}

.user-info {
  width: 100%;
  margin-top: 15px;
}

.info-item {
  background-color: var(--bg-secondary);
  border-radius: 6px;
  padding: 10px 15px;
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  transition: background-color var(--transition-normal);
}

.info-icon {
  width: 20px;
  height: 20px;
  color: var(--primary);
  margin-right: 10px;
}

.info-text {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

/* 主要内容区 */
.profile-main {
  width: 100%;
}

@media (min-width: 768px) {
  .profile-main {
    width: 70%;
  }
}

.form-container {
  background-color: var(--bg-primary);
  border-radius: 8px;
  border: 1px solid var(--border-color);
  overflow: hidden;
  transition: background-color var(--transition-normal), border-color var(--transition-normal);
}

.tabs-container {
  border-bottom: 1px solid var(--border-color);
}

.tabs-nav {
  display: flex;
}

.tab-button {
  border: none;
  background: none;
  padding: 15px 20px;
  font-size: 16px;
  color: var(--text-secondary);
  font-weight: 500;
  cursor: pointer;
  position: relative;
  transition: color 0.3s;
}

.tab-button:hover {
  color: var(--primary);
}

.tab-button.active {
  color: var(--primary);
}

.tab-button.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: var(--primary);
}

.tab-content {
  padding: 25px;
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 表单样式 */
.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: var(--text-primary);
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 16px;
  color: var(--text-primary);
  background-color: var(--bg-primary);
  transition: border-color 0.3s, box-shadow 0.3s, background-color var(--transition-normal);
}

.form-input:focus,
.form-textarea:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--primary-light);
  outline: none;
}

.form-input.disabled {
  background-color: var(--bg-secondary);
  cursor: not-allowed;
}

.form-textarea {
  min-height: 100px;
  resize: vertical;
}

.form-help-text {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 5px;
}

.error-text {
  color: #d32f2f;
  font-size: 14px;
  margin-top: 5px;
}

.message-alert {
  padding: 12px 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.message-alert.success {
  background-color: #e8f5e9;
  color: #2e7d32;
  border-left: 4px solid #4caf50;
}

.message-alert.error {
  background-color: #ffebee;
  color: #c62828;
  border-left: 4px solid #f44336;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.submit-button,
.cancel-button {
  padding: 10px 20px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: background 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-button {
  background-color: var(--primary);
  color: white;
}

.submit-button:hover {
  background-color: var(--primary-dark);
}

.submit-button:disabled {
  background-color: var(--text-tertiary);
  cursor: not-allowed;
}

.cancel-button {
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

.cancel-button:hover {
  background-color: var(--bg-tertiary);
}

.spinner-icon {
  animation: spin 1s linear infinite;
  width: 18px;
  height: 18px;
  margin-right: 8px;
}
</style>