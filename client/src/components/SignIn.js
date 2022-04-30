import jsCookies from "js-cookies";
import { useMemo, useReducer, useState } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from 'react-router-dom';
import { fetchUser } from "../actions";
import {login} from "../api/api";
import useForm from "../hooks/useForm";
import Input from "./Input";
import Toast from "./Toast";

export default function SignIn (props) {
    const { add, handleSubmit } = useForm();
    const [ emailError, setEmailError ] = useState("");
    const [ passwordError, setPasswordError ] = useState("");
    const [fail, setFail] = useState(null);
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const navigateToProjectList = () => {
      navigate("/projects");
    };

    const onSuccess = async (data) => {
      try{
        const response = await login(`auth/login`, {
          username: data.email.value,
          password: data.password.value
        });
        if(response.status == 200) {
          document.cookie = `token=${response.data.token}`;
          dispatch(fetchUser(data.email.value));
          navigateToProjectList();
        } else setFail(response);
      } catch(error) {
          setFail(error);
      }
        // const config = {headers: {Authorization: `Bearer ${jsCookies.getItem("token")}`}}
        // const res = await api.get(`/test`, config);
    }

    const onFail = (data, error) => {
        if(error['email'] != null)
            setEmailError("An email should contain @");
        if(error['password'] != null)
            setPasswordError("A password should be at least 2");
    }
    //임시로 이메일 type="email과 contains:"@" 삭제"
    return (
    <div>
    <div className="text-center form-div max-height">
        <form className="form-container" onSubmit={e => handleSubmit(e, onSuccess, onFail)}>
          <h2 className="mb-5" >Please Sign in</h2>
          <Input add ={ add({name:"email"}) }
            
            placeholder="Email address" 
            error={[emailError, setEmailError]} />
          <Input add ={ add({name:"password", minlength:2}) }
            type="password"
            placeholder="password"
            error={[passwordError, setPasswordError]} />
          <button className="mt-5 btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
        </form>
      </div>
      {
        fail && 
        <Toast
            toastList={[{
              id: 1,
              title: 'Failed',
              description: `Log in failed.. Error code: ${fail}`,
              backgroundColor: '#d9534f',
              icon: 'error'
            }]}
            position="bottom-right"
            autoDelete={true}
            dismissTime={10000}
          />
      }
      </div>
    );
}