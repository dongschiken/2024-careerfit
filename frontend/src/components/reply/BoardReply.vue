<template>
  <div class="board-reply-container">
    <!-- 댓글 작성 -->
    <div class="reply-form">
      <textarea
        v-model="replyData.content"
        class="reply-input"
        placeholder="댓글 작성..."
        rows="3"
      ></textarea>
      <button @click="registReply()" class="reply-button">댓글 쓰기</button>
    </div>

    <!-- 댓글 리스트 -->
    <div class="reply-list">
      <div class="reply-item" v-for="reply in replies" :key="reply.replyId">
        <div class="reply-header">
          <div
            class="author-avatar"
            :class="{
              'gradient-animation':
                !reply.user.profileUrl ||
                reply.user.profileUrl === '' ||
                reply.user.profileUrl == null,
            }"
          ></div>
          <div class="reply-info">
            <span class="reply-author">@{{ reply.user.nickname }}</span>
            <span class="reply-time">{{ reply.dateAgo }}</span>
          </div>
        </div>
        <p class="reply-content">{{ reply.content }}</p>
        <div class="reply-footer">
          <div class="like-group">
            <div class="like-rereply">
              <img
                src="@/assets/img/thumb_up_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
              />
              <span>{{ reply.likes }}</span>
              <div class="add-reply" @click="toggleReplyForm(reply)">답글</div>
            </div>
            <div
              v-if="reply.user.userId == replyData.userId"
              class="user-controller"
            >
              <img
                @click="toggleUserController(reply.replyId)"
                src="@/assets/img/dot.png"
              />
              <div
                v-if="reply.showUserController"
                class="user-controller-toggle"
              >
                <div @click="editReply(reply.replyId)">수정</div>
                <div @click="deleteReply(reply.replyId)">삭제</div>
              </div>
            </div>
          </div>
        </div>
        <div
          @click="toggleRereply(reply.replyId)"
          v-if="reply.replyResponses.length !== 0"
          class="reply-more-group"
        >
          <img src="@/assets/img/down_arrow.png" />
          <div class="reply-more">답글 더보기</div>
        </div>
        <!-- 답글 폼 표시 -->
        <div v-if="reply.showReplyForm" class="reply-form">
          <textarea
            v-model="reply.replyContent"
            class="rereply-input"
            placeholder="답글을 작성하세요..."
            rows="3"
          ></textarea>
          <button @click="submitReply(reply.replyId)" class="reply-button">
            답글 쓰기
          </button>
        </div>
        <!-- 대댓글 리스트 -->
        <div v-if="reply.showReplies" class="reply-responses">
          <div v-for="rereply in reply.replyResponses" :key="rereply.replyId">
            <div class="reply-item">
              <div class="rereply-header">
                <div class="reply-info">
                  <span class="reply-author">@{{ rereply.user.nickname }}</span>
                  <span class="reply-time">{{ rereply.dateAgo }}</span>
                </div>
                <div
                  v-if="reply.user.userId == replyData.userId"
                  class="rereply-user-controller"
                >
                  <img
                    @click="toggleUserReController(rereply.replyId)"
                    src="@/assets/img/dot.png"
                  />
                  <div
                    v-if="rereply.showUserController"
                    class="user-controller-toggle"
                  >
                    <div @click="editReply(rereply.replyId)">수정</div>
                    <div @click="deleteReply(rereply.replyId)">삭제</div>
                  </div>
                </div>
              </div>
              <p class="rereply-content">{{ rereply.content }}</p>
            </div>
          </div>
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
const rereplyData = ref({
  userId: 0,
  boardId: 0,
  content: "",
  parentReplyId: 0,
  depth: 1,
});
const replies = ref([]);
const props = defineProps({
  boardId: Number,
});

const editReply = async (replyId) => {};

const deleteReply = async (replyId) => {
  let reply = replies.value.find((reply) => reply.replyId === replyId);
  if (!reply) {
    reply = replies.value.find((r) =>
      r.replyResponses.some((r2) => r2.replyId === replyId)
    );
  }
  console.log(reply);
  const isDelete = confirm("정말 삭제하시겠습니까?");
  if (!isDelete) return;
  try {
    const response = await api.delete("/api/reply" + `/${replyId}`);
    axios
      .get(`http://localhost:8080/api/reply/${reply.boardId}`)
      .then((response) => {
        replies.value = response.data;
        // 대댓글이 있을 경우에만 toggleRereply 호출
        if (reply.replyResponses && reply.replyResponses.length > 1) {
          toggleRereply(reply.replyId);
        }
      })
      .catch((error) => {
        console.error("Error fetching replies:", error);
      });
  } catch (error) {
    console.error("Error in registReply:", error);
    if (error.response.status === 403) {
      alert("답글을 작성하시려면 로그인을 진행해주세요");
      router.push({ name: "login" });
    }
  }
};

const toggleUserController = (replyId) => {
  const reply = replies.value.find((r) => r.replyId === replyId);
  if (reply) {
    reply.showUserController = !reply.showUserController;
  }
};

const toggleUserReController = (replyId) => {
  // 대댓글을 포함한 댓글을 찾음
  const reply = replies.value.find((r) =>
    r.replyResponses.some((r2) => r2.replyId === replyId)
  );
  if (reply) {
    const rereply = reply.replyResponses.find((r) => r.replyId === replyId);
    if (rereply) {
      rereply.showUserController = !rereply.showUserController;
    }
  }
};

const submitReply = async (replyId) => {
  const reply = replies.value.find((reply) => reply.replyId === replyId);
  console.log(reply);
  if (!reply || !reply.replyContent) {
    alert("답글 내용이 없습니다.");
    return;
  }
  const rereply = {
    userId: replyData.value.userId,
    boardId: reply.boardId,
    content: reply.replyContent,
    depth: 1,
    parentReplyId: reply.replyId,
  };
  try {
    const response = await api.post("/api/reply", rereply, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    replyData.value.content = "";
    axios
      .get(`http://localhost:8080/api/reply/${reply.boardId}`)
      .then((response) => {
        replies.value = response.data;
        toggleRereply(reply.replyId);
        console.log(response.data);
      })
      .catch((error) => {
        console.error("Error fetching replies:", error);
      });
  } catch (error) {
    console.error("Error in registReply:", error);
    if (error.response.status === 403) {
      alert("답글을 작성하시려면 로그인을 진행해주세요");
      router.push({ name: "login" });
    }
  }
};

// 답글 폼 표시/숨김
const toggleReplyForm = (reply) => {
  reply.showReplyForm = !reply.showReplyForm;
};

const toggleRereply = (replyId) => {
  console.log("대댓글 리스트:", replies.value);
  const reply = replies.value.find((r) => r.replyId === replyId);
  if (reply) {
    reply.showReplies = !reply.showReplies; // 대댓글 리스트 보이기/숨기기
  }
};

// 이미지 배경 스타일
function getBackgroundImage(profileUrl) {
  return profileUrl
    ? { backgroundImage: `url(${profileUrl})` }
    : { background: "linear-gradient(135deg, #FF7F50, #FFB6C1, #87CEFA)" };
}

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
    axios
      .get(`http://localhost:8080/api/reply/${props.boardId}`)
      .then((response) => {
        replies.value = response.data;
        console.log(response.data);
      })
      .catch((error) => {
        console.error("Error fetching replies:", error);
      });
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
        console.error(error);
      });
  }
});
</script>

<style scoped>
.user-controller-toggle > div {
  font-size: 15px;
  border-radius: 8px;
}
.user-controller-toggle > div:hover {
  cursor: pointer;
  background-color: rgba(0, 0, 0, 0.1);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.user-controller-toggle {
  display: flex;
  flex-direction: column;
  position: absolute;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 5px;
  border-radius: 8px;
  margin-top: 10px;
  width: 50px;
  text-align: center;
  z-index: 10;
}
.rereply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  margin-left: 35px;
}
.rereply-user-controller > img {
  width: 25px;
  border-radius: 8px;
}
.rereply-user-controller > img:hover {
  cursor: pointer;
  background-color: rgba(0, 0, 0, 0.1);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.rereply-content {
  font-size: 14px;
  margin-top: 4px;
  margin-bottom: 8px;
  margin-left: 50px;
  min-width: 650px;
  max-width: 650px;
}
.reply-responses {
  margin-left: 30px;
  background-color: #f0f0f0; /* 연회색 배경 */
  padding: 10px;
  border-radius: 8px;
  padding-top: 45px;
}
.rereply-input {
  resize: none;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px;
  font-size: 14px;
  margin-bottom: 8px;
  min-width: 733px;
  max-width: 733px;
  font-family: inherit;
  margin-left: 50px;
}
.reply-footer {
  margin-top: 8px;
}
.add-reply {
  display: flex;
  border-radius: 10px;
  min-width: 35px;
  max-width: 37px;
  min-height: 25px;
  max-height: 25px;
  text-align: center;
  align-items: center;
  justify-content: center;
}
.add-reply:hover {
  cursor: pointer;
  background-color: rgba(0, 0, 0, 0.1);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.reply-more {
  font-size: 14px;
}
.reply-more-group {
  display: flex;
  margin-left: 45px;
  margin-top: 8px;
  min-width: 110px;
  max-width: 110px;
  align-items: center;
  border-radius: 10px;
}
.reply-more-group:hover {
  cursor: pointer;
  background-color: rgba(0, 0, 0, 0.1);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.reply-more-group > img {
  width: 25px;
  height: 25px;
}
.like-rereply {
  display: flex;
  align-items: center;
}
.like-rereply > div {
  font-size: 14px;
  margin-left: 15px;
}
.user-controller > img {
  width: 25px;
  border-radius: 10px;
}
.user-controller > img:hover {
  cursor: pointer;
  background-color: rgba(0, 0, 0, 0.1);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.like-rereply > img {
  width: 19px;
  height: 19px;
}
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
  align-items: flex-start;
  margin-bottom: 8px;
  margin-left: 50px;
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
  margin-right: 15px;
  margin-left: 15px;
}

.reply-time {
  color: #888;
}

.reply-content {
  font-size: 14px;
  margin-top: 15x;
  margin-bottom: 8px;
  margin-left: 50px;
  min-width: 700px;
  max-width: 700px;
}

.reply-footer {
  font-size: 12px;
  color: #666;
  display: flex;
  margin-left: 50px;
}
.like-group {
  display: flex;
  min-width: 740px;
  max-width: 740px;
  justify-content: space-between;
  margin-top: 15px;
}
.like-group > img {
  width: 20px;
}
.like-group > span {
  font-size: 14px;
  margin-left: 5px;
}
.gradient-animation {
  background: linear-gradient(135deg, #ff7f50, #ffb6c1, #87cefa);
  background-size: 200% 200%;
  animation: gradientShift 5s infinite;
}

@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
.author-avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
}
</style>
