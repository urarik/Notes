import axios from "axios";
import jsCookies from "js-cookies";

const api = axios.create({
    baseURL: `http://localhost:8080/`
})

const login = async (url, type) => {
    return api.post(url, type);
};

const post = async (url, type) => {
    const config = {headers: {Authorization: `Bearer ${jsCookies.getItem("token")}`}};
    return api.post(url, type, config);
}

const get = async (url, type) => {
    const config = {
        headers: {Authorization: `Bearer ${jsCookies.getItem("token")}`},
        params: type
    };
    return api.get(url, config);
}

// api는 기존에 쓰는 코드용
// 기존에 api를 쓰는 코드를 다 바꾸자
export {api, login, post, get};