<template>
  <div class="thread-detail-page p-6 bg-gray-50 min-h-screen">
    <div class="max-w-3xl mx-auto">      <button @click="goBack" class="mb-6 btn btn-secondary flex items-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
        </svg>
        返回列表
      </button>

      <div v-if="loadingThread || loadingPosts" class="text-center py-10">
        <p class="text-blue-600">加载中...</p>
      </div>

      <div v-else-if="threadError || postsError" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-6" role="alert">
        <strong class="font-bold">错误!</strong>
        <span class="block sm:inline"> {{ threadError || postsError }}</span>
      </div>

      <div v-else-if="!thread" class="text-center py-10 bg-white shadow-md rounded-lg">
        <p class="text-gray-600 text-lg">讨论帖未找到。</p>
      </div>

      <div v-else class="bg-white shadow-xl rounded-lg overflow-hidden">
        <!-- Thread Details -->
        <div class="p-6 border-b border-gray-200">
          <h1 class="text-3xl font-bold text-blue-700 mb-3">{{ thread.title }}</h1>
          <p class="text-sm text-gray-500 mb-4">
            由 <span class="font-medium text-blue-600">{{ thread.authorName }}</span> 创建于 {{ formatDate(thread.createdAt) }}
          </p>
          <div class="prose max-w-none text-gray-800" v-html="thread.content"></div>
        </div>

        <!-- Posts/Replies -->
        <div class="p-6">
          <h2 class="text-xl font-semibold text-blue-600 mb-4">回复 ({{ posts.length }})</h2>
          <div v-if="posts.length > 0" class="space-y-4 mb-6">
            <div v-for="post in posts" :key="post.id" class="p-4 bg-gray-50 rounded-md border border-gray-200 relative">
              <div class="flex justify-between items-start">
                <p class="text-sm text-gray-500 mb-1">
                  <span class="font-medium text-blue-600">{{ post.authorName }}</span> 回复于 {{ formatDate(post.createdAt) }}
                </p>
                <div class="flex space-x-2">                  <!-- 点赞按钮 -->
                  <button 
                    @click="likePost(post.id)"
                    class="btn btn-primary text-xs flex items-center"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                    </svg>
                    {{ post.likeCount || 0 }}
                  </button>
                  <!-- 删除按钮（仅作者、助教和老师可见） -->
                  <button 
                    v-if="canDeletePost(post)"
                    @click="confirmDeletePost(post.id)"
                    class="btn btn-secondary text-xs flex items-center"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                    删除
                  </button>
                </div>
              </div>
              <div class="prose prose-sm max-w-none text-gray-700" v-html="post.content"></div>
            </div>
          </div>
          <p v-else class="text-gray-500 mb-6">还没有回复，快来抢沙发！</p>

          <!-- Create Post Form -->
          <form @submit.prevent="handleCreatePost" class="mt-6 pt-6 border-t border-gray-200">
            <h3 class="text-lg font-semibold text-blue-600 mb-3">发表你的回复</h3>
            <div>
              <label for="postContent" class="sr-only">回复内容</label>
              <textarea 
                id="postContent" 
                v-model="newPost.content" 
                rows="4" 
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                placeholder="输入你的回复..."
                required
              ></textarea>
            </div>
            <div class="mt-3 flex justify-end">              
              <button 
                type="submit" 
                :disabled="creatingPost"
                class="btn btn-primary"
              >
                {{ creatingPost ? '提交中...' : '提交回复' }}
              </button>
            </div>
            <p v-if="createPostError" class="text-red-500 text-sm mt-2">{{ createPostError }}</p>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import discussionApi, { type Discussion, type DiscussionReply } from '../../api/discussion';

const route = useRoute();
const router = useRouter();

// 当前用户信息
const userRole = ref<'student' | 'assistant' | 'teacher'>('student'); // 默认为学生
const currentUserId = ref<string>('user123'); // 模拟当前用户ID

const threadId = ref<string>(route.params.threadId as string);
const thread = ref<Discussion | null>(null);
const posts = ref<DiscussionReply[]>([]);

const loadingThread = ref(true);
const threadError = ref<string | null>(null);
const loadingPosts = ref(true);
const postsError = ref<string | null>(null);

const creatingPost = ref(false);
const createPostError = ref<string | null>(null);
const newPost = reactive({
  content: '',
});

const fetchThreadDetails = async () => {
  loadingThread.value = true;
  threadError.value = null;
  try {
    const response = await discussionApi.getDiscussionDetail(Number(threadId.value));
    thread.value = response.data;
  } catch (err: any) {
    console.error('Failed to fetch thread details:', err);
    threadError.value = err.response?.data?.message || '无法加载讨论帖详情。';
  } finally {
    loadingThread.value = false;
  }
};

const fetchPosts = async () => {
  loadingPosts.value = true;
  postsError.value = null;
  try {
    const response = await discussionApi.getAllReplies(Number(threadId.value));
    posts.value = response.data.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime());
  } catch (err: any) {
    console.error('Failed to fetch posts:', err);
    postsError.value = err.response?.data?.message || '无法加载回复列表。';
  } finally {
    loadingPosts.value = false;
  }
};

const handleCreatePost = async () => {
  if (!newPost.content.trim()) {
    createPostError.value = '回复内容不能为空。';
    return;
  }
  creatingPost.value = true;
  createPostError.value = null;
  try {
    await discussionApi.createReply(Number(threadId.value), {
      content: newPost.content
    });
    newPost.content = ''; // Clear textarea
    await fetchPosts(); // Refresh posts
  } catch (err: any) {
    console.error('Failed to create post:', err);
    createPostError.value = err.response?.data?.message || '回复失败，请稍后再试。';
  } finally {
    creatingPost.value = false;
  }
};

// 根据权限判断是否可以删除回复
const canDeletePost = (post: DiscussionReply) => {
  return post.authorId === Number(currentUserId.value) || 
         userRole.value === 'teacher' || 
         userRole.value === 'assistant';
};

// 确认删除回复
const confirmDeletePost = async (postId: number) => {
  if (!confirm('确定要删除这条回复吗？此操作不可撤销。')) {
    return;
  }
  
  try {
    await discussionApi.deleteReply(postId);
    posts.value = posts.value.filter(p => p.id !== postId);
  } catch (err: any) {
    console.error('删除失败:', err);
    alert('删除失败，请重试');
  }
};

// 点赞回复（暂时模拟，实际项目中需要实现点赞API）
const likePost = async (postId: number) => {
  try {
    // 模拟点赞效果，实际项目中需要调用点赞API
    const post = posts.value.find(p => p.id === postId);
    if (post) {
      post.likeCount = (post.likeCount || 0) + 1;
    }
    console.log(`点赞回复: ${postId}`);
  } catch (err: any) {
    console.error('点赞失败:', err);
    alert('点赞失败，请重试');
  }
};

const formatDate = (dateString: string) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
  });
};

const goBack = () => {
  if (thread.value?.courseId) {
    router.push(`/courses/${thread.value.courseId}/discussions`);
  } else {
    router.back(); // Fallback if courseId is not available
  }
};

onMounted(() => {
  if (threadId.value) {
    fetchThreadDetails();
    fetchPosts();
  } else {
    threadError.value = '未指定讨论帖ID。';
    postsError.value = '未指定讨论帖ID。';
    loadingThread.value = false;
    loadingPosts.value = false;
  }
});

</script>

<style scoped>
/* For rendering HTML content safely, consider using a library or DOMPurify if content is user-generated and complex */
.prose :deep(p) {
  margin-bottom: 1em;
}
</style>

