<template>  <div class="discussion-layout">
    <!-- 主内容区 -->
    <div class="discussion-list-page p-4 md:p-6 min-h-screen flex-1">
      <div class="discussion-container">
        <!-- 顶部工具栏 -->
        <div class="toolbar">
          <div class="toolbar-left">
            <h1 class="title">
              <i class="fa fa-comments fa-lg"></i>
              讨论区 {{ courseName ? '- ' + courseName : '' }}
              <span v-if="unreadNotificationsCount > 0" class="badge">{{ unreadNotificationsCount }}</span>
            </h1>
          </div>
          <div class="toolbar-right">
            <button 
              @click="showCreateInput = true"
              class="create-discussion-btn"
            >
              <i class="fa fa-plus"></i>
              发起讨论
            </button>
            
            <div class="action-buttons">
              <button @click="refreshDiscussions" class="action-btn" :disabled="loading">
                <i class="fa fa-refresh" :class="{ 'fa-spin': loading }"></i>
                刷新
              </button>
            </div>
          </div>
        </div>
        
        <!-- 搜索栏 -->
        <div class="search-container">
          <div class="search-inner">
            <input 
              type="text" 
              v-model="searchQuery"
              placeholder="搜索帖子标题或内容..."
              class="search-input"
              @keyup.enter="performSearch"
            />
            <button
              @click="performSearch"
              class="search-btn"
            >
              <i class="fa fa-search"></i>
              搜索
            </button>
          </div>
        </div>      <!-- 筛选和排序工具栏 -->
        <div class="filter-toolbar">
          <div class="filter-section">
            <span class="filter-label">筛选：</span>
            <div class="filter-options">
              <button 
                v-for="option in filterOptions" 
                :key="option.value"
                @click="currentFilter = option.value"
                :class="['filter-option', { active: currentFilter === option.value }]"
              >
                {{ option.label }}
              </button>
            </div>
          </div>
          
          <div class="sort-section">
            <span class="sort-label">排序：</span>
            <select 
              v-model="currentSort" 
              class="sort-select"
            >
              <option 
                v-for="option in sortOptions" 
                :key="option.value" 
                :value="option.value"
              >
                {{ option.label }}
              </option>
            </select>
          </div>
        </div>      <!-- 加载状态 -->
      <div v-if="loading" class="skeleton-container">
        <div v-for="i in 3" :key="i" class="skeleton-card">
          <div class="skeleton-header">
            <div class="skeleton-meta">
              <div class="skeleton-tag"></div>
              <div class="skeleton-tag"></div>
              <div class="skeleton-tag"></div>
            </div>
            <div class="skeleton-date"></div>
          </div>
          <div class="skeleton-title"></div>
          <div class="skeleton-content"></div>
          <div class="skeleton-content short"></div>
          <div class="skeleton-footer">
            <div class="skeleton-tag large"></div>
            <div class="skeleton-actions">
              <div class="skeleton-action"></div>
              <div class="skeleton-action"></div>
            </div>
          </div>
        </div>
      </div>

        <div v-else-if="error" class="alert-error">
          <i class="fa fa-exclamation-circle fa-lg"></i>
          <div>
            <p class="error-title">加载失败</p>
            <p>{{ error }}</p>
            <button @click="refreshDiscussions" class="retry-btn">
              <i class="fa fa-refresh"></i>
              重试
            </button>
          </div>
        </div>

        <div v-else-if="filteredThreads.length === 0" class="empty-state">
          <i class="fa fa-comments fa-4x"></i>
          <p class="empty-title">{{ searchQuery ? '没有找到匹配的帖子' : '暂无讨论' }}</p>
          <p class="empty-description">{{ getEmptyStateMessage() }}</p>
          <button v-if="searchQuery" @click="searchQuery = ''" class="clear-search-btn">
            <i class="fa fa-times"></i>
            清除搜索
          </button>
          <button v-else @click="showCreateInput = true" class="start-btn">
            <i class="fa fa-plus-circle"></i>
            发起第一个讨论
          </button>
        </div>        <div v-else>          <div
            v-for="thread in filteredThreads"
            :key="thread.id"
            class="discussion-card"
            :class="{ 'pinned': thread.isPinned, 'closed': thread.isClosed }"
          >
            <!-- 置顶指示器 -->
            <div v-if="thread.isPinned" class="pin-indicator">
              <i class="fa fa-thumbtack"></i>
            </div>
            
            <div class="discussion-header">
              <div class="discussion-meta">
                <span v-if="thread.isPinned" class="tag pin-tag">
                  <i class="fa fa-thumbtack"></i>
                  置顶
                </span>
                <span v-if="thread.isClosed" class="tag closed-tag">
                  <i class="fa fa-lock"></i>
                  已关闭
                </span>
                <span class="tag like-tag" :class="{'highlight': isLikedByUser(thread.id)}">
                  <i class="fa fa-heart"></i>
                  {{ thread.likes || 0 }} 点赞
                </span>
                <span class="tag reply-tag">
                  <i class="fa fa-comments"></i>
                  {{ thread.replyCount || 0 }} 回复
                </span>
                <span class="tag heat-tag" :class="{'hot': isHot(thread)}">
                  <i class="fa fa-fire"></i>
                  热度: {{ calculateHeat(thread) }}
                </span>
                <span class="tag view-tag">
                  <i class="fa fa-eye"></i>
                  {{ thread.viewCount || 0 }} 浏览
                </span>
              </div>
              <div class="thread-meta-info">
                <div class="author-info">
                  <span class="author-avatar">{{ getAvatarInitial(thread.author) }}</span>
                  <span class="author-name">{{ thread.author }}</span>
                </div>
                <div class="date-info">
                  <i class="fa fa-clock-o"></i> {{ formatDate(thread.createdAt) }}
                </div>
              </div>
            </div>
            
            <!-- 主要内容区域，点击可以导航 -->
            <div 
              class="thread-content-area"
              @click="navigateToThread(thread.id)"
            >
              <h2 class="discussion-title">{{ thread.title }}</h2>
              <div class="discussion-content-wrapper">
                <p class="discussion-content line-clamp-2" v-html="formatContent(thread.content)"></p>
              </div>
              <div class="read-more-link">查看详情 <i class="fa fa-angle-right"></i></div>
            </div>

            <!-- 操作按钮区域 -->
            <div class="discussion-footer">
              <div class="discussion-tags">
                <span class="course-tag">
                  <i class="fa fa-book"></i>
                  {{ thread.courseName || '课程讨论' }}
                </span>
              </div>
              
              <div class="discussion-actions">
                <!-- 置顶按钮（仅助教和老师可见） -->
                <button 
                  v-if="userRole === 'assistant' || userRole === 'teacher'"
                  @click.stop="togglePinThread(thread)"
                  class="action-btn"
                  :class="{'active': thread.isPinned}"
                >
                  <i class="fa fa-thumbtack"></i>
                  <span class="action-text">{{ thread.isPinned ? '取消置顶' : '置顶' }}</span>
                </button>

                <!-- 关闭/开启讨论帖按钮（仅助教和老师可见） -->
                <button 
                  v-if="userRole === 'assistant' || userRole === 'teacher'"
                  @click.stop="toggleCloseThread(thread)"
                  class="action-btn"
                  :class="{'active': thread.isClosed}"
                >
                  <i class="fa" :class="thread.isClosed ? 'fa-unlock' : 'fa-lock'"></i>
                  <span class="action-text">{{ thread.isClosed ? '开启讨论' : '关闭讨论' }}</span>
                </button>

                <!-- 点赞按钮 -->
                <button 
                  @click.stop="likeThread(thread.id)"
                  class="action-btn like-btn"
                  :class="{'liked': isLikedByUser(thread.id)}"
                  :disabled="thread.isClosed"
                >
                  <i class="fa fa-heart"></i>
                  <span class="action-text">点赞</span>
                  <span v-if="isLikedByUser(thread.id)" class="like-pulse"></span>
                </button>

                <!-- 删除按钮（仅作者、助教和老师可见） -->
                <button 
                  v-if="canDeleteThread(thread)"
                  @click.stop="confirmDeleteThread(thread.id)"
                  class="action-btn delete-btn"
                >
                  <i class="fa fa-trash"></i>
                  删除
                </button>
              </div>
            </div>
          </div>
        </div>          <!-- 创建讨论帖表单（弹窗模式） -->
        <transition name="modal-fade">
          <div v-if="showCreateInput" class="modal-overlay" @click.self="showCreateInput = false">
            <div class="create-modal-container">
              <div class="create-modal-content">
                
                <div class="create-modal-header">
                  <div class="header-content">
                    <div class="header-icon-wrapper">
                      <i class="fa fa-plus-circle"></i>
                    </div>
                    <div class="header-text">
                      <h2>创建新讨论</h2>
                      <p class="header-subtitle">分享你的想法，参与精彩讨论</p>
                    </div>
                  </div>
                  <button class="close-modal-btn" @click="showCreateInput = false">
                    <i class="fa fa-times"></i>
                  </button>
                </div>
                
                <div class="create-modal-body">
                  <!-- 进度指示器 -->
                  <div class="progress-indicator">
                    <div class="progress-step" :class="{'active': newThread.title.length > 0, 'completed': titleFocused || newThread.title.length > 3}">
                      <div class="step-circle">
                        <i class="fa fa-edit"></i>
                      </div>
                      <span class="step-label">标题</span>
                    </div>
                    <div class="progress-line" :class="{'active': newThread.title.length > 3}"></div>
                    <div class="progress-step" :class="{'active': newThread.content.length > 0, 'completed': contentFocused || getContentLength() > 10}">
                      <div class="step-circle">
                        <i class="fa fa-file-text-o"></i>
                      </div>
                      <span class="step-label">内容</span>
                    </div>
                    <div class="progress-line" :class="{'active': getContentLength() > 10}"></div>
                    <div class="progress-step" :class="{'active': isFormValid, 'completed': isFormValid}">
                      <div class="step-circle">
                        <i class="fa fa-check"></i>
                      </div>
                      <span class="step-label">完成</span>
                    </div>
                  </div>

                  <!-- 进度文本显示 -->
                  <div class="progress-text">
                    <span class="progress-percentage">{{ formProgress }}% 完成</span>
                    <span class="progress-description">{{ progressText }}</span>
                  </div>

                  <!-- 标题输入组 -->
                  <div class="form-group enhanced-form-group">
                    <div class="input-group-wrapper">
                      <div class="form-label-container">
                        <label for="thread-title" class="enhanced-label">
                          <i class="fa fa-edit label-icon"></i>
                          讨论标题
                          <span class="required-asterisk">*</span>
                        </label>
                        <div class="char-count enhanced-char-count" 
                             :class="{'warning': newThread.title.length > 80, 'error': newThread.title.length > 100, 'success': newThread.title.length >= 3 && newThread.title.length <= 80}">
                          <span class="count-text">{{ newThread.title.length }}</span>
                          <span class="count-separator">/</span>
                          <span class="count-max">100</span>
                        </div>
                      </div>
                      <div class="input-wrapper" :class="{'focused': titleFocused, 'error': titleError, 'success': !titleError && newThread.title.length >= 3}">
                        <input 
                          id="thread-title" 
                          v-model="newThread.title" 
                          type="text" 
                          class="form-input enhanced-input" 
                          placeholder="输入一个吸引人的标题，让更多人参与讨论..."
                          :class="{'error-input': titleError, 'focus-input': titleFocused}"
                          @focus="titleFocused = true"
                          @blur="titleFocused = false; validateTitle()"
                          @keyup.enter="focusContent()"
                        />
                        <div class="input-border-effect"></div>
                        <div class="input-success-icon" v-if="!titleError && newThread.title.length >= 3">
                          <i class="fa fa-check-circle"></i>
                        </div>
                      </div>
                    </div>
                    <div class="form-hint enhanced-hint">
                      <transition name="hint-fade" mode="out-in">
                        <span v-if="titleError" key="error" class="form-error enhanced-error">
                          <i class="fa fa-exclamation-triangle"></i> {{ titleError }}
                        </span>
                        <span v-else-if="newThread.title.length >= 3" key="success" class="form-success">
                          <i class="fa fa-check-circle"></i> 很棒的标题！
                        </span>
                        <span v-else key="tip" class="form-tip enhanced-tip">
                          <i class="fa fa-lightbulb-o"></i> 好的标题能够吸引更多人参与讨论
                        </span>
                      </transition>
                    </div>
                  </div>                  
                  <!-- 内容编辑器组 -->
                  <div class="form-group enhanced-form-group">
                    <div class="input-group-wrapper">
                      <div class="form-label-container">
                        <label for="thread-content" class="enhanced-label">
                          <i class="fa fa-file-text-o label-icon"></i>
                          讨论内容
                          <span class="required-asterisk">*</span>
                        </label>
                        <div class="char-count enhanced-char-count" 
                             :class="{'warning': getContentLength() > 2000, 'error': getContentLength() > 3000, 'success': getContentLength() >= 10 && getContentLength() <= 2000}">
                          <span class="count-text">{{ getContentLength() }}</span>
                          <span class="count-separator">/</span>
                          <span class="count-max">3000</span>
                        </div>
                      </div>
                      <div class="content-editor-wrapper" 
                           :class="{'focused': contentFocused, 'error': contentError, 'success': !contentError && getContentLength() >= 10}"
                           ref="contentEditorContainer">
                        <WriteBoard 
                          v-model="newThread.content" 
                          @focus="contentFocused = true"
                          @blur="contentFocused = false; validateContent()"
                          class="enhanced-editor"
                        />
                        <div class="editor-border-effect"></div>
                        <div class="editor-success-icon" v-if="!contentError && getContentLength() >= 10">
                          <i class="fa fa-check-circle"></i>
                        </div>
                      </div>
                    </div>
                    <div class="form-hint enhanced-hint">
                      <transition name="hint-fade" mode="out-in">
                        <span v-if="contentError" key="error" class="form-error enhanced-error">
                          <i class="fa fa-exclamation-triangle"></i> {{ contentError }}
                        </span>
                        <span v-else-if="getContentLength() >= 10" key="success" class="form-success">
                          <i class="fa fa-check-circle"></i> 内容很充实！
                        </span>
                        <span v-else key="tip" class="form-tip enhanced-tip">
                          <i class="fa fa-info-circle"></i> 详细的内容有助于他人理解你的问题或观点
                        </span>
                      </transition>
                    </div>
                  </div>
                  
                  <!-- 全局错误提示 -->
                  <transition name="error-slide">
                    <div v-if="createThreadError" class="form-error-global enhanced-error-global">
                      <div class="error-icon-wrapper">
                        <i class="fa fa-exclamation-circle"></i>
                      </div>
                      <div class="error-content">
                        <div class="error-title">发布失败</div>
                        <div class="error-message">{{ createThreadError }}</div>
                      </div>
                    </div>
                  </transition>
                </div>
                
                <!-- 美化的底部操作区 -->
                <div class="create-modal-footer enhanced-footer">
                  <div class="footer-info">
                    <i class="fa fa-info-circle"></i>
                    <span>{{ progressText }}</span>
                  </div>
                  <div class="footer-buttons">
                    <button @click="showCreateInput = false" class="btn btn-secondary enhanced-btn">
                      <i class="fa fa-times"></i> 
                      <span>取消</span>
                    </button>
                    <button 
                      @click="validateAndCreateThread" 
                      :disabled="creatingThread || !isFormValid" 
                      class="btn btn-primary enhanced-btn"
                      :class="{'loading': creatingThread, 'pulse': isFormValid && !creatingThread}"
                    >
                      <i class="fa" :class="creatingThread ? 'fa-spinner fa-spin' : 'fa-paper-plane'"></i>                      <span>{{ creatingThread ? '发布中...' : '发布讨论' }}</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </transition>
        
        <!-- 用户讨论记录（内联显示） -->
        <div v-if="showUserRecordPane" class="bg-white rounded-lg shadow-lg p-6 mb-8 animate-fadeIn">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-bold">我的讨论记录</h3>
            <button @click="showUserRecordPane = false" class="text-gray-500 hover:text-gray-700">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
            </button>
          </div>
          <div v-if="loadingUserRecords" class="text-center py-6">
            <p>加载中...</p>
          </div>
          <div v-else-if="userRecordsError" class="text-red-500">
            {{ userRecordsError }}
          </div>
          <div v-else>
            <div v-if="userThreads.length === 0 && userPosts.length === 0" class="text-center text-gray-500 py-4">
              您还没有参与任何讨论。
            </div>
            <div v-else>
              <!-- 用户发起的讨论 -->
              <div v-if="userThreads.length > 0" class="mb-4">
                <h4 class="font-semibold mb-2">我发起的讨论</h4>
                <ul class="space-y-2">
                  <li v-for="thread in userThreads" :key="thread.id" class="text-blue-600 cursor-pointer hover:underline" @click="navigateToThread(thread.id)">
                    {{ thread.title }}
                  </li>
                </ul>
              </div>
              <!-- 用户的回复 -->
              <div v-if="userPosts.length > 0">
                <h4 class="font-semibold mb-2">我的回复</h4>
                <ul class="space-y-2">                  <li v-for="post in userPosts" :key="post.id" class="text-blue-600 cursor-pointer hover:underline" @click="navigateToThread(post.threadId)">
                    回复于: 讨论帖 #{{ post.threadId.slice(0, 8) }}
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 通知列表（内联显示） -->
        <div v-if="showNotificationPane" class="bg-white rounded-lg shadow-lg p-6 mb-8 animate-fadeIn">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-bold">我的通知</h3>
            <button @click="showNotificationPane = false" class="text-gray-500 hover:text-gray-700">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
            </button>
          </div>
          <div v-if="loadingNotifications" class="text-center py-6">
            <p>加载中...</p>
          </div>
          <div v-else-if="notificationsError" class="text-red-500">
            {{ notificationsError }}
          </div>
          <div v-else>
            <div v-if="notifications.length === 0" class="text-center text-gray-500 py-4">
              暂无通知。
            </div>
            <ul v-else class="space-y-3">
              <li 
                v-for="notification in notifications" 
                :key="notification.id"
                class="p-3 rounded-md border cursor-pointer transition-colors"
                :class="notification.isRead ? 'border-gray-200 bg-gray-50' : 'border-blue-200 bg-blue-50'"
                @click="navigateToThread(notification.threadId); markNotificationAsRead(notification.id);"
              >
                <div class="flex justify-between items-start">
                  <span class="font-medium">
                    {{ notification.type === 'reply' ? '收到新回复' : '有人点赞了您的帖子' }}
                  </span>
                  <span class="text-xs text-gray-500">{{ formatDate(notification.createdAt) }}</span>
                </div>
                <p class="text-sm text-gray-600 mt-1">{{ notification.fromUser }} {{ notification.type === 'reply' ? '回复了您的帖子' : '点赞了您的帖子' }}：{{ notification.threadTitle }}</p>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
    <!-- 右侧操作导航栏 -->
    <nav class="discussion-action-bar">      
      <div class="action-nav-item" @click="fetchThreads">
        <svg xmlns="http://www.w3.org/2000/svg" class="action-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m-15.357-2a8.001 8.001 0 0015.357 2M9 15h4.581" />
        </svg>
        <span class="action-label">刷新</span>
      </div>      <div class="action-nav-item create-thread-btn" @click="showCreateInput = true">
        <svg xmlns="http://www.w3.org/2000/svg" class="action-icon" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
        </svg>
        <span class="action-label">创建</span>
      </div>
      <div class="action-nav-item" @click="showUserRecordPane = true">
        <svg xmlns="http://www.w3.org/2000/svg" class="action-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span class="action-label">我的记录</span>
      </div>
      <div class="action-nav-item" @click="showNotificationPane = true">
        <svg xmlns="http://www.w3.org/2000/svg" class="action-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
        </svg>
        <span class="action-label">通知</span>
        <span v-if="unreadNotificationsCount > 0" class="ml-1 px-2 py-0.5 bg-red-500 text-white text-xs rounded-full">{{ unreadNotificationsCount }}</span>
      </div>
    </nav>
    
    <!-- 状态提示条 -->
    <div v-if="showStatus" class="status-toast" :class="statusType">
      <i v-if="statusType === 'success'" class="fa fa-check-circle"></i>
      <i v-if="statusType === 'error'" class="fa fa-exclamation-circle"></i>
      <i v-if="statusType === 'warning'" class="fa fa-exclamation-triangle"></i>
      <i v-if="statusType === 'info'" class="fa fa-info-circle"></i>
      <span>{{ statusMessage }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, reactive, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import WriteBoard from '../../components/littlecomponents/WriteBoard.vue';
import discussionApi, { Discussion } from '../../api/discussion';

// 定义讨论帖类型（旧接口格式）
interface Thread {
  id: string;
  title: string;
  content: string;
  authorId: string;
  author: string;
  createdAt: string;
  courseId: string;
  courseName?: string;
  isPinned?: boolean;
  isClosed?: boolean;
  likes?: number;
  replyCount?: number;
  viewCount?: number;
  heat?: number; // 热度属性
}

// 定义回复类型（旧接口格式）
interface Post {
  id: string;
  threadId: string;
  content: string;
  authorId: string;
  author: string;
  createdAt: string;
  parentId?: string;
}

// 创建讨论帖的数据结构
interface CreateThreadData {
  title: string;
  content: string;
  courseId: string;
}

// 创建兼容层，用新接口适配旧代码
const legacyDiscussionApi = {
  // 讨论区列表页
  getAllThreads: async () => {
    try {
      const response = await discussionApi.getDiscussionList();
      return {
        data: (response.data.content || []).map((item: Discussion): Thread => ({
          id: String(item.id),
          title: item.title,
          content: item.content,
          authorId: String(item.authorId),
          author: item.authorName,
          createdAt: item.createdAt,
          courseId: String(item.courseId),
          courseName: item.courseName,
          isPinned: item.isPinned,
          isClosed: item.isClosed, // 添加isClosed状态
          likes: item.likeCount,
          replyCount: item.replyCount,
          viewCount: item.viewCount // 添加viewCount
        }))
      };
    } catch (error) {
      console.error('获取所有讨论帖失败', error);
      return { data: [] };
    }
  },
  
  getThreadsByCourse: async (courseId: string) => {
    try {
      const response = await discussionApi.getDiscussionList({ 
        courseId: Number(courseId),
        sortBy: 'createdAt',
        sortOrder: 'desc' 
      });
      return {
        data: (response.data.content || []).map((item: Discussion): Thread => ({
          id: String(item.id),
          title: item.title,
          content: item.content,
          authorId: String(item.authorId),
          author: item.authorName,
          createdAt: item.createdAt,
          courseId: String(item.courseId),
          courseName: item.courseName,
          isPinned: item.isPinned,
          isClosed: item.isClosed,
          likes: item.likeCount,
          replyCount: item.replyCount,
          viewCount: item.viewCount
        }))
      };
    } catch (error) {
      console.error(`获取课程(${courseId})讨论帖失败`, error);
      return { data: [] };
    }
  },
  
  createThread: async (data: CreateThreadData) => {
    try {
      const response = await discussionApi.createDiscussion({
        title: data.title,
        content: data.content,
        courseId: Number(data.courseId)
      });
      return {
        data: {
          id: String(response.data.id),
          title: response.data.title,
          content: response.data.content,
          authorId: String(response.data.authorId),
          author: response.data.authorName,
          createdAt: response.data.createdAt,
          courseId: String(response.data.courseId),
          courseName: response.data.courseName,
          isPinned: response.data.isPinned,
          isClosed: response.data.isClosed,
          likes: response.data.likeCount,
          replyCount: response.data.replyCount,
          viewCount: response.data.viewCount
        }
      };
    } catch (error) {
      console.error('创建讨论帖失败', error);
      throw error;
    }
  },
  
  deleteThread: async (threadId: string) => {
    try {
      await discussionApi.deleteDiscussion(Number(threadId));
      return { data: { success: true } };
    } catch (error) {
      console.error(`删除讨论帖(${threadId})失败`, error);
      throw error;
    }
  },
  
  togglePinThread: async (threadId: string, isPinned: boolean) => {
    try {
      const response = await discussionApi.pinDiscussion(Number(threadId), isPinned);
      return {
        data: {
          id: String(response.data.id),
          isPinned: response.data.isPinned
        }
      };
    } catch (error) {
      console.error(`${isPinned ? '置顶' : '取消置顶'}讨论帖(${threadId})失败`, error);
      throw error;
    }
  },
  
  likeThread: async (threadId: string) => {
    // 假设新API中没有直接的点赞方法，这里模拟实现，在实际项目中应根据新API的实际情况来实现
    console.log(`点赞讨论帖: ${threadId}`);
    return { data: { success: true } };
  },
  
  searchThreads: async (keyword: string) => {
    try {
      const response = await discussionApi.getDiscussionList({ keyword });
      // 将新接口的响应转换为旧接口格式
      return {
        data: (response.data.content || []).map((item: Discussion): Thread => ({
          id: String(item.id),
          title: item.title,
          content: item.content,
          authorId: String(item.authorId),
          author: item.authorName,
          createdAt: item.createdAt,
          courseId: String(item.courseId),
          courseName: item.courseName,
          isPinned: item.isPinned,
          isClosed: item.isClosed,
          likes: item.likeCount,
          replyCount: item.replyCount,
          viewCount: item.viewCount
        }))
      };
    } catch (error) {
      console.error(`搜索讨论帖(${keyword})失败`, error);
      return { data: [] };
    }
  },
  
  // 获取用户讨论记录
  getUserThreads: async () => {
    try {
      const response = await discussionApi.getMyDiscussions();
      return {
        data: (response.data.content || []).map((item: Discussion): Thread => ({
          id: String(item.id),
          title: item.title,
          content: item.content,
          authorId: String(item.authorId),
          author: item.authorName,
          createdAt: item.createdAt,
          courseId: String(item.courseId),
          courseName: item.courseName,
          isPinned: item.isPinned,
          isClosed: item.isClosed,
          likes: item.likeCount,
          replyCount: item.replyCount,
          viewCount: item.viewCount
        }))
      };
    } catch (error) {
      console.error('获取我的讨论帖失败', error);
      return { data: [] };
    }
  },
  
  // 通知相关API，临时模拟
  markNotificationAsRead: async (id: string) => {
    console.log(`标记通知已读: ${id}`);
    return { data: { success: true } };
  }
};

const route = useRoute();
const router = useRouter();

// 用户角色信息，用于权限控制
const userRole = ref<'student' | 'assistant' | 'teacher'>('student'); // 默认为学生
const currentUserId = ref<string>('user123'); // 模拟当前用户ID，实际项目中应从认证状态获取

const courseId = ref<string | null>(null);
const courseName = ref<string>('');
const threads = ref<Thread[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);
const searchQuery = ref('');

// 模态窗口管理
const showUserRecordModal = ref(false);
const showNotificationsModal = ref(false);
const showUserRecordPane = ref(false);
const showNotificationPane = ref(false);

// 讨论帖创建相关
const creatingThread = ref(false);
const createThreadError = ref<string | null>(null);
const titleError = ref<string | null>(null);
const contentError = ref<string | null>(null);
const titleFocused = ref(false);
const contentFocused = ref(false);
const contentEditorContainer = ref<HTMLElement | null>(null);
const newThread = reactive<CreateThreadData>({
  title: '',
  content: '',
  courseId: '',
});

// 控制创建输入区显示
const showCreateInput = ref(false);

// 监听弹窗状态，当弹窗关闭时重置表单
watch(showCreateInput, (newValue) => {
  if (!newValue) {
    // 重置表单数据和错误信息
    newThread.title = '';
    newThread.content = '';
    titleError.value = null;
    contentError.value = null;
    createThreadError.value = null;
    titleFocused.value = false;
    contentFocused.value = false;
  }
});

// 表单字段验证函数
const validateTitle = () => {
  if (!newThread.title.trim()) {
    titleError.value = '请输入讨论标题';
    return false;
  } else if (newThread.title.length < 3) {
    titleError.value = '标题至少需要3个字符';
    return false;
  } else if (newThread.title.length > 100) {
    titleError.value = '标题不能超过100个字符';
    return false;
  } else {
    titleError.value = null;
    return true;
  }
};

const validateContent = () => {
  if (!newThread.content.trim()) {
    contentError.value = '请输入讨论内容';
    return false;
  } else if (stripHtml(newThread.content).length < 10) {
    contentError.value = '内容太短，请至少输入10个字符';
    return false;
  } else if (stripHtml(newThread.content).length > 3000) {
    contentError.value = '内容过长，请不要超过3000个字符';
    return false;
  } else {
    contentError.value = null;
    return true;
  }
};

// 获取纯文本内容长度
const getContentLength = () => {
  return stripHtml(newThread.content).length;
};

// 移除HTML标签，获取纯文本
const stripHtml = (html: string) => {
  const tmp = document.createElement('div');
  tmp.innerHTML = html;
  return tmp.textContent || tmp.innerText || '';
};

// 计算表单完成进度（用于进度条）
const formProgress = computed(() => {
  let progress = 0;
  
  // 标题完成度（40%权重）
  if (newThread.title.length >= 3) {
    progress += 40;
  } else if (newThread.title.length > 0) {
    progress += 20;
  }
  
  // 内容完成度（50%权重）
  const contentLength = getContentLength();
  if (contentLength >= 10) {
    progress += 50;
  } else if (contentLength > 0) {
    progress += 25;
  }
  
  // 验证完成度（10%权重）
  if (!titleError.value && !contentError.value && newThread.title.length >= 3 && contentLength >= 10) {
    progress += 10;
  }
  
  return Math.min(progress, 100);
});

// 计算进度状态文本
const progressText = computed(() => {
  const progress = formProgress.value;
  if (progress === 0) return '开始填写表单';
  if (progress < 40) return '请继续完善标题';
  if (progress < 90) return '请添加讨论内容';
  if (progress < 100) return '检查表单信息';
  return '准备完成';
});

// 检查表单是否完全有效
const isFormValid = computed(() => {
  return !titleError.value && 
         !contentError.value && 
         newThread.title.length >= 3 && 
         getContentLength() >= 10;
});

// 焦点到内容编辑器
const focusContent = () => {
  if (contentEditorContainer.value) {
    const editorElement = contentEditorContainer.value.querySelector('.q-editor') as HTMLElement;
    if (editorElement) {
      // 尝试聚焦到编辑区域
      contentFocused.value = true;
      setTimeout(() => {
        try {
          // 直接使用q-editor的API聚焦可能更可靠
          const editableDiv = editorElement.querySelector('[contenteditable=true]');
          if (editableDiv instanceof HTMLElement) {
            editableDiv.focus();
          } else {
            // 如果找不到可编辑元素，尝试直接滚动到视图
            editorElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
          }
        } catch (e) {
          console.error('聚焦到编辑器失败', e);
        }
      }, 100);
    }
  }
};

// 讨论记录相关
const userThreads = ref<Thread[]>([]);
const userPosts = ref<Post[]>([]);
const loadingUserRecords = ref(false);
const userRecordsError = ref<string | null>(null);

// 通知相关
const notifications = ref<Notification[]>([]);
const loadingNotifications = ref(false);
const notificationsError = ref<string | null>(null);
const unreadNotificationsCount = ref(0);

// 存储用户点赞状态
const userLikes = ref<Set<string>>(new Set());

// 模拟通知数据类型
interface Notification {
  id: string;
  type: 'reply' | 'like';
  threadId: string;
  threadTitle: string;
  createdAt: string;
  isRead: boolean;
  fromUser: string;
}

// 讨论排序和过滤选项
const sortOptions = [
  { value: 'createdAt', label: '最新发布' },
  { value: 'replyCount', label: '回复最多' },
  { value: 'likes', label: '点赞最多' },
  { value: 'viewCount', label: '浏览最多' },
  { value: 'heat', label: '热度排序' }
];

const filterOptions = [
  { value: 'all', label: '全部' },
  { value: 'pinned', label: '置顶' },
  { value: 'open', label: '进行中' },
  { value: 'closed', label: '已关闭' }
];

const currentSort = ref<'createdAt' | 'replyCount' | 'likes' | 'viewCount' | 'heat'>('createdAt');
const currentFilter = ref('all');

// 过滤和排序函数
const filteredThreads = computed(() => {
  // 应用搜索过滤
  let filtered = searchQuery.value
    ? threads.value.filter(t => 
        t.title.toLowerCase().includes(searchQuery.value.toLowerCase()) || 
        t.content.toLowerCase().includes(searchQuery.value.toLowerCase()))
    : threads.value;
  
  // 应用状态过滤
  if (currentFilter.value === 'pinned') {
    filtered = filtered.filter(t => t.isPinned);
  } else if (currentFilter.value === 'closed') {
    filtered = filtered.filter(t => t.isClosed);
  } else if (currentFilter.value === 'open') {
    filtered = filtered.filter(t => !t.isClosed);
  }
  
  // 计算热度值（如果尚未计算）
  filtered.forEach(t => {
    if (!t.heat) t.heat = (t.likes || 0) - (t.replyCount || 0);
  });
  
  // 应用排序
  return [...filtered].sort((a, b) => {
    // 置顶帖优先显示
    if (a.isPinned && !b.isPinned) return -1;
    if (!a.isPinned && b.isPinned) return 1;
    
    // 应用选定排序
    if (currentSort.value === 'createdAt') {
      return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
    } else if (currentSort.value === 'heat') {
      return (b.heat || 0) - (a.heat || 0);
    } else if (currentSort.value === 'likes') {
      return (b.likes || 0) - (a.likes || 0);
    } else if (currentSort.value === 'replyCount') {
      return (b.replyCount || 0) - (a.replyCount || 0);
    } else {
      return (b.viewCount || 0) - (a.viewCount || 0);
    }
  });
});

// 状态提示相关
const statusMessage = ref<string>('');
const statusType = ref<'success' | 'error' | 'warning' | 'info'>('info');
const showStatus = ref<boolean>(false);

// 显示状态提示
const showStatusMessage = (message: string, type: 'success' | 'error' | 'warning' | 'info' = 'info') => {
  statusMessage.value = message;
  statusType.value = type;
  showStatus.value = true;
  
  // 自动隐藏
  setTimeout(() => {
    hideStatusMessage();
  }, 3000);
};

// 隐藏状态提示
const hideStatusMessage = () => {
  showStatus.value = false;
};

// 刷新讨论列表
const refreshDiscussions = async () => {
  if (loading.value) return; // 防止重复加载
  
  loading.value = true;
  try {
    if (courseId.value) {
      const response = await legacyDiscussionApi.getThreadsByCourse(courseId.value);
      threads.value = response.data;
      
      // 获取课程名称（如果API返回的话）
      if (threads.value.length > 0 && threads.value[0].courseName) {
        courseName.value = threads.value[0].courseName;
      }
    } else {
      const response = await legacyDiscussionApi.getAllThreads();
      threads.value = response.data;
    }
    error.value = null;
    showStatusMessage('讨论数据刷新成功', 'success');
  } catch (err: any) {
    console.error('获取讨论帖失败:', err);
    error.value = '获取讨论帖失败，请重试';
    showStatusMessage('获取讨论数据失败', 'error');
  } finally {
    loading.value = false;
  }
};

// 搜索帖子
const performSearch = async () => {
  if (!searchQuery.value.trim()) {
    await fetchThreads(); // 如果搜索词为空，获取所有帖子
    return;
  }
  
  loading.value = true;
  error.value = null;
  try {
    const response = await legacyDiscussionApi.searchThreads(searchQuery.value);
    if (response && response.data) {
      threads.value = response.data;
    }
  } catch (err: any) {
    console.error('搜索失败:', err);
    error.value = '搜索失败，请重试';
  } finally {
    loading.value = false;
  }
};

// 获取讨论帖列表
const fetchThreads = async () => {
  loading.value = true;
  error.value = null;
  threads.value = [];
  
  try {
    let response;
    if (courseId.value) {
      response = await legacyDiscussionApi.getThreadsByCourse(courseId.value);
      courseName.value = `课程 ${courseId.value}`;
    } else {
      response = await legacyDiscussionApi.getAllThreads();
      courseName.value = '所有讨论';
    }
    
    if (response && response.data) {
      threads.value = response.data;
    }
  } catch (err: any) {
    console.error('获取讨论帖失败:', err);
    error.value = err.response?.data?.message || '无法加载讨论列表，请稍后再试。';
    threads.value = [];
  } finally {
    loading.value = false;
  }
};

// 表单验证函数
const validateAndCreateThread = () => {
  // 重置所有错误提示
  titleError.value = null;
  contentError.value = null;
  createThreadError.value = null;
  
  // 验证标题
  if (!newThread.title.trim()) {
    titleError.value = '请输入讨论标题';
    return false;
  } else if (newThread.title.length < 3) {
    titleError.value = '标题至少需要3个字符';
    return false;
  } else if (newThread.title.length > 100) {
    titleError.value = '标题不能超过100个字符';
    return false;
  }
  
  // 验证内容
  if (!newThread.content.trim()) {
    contentError.value = '请输入讨论内容';
    return false;
  } else if (newThread.content.length < 10) {
    contentError.value = '内容太短，请至少输入10个字符';
    return false;
  }
  
  // 如果所有验证都通过，则创建讨论帖
  createThread();
  return true;
};

// 优化创建讨论帖函数
const createThread = async () => {
  creatingThread.value = true;
  
  try {
    // 如果没有选择课程，则使用当前课程ID
    if (!newThread.courseId && courseId.value) {
      newThread.courseId = courseId.value;
    } else if (!newThread.courseId) {
      // 如果既没有选择课程，也没有当前课程ID，则显示错误
      createThreadError.value = '无法确定课程信息，请重试';
      showStatusMessage('创建失败: 无法确定课程信息', 'error');
      creatingThread.value = false;
      return;
    }
    
    const response = await legacyDiscussionApi.createThread(newThread);
    
    // 将新创建的帖子添加到列表顶部
    threads.value = [response.data, ...threads.value];
    
    // 重置表单
    newThread.title = '';
    newThread.content = '';
    
    // 隐藏创建表单
    showCreateInput.value = false;
    
    // 显示成功消息
    showStatusMessage('讨论创建成功！', 'success');
  } catch (err: any) {
    console.error('创建讨论帖失败:', err);
    createThreadError.value = err.message || '创建讨论帖失败，请重试';
    showStatusMessage('创建讨论失败', 'error');
  } finally {
    creatingThread.value = false;
  }
};

// 置顶/取消置顶帖子
const togglePinThread = async (thread: Thread) => {
  try {
    await legacyDiscussionApi.togglePinThread(thread.id, !thread.isPinned);
    thread.isPinned = !thread.isPinned;
  } catch (err: any) {
    console.error('修改置顶状态失败:', err);
    alert('操作失败，请重试');
  }
};

// 点赞帖子
const likeThread = async (threadId: string) => {
  try {
    await legacyDiscussionApi.likeThread(threadId);
    const thread = threads.value.find(t => t.id === threadId);
    if (thread) {
      if (isLikedByUser(threadId)) {
        // 取消点赞
        thread.likes = Math.max((thread.likes || 0) - 1, 0);
        userLikes.value.delete(threadId);
        showStatusMessage('已取消点赞', 'info');
      } else {
        // 点赞
        thread.likes = (thread.likes || 0) + 1;
        userLikes.value.add(threadId);
        showStatusMessage('点赞成功', 'success');
      }
    }
  } catch (err: any) {
    console.error('点赞操作失败:', err);
    showStatusMessage('点赞失败，请重试', 'error');
  }
};

// 导航到帖子详情页
const navigateToThread = (threadId: string) => {
  router.push(`/discussions/${threadId}`);
};

// 检查是否可以删除帖子
const canDeleteThread = (thread: Thread) => {
  // 作者本人、教师和助教可以删除帖子
  return thread.authorId === currentUserId.value || 
         userRole.value === 'teacher' || 
         userRole.value === 'assistant';
};

// 确认删除帖子
const confirmDeleteThread = (threadId: string) => {
  if (confirm('确定要删除这个讨论帖吗？此操作无法撤销。')) {
    deleteThread(threadId);
  }
};

// 删除帖子
const deleteThread = async (threadId: string) => {
  try {
    await legacyDiscussionApi.deleteThread(threadId);
    threads.value = threads.value.filter(t => t.id !== threadId);
    showStatusMessage('讨论帖已删除', 'success');
  } catch (err: any) {
    console.error('删除讨论帖失败:', err);
    showStatusMessage('删除失败: ' + (err.message || '未知错误'), 'error');
  }
};

// 计算热度函数
const calculateHeat = (thread: Thread) => {
  const likes = thread.likes || 0;
  const replies = thread.replyCount || 0;
  const views = thread.viewCount || 0;
  return likes * 2 + replies - Math.floor(views / 10); // 热度算法：两倍点赞+回复-浏览量/10
};

// 检查一个帖子是否热门
const isHot = (thread: Thread) => {
  // 热度阈值，可根据实际情况调整
  const heatThreshold = 10;
  return calculateHeat(thread) > heatThreshold;
};

// 开启/关闭讨论帖
const toggleCloseThread = async (thread: Thread) => {
  try {
    // 假设新API里有关闭讨论的方法，这里模拟实现
    // 实际项目中应该调用相应API
    thread.isClosed = !thread.isClosed;
    
    const actionName = thread.isClosed ? '关闭' : '开启';
    showStatusMessage(`已${actionName}讨论`, 'success');
  } catch (err: any) {
    console.error('修改讨论状态失败:', err);
    showStatusMessage('操作失败，请重试', 'error');
  }
};

// 获取用户头像的首字母
const getAvatarInitial = (name: string) => {
  if (!name) return '?';
  return name.charAt(0).toUpperCase();
};

// 格式化内容，简单处理HTML内容
const formatContent = (content: string) => {
  if (!content) return '';
  
  // 简单过滤HTML标签，实际项目中可能需要更复杂的处理或使用专门的库
  return content.replace(/<[^>]*>/g, ' ');
};

// 获取空状态提示信息
const getEmptyStateMessage = () => {
  if (searchQuery.value) {
    return '尝试使用其他关键词或查看所有讨论';
  } else if (courseId.value) {
    return '该课程下暂无讨论帖，成为第一个发起讨论的人吧！';
  } else {
    return '还没有任何讨论帖，选择一个课程或发起新的讨论吧！';
  }
};

// 检查用户是否点赞了某个帖子
const isLikedByUser = (threadId: string) => {
  return userLikes.value.has(threadId);
};

// 格式化日期函数（已删除重复的validateAndCreateThread）

// 格式化日期
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 获取用户讨论记录
const fetchUserDiscussionRecords = async () => {
  loadingUserRecords.value = true;
  userRecordsError.value = null;
  
  try {
    const response = await legacyDiscussionApi.getUserThreads();
    userThreads.value = response.data;
    
    // 暂时模拟用户回复数据，实际项目中需要从API获取
    userPosts.value = [];
    
    loadingUserRecords.value = false;
  } catch (err: any) {
    console.error('获取用户记录失败', err);
    userRecordsError.value = '获取记录失败，请重试';
    loadingUserRecords.value = false;
  }
};

// 获取通知 (暂时保留模拟数据，实际项目中需要实现通知API)
const fetchNotifications = async () => {
  loadingNotifications.value = true;
  notificationsError.value = null;
  
  try {
    // 模拟空通知列表，实际项目中应调用通知API
    notifications.value = [];
    unreadNotificationsCount.value = 0;
    loadingNotifications.value = false;
  } catch (err) {
    console.error('获取通知失败:', err);
    notificationsError.value = '获取通知失败，请重试';
    loadingNotifications.value = false;
  }
};

// 标记通知为已读
const markNotificationAsRead = async (notificationId: string) => {
  try {
    await legacyDiscussionApi.markNotificationAsRead(notificationId);
    
    // 更新本地状态
    const notification = notifications.value.find(n => n.id === notificationId);
    if (notification && !notification.isRead) {
      notification.isRead = true;
      unreadNotificationsCount.value = Math.max(unreadNotificationsCount.value - 1, 0);
    }
  } catch (err) {
    console.error('标记通知已读失败:', err);
  }
};

onMounted(async () => {
  courseId.value = route.params.courseId as string || null;
  if (newThread) {
    newThread.courseId = courseId.value || '';
  }
  
  // 获取真实的讨论数据
  await fetchThreads();
  
  // 初始化未读通知计数（实际项目中应从API获取）
  unreadNotificationsCount.value = 0;
});

// 监听模态窗口显示状态，加载相应数据
watch(showUserRecordModal, (newVal) => {
  if (newVal) {
    fetchUserDiscussionRecords();
  }
});

watch(showNotificationsModal, (newVal) => {
  if (newVal) {
    fetchNotifications();
  }
});

// 监听内联显示面板，加载相应数据
watch(showUserRecordPane, (newVal) => {
  if (newVal) {
    fetchUserDiscussionRecords();
  }
});

watch(showNotificationPane, (newVal) => {
  if (newVal) {
    fetchNotifications();
  }
});

</script>

<style scoped>
/* CSS Variables for consistent theming */
:root {
  --primary: #667eea;
  --primary-dark: #5a67d8;
  --primary-light: #e6f3ff;
  --secondary: #f093fb;
  --accent: #4facfe;
  --success: #48bb78;
  --warning: #ed8936;
  --danger: #f56565;
  --bg-primary: #ffffff;
  --bg-secondary: #f8fafc;
  --bg-tertiary: #edf2f7;
  --bg-quaternary: #e2e8f0;
  --text-primary: #2d3748;
  --text-secondary: #4a5568;
  --text-tertiary: #718096;
  --border-color: #e2e8f0;
  --shadow-sm: 0 2px 4px rgba(0, 0, 0, 0.1);
  --shadow-md: 0 4px 12px rgba(0, 0, 0, 0.15);
  --shadow-lg: 0 8px 25px rgba(0, 0, 0, 0.2);
  --shadow-xl: 0 12px 35px rgba(0, 0, 0, 0.25);
  --transition-fast: 0.2s ease;
  --transition-normal: 0.3s ease;
  --transition-slow: 0.5s ease;
  --border-radius: 12px;
  --border-radius-sm: 8px;
  --border-radius-lg: 16px;
}

/* Dark mode variables */
@media (prefers-color-scheme: dark) {
  :root {
    --bg-primary: #1a202c;
    --bg-secondary: #2d3748;
    --bg-tertiary: #4a5568;
    --bg-quaternary: #718096;
    --text-primary: #f7fafc;
    --text-secondary: #e2e8f0;
    --text-tertiary: #cbd5e0;
    --border-color: #4a5568;
  }
}

/* Main layout and background styling */
.discussion-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 20px;
  position: relative;
}

.discussion-layout::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(120, 219, 255, 0.2) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

.discussion-list-page {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  color: var(--text-primary);
  transition: all var(--transition-normal);
  position: relative;
  z-index: 1;
  border-radius: var(--border-radius-lg);
  width: 100%;
  max-width: 1200px;
}

.discussion-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
  background: transparent;
  transition: all var(--transition-normal);
}

/* Enhanced loading and empty states */
.skeleton-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  padding: 1rem 0;
}

.skeleton-card {
  padding: 2rem;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-md);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  position: relative;
  overflow: hidden;
}

.skeleton-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
}

.skeleton-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.skeleton-meta {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.skeleton-tag {
  height: 1.5rem;
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.1), rgba(102, 126, 234, 0.2), rgba(102, 126, 234, 0.1));
  background-size: 200% 100%;
  animation: skeleton-shimmer 2s infinite;
  border-radius: 20px;
  width: 5rem;
}

.skeleton-tag.large {
  width: 8rem;
}

.skeleton-date {
  height: 1rem;
  width: 6rem;
  background: linear-gradient(90deg, rgba(113, 128, 150, 0.1), rgba(113, 128, 150, 0.2), rgba(113, 128, 150, 0.1));
  background-size: 200% 100%;
  animation: skeleton-shimmer 2s infinite;
  border-radius: 10px;
}

.skeleton-title {
  height: 1.75rem;
  width: 70%;
  background: linear-gradient(90deg, rgba(45, 55, 72, 0.1), rgba(45, 55, 72, 0.2), rgba(45, 55, 72, 0.1));
  background-size: 200% 100%;
  animation: skeleton-shimmer 2s infinite;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.skeleton-content {
  height: 1rem;
  width: 100%;
  background: linear-gradient(90deg, rgba(74, 85, 104, 0.1), rgba(74, 85, 104, 0.2), rgba(74, 85, 104, 0.1));
  background-size: 200% 100%;
  animation: skeleton-shimmer 2s infinite;
  border-radius: 6px;
  margin-bottom: 0.5rem;
}

.skeleton-content.short {
  width: 60%;
}

.skeleton-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid rgba(226, 232, 240, 0.3);
}

.skeleton-actions {
  display: flex;
  gap: 0.75rem;
}

.skeleton-action {
  height: 1.5rem;
  width: 4rem;
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.1), rgba(102, 126, 234, 0.2), rgba(102, 126, 234, 0.1));
  background-size: 200% 100%;
  animation: skeleton-shimmer 2s infinite;
  border-radius: 20px;
}

@keyframes skeleton-pulse {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 1; }
}

@keyframes skeleton-shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

/* Enhanced empty state */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 4rem 2rem;
  border-radius: var(--border-radius);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border: 2px dashed rgba(102, 126, 234, 0.3);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
}

.empty-state::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 20%, rgba(102, 126, 234, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(240, 147, 251, 0.05) 0%, transparent 50%);
  pointer-events: none;
}

.empty-state .fa {
  color: var(--primary);
  margin-bottom: 1.5rem;
  opacity: 0.7;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.empty-title {
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.75rem;
}

.empty-description {
  color: var(--text-tertiary);
  margin-bottom: 2rem;
  max-width: 400px;
  line-height: 1.6;
}

.clear-search-btn,
.start-btn {
  padding: 0.75rem 1.5rem;
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid rgba(102, 126, 234, 0.3);
  border-radius: 25px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

.clear-search-btn::before,
.start-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  transition: left var(--transition-normal);
  z-index: -1;
}

.clear-search-btn:hover,
.start-btn:hover {
  color: white;
  border-color: var(--primary);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
}

.clear-search-btn:hover::before,
.start-btn:hover::before {
  left: 0;
}

.start-btn {
  background: linear-gradient(135deg, var(--primary-light), rgba(255, 255, 255, 0.9));
  color: var(--primary);
  border-color: var(--primary);
  font-weight: 700;
}

/* Enhanced error styling */
.alert-error {
  background: rgba(245, 101, 101, 0.1);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(245, 101, 101, 0.3);
  border-left: 4px solid var(--danger);
  border-radius: var(--border-radius);
  padding: 2rem;
  margin-bottom: 2rem;
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  box-shadow: var(--shadow-md);
}

.alert-error .fa {
  color: var(--danger);
  flex-shrink: 0;
  margin-top: 0.25rem;
  font-size: 1.25rem;
}

.error-title {
  font-weight: 700;
  margin-bottom: 0.75rem;
  color: var(--danger);
}

.retry-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
  padding: 0.75rem 1.25rem;
  background: rgba(245, 101, 101, 0.1);
  color: var(--danger);
  border: 2px solid var(--danger);
  border-radius: 25px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-normal);
  backdrop-filter: blur(10px);
}

.retry-btn:hover {
  background: var(--danger);
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(245, 101, 101, 0.4);
}

.course-tag {
  background-color: rgba(25, 118, 210, 0.1);
  color: var(--primary);
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.875rem;
}

.discussion-tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.text-secondary {
  color: var(--text-secondary);
}

/* WriteBoard编辑器样式微调 */
.q-editor {
  border-radius: 0.375rem;
  border-color: #e5e7eb;
  min-height: 250px;
  margin-bottom: 1rem;
}

.q-editor:focus-within {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.3);
}

/* 创建讨论帖表单样式 */
.create-discussion-form {
  background-color: var(--bg-primary);
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-md);
  margin-bottom: 1.5rem;
}

/* 表单样式 */
.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.form-group input, .form-group select, .form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  font-size: 1rem;
}

.form-group input:focus, .form-group select:focus, .form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(44, 110, 207, 0.1);
 }

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1.5rem;
}

/* 创建讨论帖按钮特效 */
.create-thread-btn {
  position: relative;
  overflow: hidden;
}

.create-thread-btn::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(-100%);
  transition: transform 0.3s ease;
}

.create-thread-btn:hover::after {
  transform: translateX(0);
}

.search-container {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 1.5rem;
}

.search-inner {
  width: 100%;
  display: flex;
  box-shadow: var(--shadow-sm);
  border-radius: 8px;
  overflow: hidden;
}

.search-input {
  width: 100%;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  border: 1px solid var(--border-color);
  border-right: none;
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  transition: all var(--transition-fast);
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.3);
}

.search-btn {
  padding: 0.75rem 1.5rem;
  background-color: var(--primary);
  color: white;
  border: none;
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color var(--transition-fast);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.modal-content {
  max-height: 80vh;
  overflow-y: auto;
}

/* Enhanced toolbar styling */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 1.5rem 2rem;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-md);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.toolbar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary), var(--secondary), var(--accent));
}

.toolbar-left {
  display: flex;
  align-items: center;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.title {
  font-size: 1.75rem;
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.title .fa {
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.badge {
  background: linear-gradient(135deg, var(--danger), #ff6b6b);
  color: white;
  border-radius: 20px;
  padding: 0.25rem 0.75rem;
  font-size: 0.75rem;
  font-weight: 600;
  margin-left: 0.5rem;
  box-shadow: 0 2px 8px rgba(245, 101, 101, 0.3);
  animation: pulse-badge 2s infinite;
}

@keyframes pulse-badge {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.create-discussion-btn {
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.95rem;
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  position: relative;
  overflow: hidden;
}

.create-discussion-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.create-discussion-btn:hover::before {
  left: 100%;
}

.create-discussion-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.action-buttons {
  display: flex;
  gap: 0.75rem;
}

.action-btn {
  padding: 0.6rem 1rem;
  border: 2px solid transparent;
  background: rgba(255, 255, 255, 0.8);
  color: var(--text-secondary);
  border-radius: 20px;
  cursor: pointer;
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

.action-btn::after {
  content: '';
  position: absolute;
  width: 0;
  height: 100%;
  top: 0;
  left: 0;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  transition: width var(--transition-normal);
  z-index: -1;
}

.action-btn:hover {
  color: white;
  border-color: var(--primary);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.action-btn:hover::after {
  width: 100%;
}

.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* Search container enhanced styling */
.search-container {
  margin-bottom: 2rem;
}

.search-inner {
  display: flex;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 30px;
  box-shadow: var(--shadow-md);
  border: 1px solid rgba(255, 255, 255, 0.3);
  overflow: hidden;
  transition: all var(--transition-normal);
}

.search-inner:focus-within {
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  transform: translateY(-2px);
}

.search-input {
  flex: 1;
  padding: 1rem 1.5rem;
  font-size: 1rem;
  border: none;
  background: transparent;
  color: var(--text-primary);
  outline: none;
}

.search-input::placeholder {
  color: var(--text-tertiary);
}

.search-btn {
  padding: 1rem 2rem;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.search-btn:hover {
  background: linear-gradient(135deg, var(--primary-dark), var(--secondary));
}

/* Filter toolbar enhanced styling */
.filter-toolbar {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-md);
  border: 1px solid rgba(255, 255, 255, 0.3);
  gap: 1.5rem;
}

.filter-section, .sort-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.filter-label, .sort-label {
  color: var(--text-secondary);
  font-weight: 600;
  font-size: 0.95rem;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.filter-option {
  background: rgba(255, 255, 255, 0.8);
  border: 2px solid rgba(102, 126, 234, 0.2);
  color: var(--text-secondary);
  border-radius: 25px;
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

.filter-option::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  transition: left var(--transition-normal);
  z-index: -1;
}

.filter-option:hover {
  color: white;
  border-color: var(--primary);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.filter-option:hover::before {
  left: 0;
}

.filter-option.active {
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  border-color: var(--primary);
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.sort-select {
  padding: 0.75rem 1.25rem;
  border-radius: 25px;
  border: 2px solid rgba(102, 126, 234, 0.2);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  color: var(--text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  appearance: none;
  padding-right: 2rem;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%23667eea' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1rem;
  transition: all var(--transition-normal);
}

.sort-select:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
  transform: translateY(-1px);
}

/* Discussion cards enhanced styling */
.discussion-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-md);
  margin-bottom: 1.5rem;
  padding: 2rem;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.discussion-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  transition: width var(--transition-normal);
}

.discussion-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
  border-color: rgba(102, 126, 234, 0.3);
}

.discussion-card:hover::before {
  width: 8px;
}

.discussion-card.pinned {
  border-color: rgba(237, 137, 54, 0.3);
  background: linear-gradient(135deg, rgba(255, 243, 224, 0.9), rgba(255, 255, 255, 0.95));
}

.discussion-card.pinned::before {
  background: linear-gradient(135deg, var(--warning), #ffa726);
}

.discussion-card.closed {
  opacity: 0.7;
  background: linear-gradient(135deg, rgba(248, 250, 252, 0.9), rgba(255, 255, 255, 0.95));
}

.pin-indicator {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: linear-gradient(135deg, var(--warning), #ffa726);
  color: white;
  border-radius: 50%;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  box-shadow: 0 2px 8px rgba(237, 137, 54, 0.3);
  animation: pin-glow 2s infinite alternate;
}

@keyframes pin-glow {
  0% { box-shadow: 0 2px 8px rgba(237, 137, 54, 0.3); }
  100% { box-shadow: 0 4px 16px rgba(237, 137, 54, 0.6); }
}

.discussion-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.5rem;
}

.discussion-meta {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  align-items: center;
}

.tag {
  padding: 0.375rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.25rem;
  transition: all var(--transition-normal);
}

.pin-tag {
  background: linear-gradient(135deg, var(--warning), #ffa726);
  color: white;
  box-shadow: 0 2px 8px rgba(237, 137, 54, 0.3);
}

.closed-tag {
  background: linear-gradient(135deg, #94a3b8, #cbd5e1);
  color: white;
  box-shadow: 0 2px 8px rgba(148, 163, 184, 0.3);
}

.like-tag {
  background: rgba(245, 101, 101, 0.1);
  color: var(--danger);
  border: 1px solid rgba(245, 101, 101, 0.2);
}

.like-tag.highlight {
  background: linear-gradient(135deg, var(--danger), #ff6b6b);
  color: white;
  box-shadow: 0 2px 8px rgba(245, 101, 101, 0.3);
  animation: like-pulse 0.6s ease;
}

@keyframes like-pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.reply-tag {
  background: rgba(79, 172, 254, 0.1);
  color: var(--accent);
  border: 1px solid rgba(79, 172, 254, 0.2);
}

.heat-tag {
  background: rgba(102, 126, 234, 0.1);
  color: var(--primary);
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.heat-tag.hot {
  background: linear-gradient(135deg, #ff6b6b, #ffa726);
  color: white;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
  animation: heat-glow 2s infinite alternate;
}

@keyframes heat-glow {
  0% { box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3); }
  100% { box-shadow: 0 4px 16px rgba(255, 107, 107, 0.6); }
}

.view-tag {
  background: rgba(113, 128, 150, 0.1);
  color: var(--text-tertiary);
  border: 1px solid rgba(113, 128, 150, 0.2);
}

.thread-meta-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.5rem;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.author-avatar {
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.875rem;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.author-name {
  color: var(--text-secondary);
  font-weight: 600;
  font-size: 0.875rem;
}

.date-info {
  color: var(--text-tertiary);
  font-size: 0.75rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.thread-content-area {
  cursor: pointer;
  transition: all var(--transition-normal);
  padding: 1rem;
  border-radius: var(--border-radius-sm);
  margin: 1rem 0;
}

.thread-content-area:hover {
  background: rgba(102, 126, 234, 0.05);
  transform: translateX(4px);
}

.discussion-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 0.75rem;
  line-height: 1.4;
  transition: color var(--transition-normal);
}

.thread-content-area:hover .discussion-title {
  color: var(--primary);
}

.discussion-content-wrapper {
  margin-bottom: 0.75rem;
}

.discussion-content {
  color: var(--text-secondary);
  line-height: 1.6;
  font-size: 0.95rem;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.read-more-link {
  color: var(--primary);
  font-size: 0.875rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.25rem;
  transition: all var(--transition-normal);
}

.thread-content-area:hover .read-more-link {
  color: var(--secondary);
  transform: translateX(4px);
}

.discussion-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid rgba(226, 232, 240, 0.5);
}

.discussion-tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.course-tag {
  background: rgba(79, 172, 254, 0.1);
  color: var(--accent);
  padding: 0.375rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  border: 1px solid rgba(79, 172, 254, 0.2);
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.discussion-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.discussion-actions .action-btn {
  padding: 0.5rem 0.75rem;
  border: 2px solid transparent;
  background: rgba(255, 255, 255, 0.8);
  color: var(--text-secondary);
  border-radius: 20px;
  cursor: pointer;
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.75rem;
  font-weight: 500;
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

.discussion-actions .action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  transition: left var(--transition-normal);
  z-index: -1;
}

.discussion-actions .action-btn:hover {
  color: white;
  border-color: var(--primary);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.discussion-actions .action-btn:hover::before {
  left: 0;
}

.discussion-actions .action-btn.active {
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  border-color: var(--primary);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.like-btn {
  position: relative;
}

.like-btn.liked {
  background: linear-gradient(135deg, var(--danger), #ff6b6b);
  color: white;
  border-color: var(--danger);
  box-shadow: 0 4px 15px rgba(245, 101, 101, 0.4);
}

.like-pulse {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: pulse-effect 0.6s ease-out;
}

@keyframes pulse-effect {
  0% {
    transform: translate(-50%, -50%) scale(0);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(1.5);
    opacity: 0;
  }
}

.delete-btn:hover {
  background: linear-gradient(135deg, var(--danger), #ff6b6b) !important;
  border-color: var(--danger) !important;
  box-shadow: 0 4px 15px rgba(245, 101, 101, 0.4) !important;
}

.action-text {
  display: none;
}

@media (min-width: 768px) {
  .action-text {
    display: inline;
  }
}

/* Enhanced action bar styling */
.discussion-action-bar {
  position: fixed;
  right: 2rem;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(211, 176, 227, 0.95);
  backdrop-filter: blur(20px);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-xl);
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  z-index: 100;
  transition: all var(--transition-normal);
}

.discussion-action-bar:hover {
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  transform: translateY(-50%) translateX(-4px);
}

.action-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  border-radius: var(--border-radius);
  cursor: pointer;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 2px solid transparent;
}

.action-nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  transition: left var(--transition-normal);
  z-index: -1;
}

.action-nav-item:hover {
  color: white;
  border-color: var(--primary);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.action-nav-item:hover::before {
  left: 0;
}

.action-nav-item.create-thread-btn {
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  border-color: var(--primary);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.action-nav-item.create-thread-btn:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 25px rgba(102, 126, 234, 0.6);
}

.action-icon {
  width: 1.5rem;
  height: 1.5rem;
  transition: all var(--transition-normal);
}

.action-nav-item:hover .action-icon {
  transform: scale(1.1);
}

.action-label {
  font-size: 0.75rem;
  font-weight: 600;
  text-align: center;
  line-height: 1.2;
  transition: all var(--transition-normal);
}

/* Status toast styling */
.status-toast {
  position: fixed;
  top: 2rem;
  right: 2rem;
  padding: 1rem 1.5rem;
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-lg);
  z-index: 1100;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-weight: 600;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  animation: toast-appear 0.4s ease-out forwards;
  min-width: 300px;
  max-width: 400px;
}

@keyframes toast-appear {
  0% {
    opacity: 0;
    transform: translateX(100%) scale(0.9);
  }
  100% {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

.status-toast.success {
  background: rgba(72, 187, 120, 0.95);
  color: white;
  border-color: rgba(72, 187, 120, 0.3);
}

.status-toast.error {
  background: rgba(245, 101, 101, 0.95);
  color: white;
  border-color: rgba(245, 101, 101, 0.3);
}

.status-toast.warning {
  background: rgba(237, 137, 54, 0.95);
  color: white;
  border-color: rgba(237, 137, 54, 0.3);
}

.status-toast.info {
  background: rgba(102, 126, 234, 0.95);
  color: white;
  border-color: rgba(102, 126, 234, 0.3);
}

.status-toast .fa {
  font-size: 1.125rem;
  flex-shrink: 0;
}

/* Progress indicator styles */
.progress-indicator {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 2rem;
  padding: 0 2rem;
  gap: 1rem;
}

/* Progress steps */
.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  opacity: 0.4;
  transition: all var(--transition-normal);
  position: relative;
}

.progress-step.active {
  opacity: 0.8;
  color: var(--primary);
}

.progress-step.completed {
  opacity: 1;
  color: var(--success);
}

.step-circle {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  background: rgba(226, 232, 240, 0.5);
  border: 2px solid rgba(226, 232, 240, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.125rem;
  transition: all var(--transition-normal);
  backdrop-filter: blur(10px);
}

.progress-step.active .step-circle {
  background: rgba(102, 126, 234, 0.1);
  border-color: var(--primary);
  color: var(--primary);
  transform: scale(1.1);
}

.progress-step.completed .step-circle {
  background: rgba(34, 197, 94, 0.15);
  border-color: var(--success);
  color: var(--success);
  transform: scale(1.1);
  animation: step-complete 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

@keyframes step-complete {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

.step-label {
  font-size: 0.875rem;
  font-weight: 600;
  text-align: center;
  transition: all var(--transition-normal);
}

.progress-line {
  width: 3rem;
  height: 2px;
  background: rgba(226, 232, 240, 0.5);
  position: relative;
  overflow: hidden;
  border-radius: 2px;
}

.progress-line.active {
  background: var(--primary);
  animation: line-fill 0.5s ease-out;
}

@keyframes line-fill {
  0% { transform: scaleX(0); }
  100% { transform: scaleX(1); }
}

.progress-line.active::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
  animation: line-shine 1.5s infinite;
}

@keyframes line-shine {
  0% { left: -100%; }
  100% { left: 100%; }
}

.progress-text {
  text-align: center;
  margin-bottom: 2rem;
  padding: 0 2rem;
}

.progress-percentage {
  display: block;
  font-size: 1.125rem;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: 0.25rem;
}

.progress-description {
  display: block;
  font-size: 0.875rem;
  color: var(--text-secondary);
  font-weight: 500;
}

/* Enhanced form group styles */
.enhanced-form-group {
  margin-bottom: 2.5rem;
  position: relative;
}

.enhanced-label {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.875rem;
  font-weight: 700;
  color: var(--text-primary);
  font-size: 1rem;
}

.label-main {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.label-icon {
  color: var(--primary);
  transition: all var(--transition-normal);
}

.required-asterisk {
  color: var(--danger);
  margin-left: 0.25rem;
  font-weight: 700;
}

.enhanced-char-count {
  font-size: 0.75rem;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  background: rgba(102, 126, 234, 0.1);
  color: var(--primary);
  font-weight: 600;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  gap: 0.125rem;
}

.enhanced-char-count.success {
  background: rgba(34, 197, 94, 0.15);
  color: var(--success);
}

.enhanced-char-count.warning {
  background: rgba(237, 137, 54, 0.15);
  color: var(--warning);
  animation: pulse-warning 2s infinite;
}

.enhanced-char-count.error {
  background: rgba(245, 101, 101, 0.15);
  color: var(--danger);
  animation: shake-count 0.5s ease-in-out;
}

.count-text {
  font-weight: 700;
}

.count-separator {
  opacity: 0.6;
}

.count-max {
  opacity: 0.8;
}

@keyframes pulse-warning {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.05); opacity: 0.8; }
}

@keyframes shake-count {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-3px); }
  75% { transform: translateX(3px); }
}

/* Input wrapper styles */
.input-group-wrapper {
  position: relative;
}

.input-wrapper, .content-editor-wrapper {
  position: relative;
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(226, 232, 240, 0.5);
  overflow: hidden;
}

.input-wrapper:hover, .content-editor-wrapper:hover {
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.input-wrapper.focused, .content-editor-wrapper.focused {
  border-color: var(--primary);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
}

.input-wrapper.error, .content-editor-wrapper.error {
  border-color: var(--danger);
  background: rgba(245, 101, 101, 0.05);
  animation: error-shake 0.5s ease-in-out;
}

.input-wrapper.success, .content-editor-wrapper.success {
  border-color: var(--success);
  background: rgba(34, 197, 94, 0.05);
}

@keyframes error-shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.input-wrapper .form-input {
  border: none;
  background: transparent;
  box-shadow: none;
  transition: all var(--transition-normal);
}

.input-wrapper .form-input:focus {
  box-shadow: none;
  transform: none;
}

.success-icon {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: var(--success);
  font-size: 1.125rem;
  opacity: 0;
  transition: all var(--transition-normal);
}

.input-wrapper.success .success-icon,
.content-editor-wrapper.success .success-icon {
  opacity: 1;
  animation: success-bounce 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

@keyframes success-bounce {
  0% { transform: translateY(-50%) scale(0); }
  50% { transform: translateY(-50%) scale(1.2); }
  100% { transform: translateY(-50%) scale(1); }
}

/* Enhanced hint styles */
.enhanced-hint {
  margin-top: 1rem;
  min-height: 1.5rem;
}

.enhanced-hint .form-error {
  padding: 0.75rem 1rem;
  background: rgba(245, 101, 101, 0.1);
  border-radius: var(--border-radius-sm);
  border-left: 4px solid var(--danger);
  margin-bottom: 0.5rem;
  animation: error-slide-in 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.enhanced-hint .form-tip {
  padding: 0.5rem 1rem;
  background: rgba(102, 126, 234, 0.05);
  border-radius: var(--border-radius-sm);
  border-left: 4px solid var(--primary);
  animation: tip-fade-in 0.3s ease-out;
}

@keyframes error-slide-in {
  0% {
    opacity: 0;
    transform: translateX(-20px);
    max-height: 0;
  }
  100% {
    opacity: 1;
    transform: translateX(0);
    max-height: 100px;
  }
}

@keyframes tip-fade-in {
  0% {
    opacity: 0;
    transform: translateY(-10px);
  }  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== 全屏模态框样式 ===== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.65);
  backdrop-filter: blur(12px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 1rem;
}

.create-modal-container {
  width: 95%;
  max-width: 1000px;
  max-height: 95vh;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(25px);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15), 
              0 0 0 1px rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.create-modal-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.create-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2rem 2.5rem 1.5rem;
  background: linear-gradient(135deg, 
    var(--primary) 0%, 
    var(--secondary) 50%, 
    rgba(102, 126, 234, 0.9) 100%);
  color: white;
  position: relative;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.header-icon-wrapper {
  width: 3rem;
  height: 3rem;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.header-icon-wrapper i {
  font-size: 1.25rem;
  color: white;
}

.header-text h2 {
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0 0 0.25rem 0;
  line-height: 1.2;
}

.header-subtitle {
  font-size: 0.95rem;
  opacity: 0.9;
  margin: 0;
  font-weight: 400;
}

.close-modal-btn {
  background: rgba(255, 255, 255, 0.15);
  border: 2px solid rgba(255, 255, 255, 0.25);
  color: white;
  cursor: pointer;
  width: 2.75rem;
  height: 2.75rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
  font-size: 1.125rem;
}

.close-modal-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.4);
  transform: rotate(90deg) scale(1.05);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.create-modal-body {
  padding: 2.5rem;
  flex: 1;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.6);
  position: relative;
}

/* 自定义滚动条 */
.create-modal-body::-webkit-scrollbar {
  width: 8px;
}

.create-modal-body::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
}

.create-modal-body::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.3);
  border-radius: 4px;
  transition: all 0.3s ease;
}

.create-modal-body::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.5);
}

.create-modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2rem 2.5rem;
  background: rgba(248, 250, 252, 0.95);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(226, 232, 240, 0.6);
}

.enhanced-footer {
  gap: 2rem;
}

.footer-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: var(--text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
}

.footer-info i {
  color: var(--primary);
  font-size: 1rem;
}

.footer-buttons {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.enhanced-btn {
  padding: 0.875rem 2rem;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  min-width: 120px;
  justify-content: center;
}

.enhanced-btn:before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.enhanced-btn:hover:before {
  left: 100%;
}

.btn-secondary.enhanced-btn {
  background: rgba(156, 163, 175, 0.1);
  border: 2px solid rgba(156, 163, 175, 0.3);
  color: var(--text-secondary);
}

.btn-secondary.enhanced-btn:hover {
  background: rgba(156, 163, 175, 0.2);
  border-color: rgba(156, 163, 175, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(156, 163, 175, 0.3);
}

.btn-primary.enhanced-btn {
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  border: 2px solid transparent;
  color: white;
  position: relative;
}

.btn-primary.enhanced-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.btn-primary.enhanced-btn.pulse {
  animation: btn-pulse 2s infinite;
}

@keyframes btn-pulse {
  0%, 100% {
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  }
  50% {
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6), 
                0 0 0 8px rgba(102, 126, 234, 0.1);
  }
}

.btn-primary.enhanced-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-primary.enhanced-btn.loading {
  cursor: wait;
}

.btn-primary.enhanced-btn.loading i {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* Modal transition animations */
.modal-fade-enter-active {
  transition: opacity 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-fade-leave-active {
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .create-modal-container {
  animation: modal-slide-in 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.modal-fade-leave-active .create-modal-container {
  animation: modal-slide-out 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes modal-slide-in {
  0% {
    opacity: 0;
    transform: translateY(60px) scale(0.8);
    filter: blur(10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
    filter: blur(0);
  }
}

@keyframes modal-slide-out {
  0% {
    opacity: 1;
    transform: translateY(0) scale(1);
    filter: blur(0);
  }
  100% {
    opacity: 0;
    transform: translateY(40px) scale(0.9);
    filter: blur(5px);
  }
}

/* Enhanced error global styling */
.enhanced-error-global {
  background: linear-gradient(135deg, rgba(245, 101, 101, 0.1), rgba(245, 101, 101, 0.05));
  border: 1px solid rgba(245, 101, 101, 0.2);
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  animation: error-appear 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.error-icon-wrapper {
  width: 2.5rem;
  height: 2.5rem;
  background: rgba(245, 101, 101, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.error-icon-wrapper i {
  color: var(--danger);
  font-size: 1.125rem;
}

.error-content {
  flex: 1;
}

.error-title {
  font-weight: 700;
  color: var(--danger);
  font-size: 1rem;
  margin-bottom: 0.25rem;
}

.error-message {
  color: var(--text-secondary);
  font-size: 0.875rem;
  line-height: 1.5;
}

@keyframes error-appear {
  0% {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* Error slide transition */
.error-slide-enter-active,
.error-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.error-slide-enter-from {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
  max-height: 0;
}

.error-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.98);
  max-height: 0;
}

/* Responsive design for modal */
@media (max-width: 768px) {
  .modal-overlay {
    padding: 0.5rem;
  }
  
  .create-modal-container {
    width: 98%;
    max-height: 98vh;
    border-radius: 16px;
  }
  
  .create-modal-header {
    padding: 1.5rem 1.5rem 1rem;
  }
  
  .header-text h2 {
    font-size: 1.5rem;
  }
  
  .header-subtitle {
    font-size: 0.875rem;
  }
  
  .create-modal-body {
    padding: 2rem 1.5rem;
  }
  
  .create-modal-footer {
    padding: 1.5rem;
    flex-direction: column;
    gap: 1rem;
  }
  
  .footer-buttons {
    width: 100%;
    justify-content: stretch;
  }
  
  .enhanced-btn {
    flex: 1;
    min-width: auto;
  }
  
  .enhanced-form-group {
    margin-bottom: 2rem;
  }
  
  .progress-indicator {
    padding: 0 1rem;
    gap: 0.75rem;
  }
  
  .progress-step {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .progress-line {
    width: 2rem;
    transform: rotate(90deg);
  }
}

@media (max-width: 480px) {
  .create-modal-container {
    width: 100%;
    height: 100%;
    max-height: 100vh;
    border-radius: 0;
    margin: 0;
  }
  
  .enhanced-label {
    font-size: 0.875rem;
  }
  
  .progress-indicator {
    flex-direction: column;
    gap: 1rem;
    margin-bottom: 2rem;
  }
  
  .progress-line {
    width: 100%;
    height: 2px;
    transform: none;
  }
  
  .header-content {
    gap: 0.75rem;
  }
  
  .header-icon-wrapper {
    width: 2.5rem;
    height: 2.5rem;
  }
  
  .close-modal-btn {
    width: 2.5rem;
    height: 2.5rem;
    font-size: 1rem;
  }
}

/* High contrast mode support */
@media (prefers-contrast: high) {
  .modal-overlay {
    background: rgba(0, 0, 0, 0.8);
  }
  
  .create-modal-container {
    background: white;
    border: 2px solid black;
  }
  
  .create-modal-header {
    background: var(--primary);
    border-bottom: 2px solid black;
  }
  
  .enhanced-btn {
    border-width: 2px;
  }
}

/* Reduced motion support */
@media (prefers-reduced-motion: reduce) {
  .modal-fade-enter-active,
  .modal-fade-leave-active {
    transition-duration: 0.1s;
  }
  
  .create-modal-container {
    animation: none;
  }
  
  .enhanced-btn:before {
    display: none;
  }
  
  .btn-pulse {
    animation: none;
  }
  
  .progress-line.active::after {
    animation: none;
  }
}
</style>
