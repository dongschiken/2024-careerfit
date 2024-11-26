import axios from "axios";
const ncapi = axios.create({
  baseURL: "http://192.168.210.52:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

export default ncapi;
