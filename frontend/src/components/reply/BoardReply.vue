<template>
  <div class="board-reply-container">
    <!-- 댓글 작성 -->
    <div class="reply-form">
      <textarea
        v-model="replyData.content"
        class="reply-input"
        placeholder="댓글 작성."
        rows="3"
      ></textarea>
      <button @click="registReply()" class="reply-button">댓글 쓰기</button>
    </div>

    <!-- 댓글 리스트 -->
    <div class="reply-list">
      <div class="reply-item" v-for="reply in replies" :key="reply.id">
        <div class="reply-header">
          <img
            class="reply-avatar"
            src="https://via.placeholder.com/40"
            alt="User Avatar"
          />
          <div class="reply-info">
            <span class="reply-author">{{ reply.author }}</span>
            <span class="reply-time">{{ reply.time }}</span>
          </div>
        </div>
        <p class="reply-content">{{ reply.content }}</p>
        <div class="reply-footer">
          <span class="like-group">
            <img
              src="@/assets/img/thumb_up_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
            />
            <span>{{ reply.likes }}</span></span
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import api from "@/api/axiosInstance";
import { ref, onMounted, watch } from "vue";
import { defineProps } from "vue";
import router from "@/router";
const replyData = ref({
  userId: 0,
  boardId: 0,
  content: "",
  parentReplyId: 0,
  depth: 0,
});
const replies = ref([]);
const props = defineProps({
  boardId: Number,
});
const registReply = async () => {
  if (!replyData.value.content.trim()) {
    alert("댓글 내용을 작성해 주세요.");
    return;
  }
  const reply = {
    userId: replyData.value.userId,
    boardId: props.boardId,
    content: replyData.value.content,
    depth: 0,
  };

  try {
    const response = await api.post("/api/reply", reply, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    replyData.value.content = "";
  } catch (error) {
    console.error("Error in registReply:", error);
    if (error.response.status === 403) {
      alert("댓글을 작성하시려면 로그인을 진행해주세요");
      router.push({ name: "login" });
    }
  }
};

watch(
  () => props.boardId,
  (newBoardId) => {
    if (newBoardId) {
      axios
        .get(`http://localhost:8080/api/reply/${newBoardId}`)
        .then((response) => {
          replies.value = response.data;
          console.log(response.data);
        })
        .catch((error) => {
          console.error(error);
        });
    }
  }
);

onMounted(() => {
  const accessToken = sessionStorage.getItem("accessToken");
  if (accessToken) {
    axios
      .get("http://localhost:8080/api/token-user", {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      })
      .then((response) => {
        replyData.value.userId = response.data.userId;
        console.log(response.data.userId);
      })
      .catch((error) => {
        console.error(error);
      });
  } else {
    console.error("access 토큰이 없습니다.");
  }

  if (props.boardId) {
    axios
      .get(`http://localhost:8080/api/reply/${props.boardId}`)
      .then((response) => {
        replies.value = response.data;
        console.log(response.data);
      })
      .catch((error) => {
        console.error("Error fetching replies:", error);
      });
  }
});
</script>

<style scoped>
.board-reply-container {
  max-width: 800px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #ddd;
  padding: 16px;
  margin-top: 50px;
  margin-bottom: 50px;
}

/* 댓글 작성 */
.reply-form {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
}

.reply-input {
  resize: none;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px;
  font-size: 14px;
  margin-bottom: 8px;
  width: 98%;
  font-family: inherit;
}

.reply-button {
  align-self: flex-end;
  background-color: #ff7f50;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
}

.reply-button:hover {
  background-color: #ff5722;
}

/* 댓글 리스트 */
.reply-list {
  margin-top: 16px;
}

.reply-item {
  border-bottom: 1px solid #eee;
  padding-bottom: 16px;
  margin-bottom: 16px;
}

.reply-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.reply-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 8px;
}

.reply-info {
  font-size: 14px;
}

.reply-author {
  font-weight: bold;
  margin-right: 8px;
}

.reply-time {
  color: #888;
}

.reply-content {
  font-size: 14px;
  margin-top: 4px;
  margin-bottom: 8px;
}

.reply-footer {
  font-size: 12px;
  color: #666;
  display: flex;
  justify-content: flex-end;
}
.like-group {
  display: flex;
  align-items: start;
}
.like-group > img {
  width: 20px;
}
.like-group > span {
  font-size: 14px;
  margin-left: 5px;
}
</style>
