import useForm from "../hooks/useForm";
import {useState} from 'react';
import { useNavigate } from "react-router-dom";
import Input from "./Input";
import Toast from "./Toast";
import { login as register} from "../api/api";

export default function SignUp (props) {
    const {add, handleSubmit} = useForm();
    const [fail, setFail] = useState(null);

    const [emailError, setEmailError] = useState("");
    const [passwordError, setPasswordError] = useState("");
    const [confirmPasswordError, setConfirmPasswordError] = useState("");

    const navigate = useNavigate();
    const navigateToIndex = () => {
        navigate("/");
    }

    const onSuccess = async (data) => {
        try{
            const response = await register(`register`, {
              username: data.email.value,
              password: data.password.value
            });
            if(response.status == 201) {
                navigateToIndex();
              } else setFail(response);
          } catch(error) {
            setFail(error);
          }
    };

    const onFail = async (data, error) => {
        if(error['email'] != null)
        setEmailError("An email should contain @");
        if(error['password'] != null)
            setPasswordError("A password should be at least 2");
        if(error['confirm_password'] != null)
            setConfirmPasswordError("Your password and confirmation password do not match.");
    };

    return (
        <div>
        <div className="text-center form-div">
            <form className="form-container" onSubmit={e => handleSubmit(e, onSuccess, onFail)}>
                <h2 className="mb-5">Please Sign up!</h2>
                <Input add={ add({name:"email", contains:"@"}) }
                    type="email"
                    placeholder="Email address"
                    error={[emailError, setEmailError]} />
                <Input add={ add({name:"password", minlength:2}) }
                    type="password"
                    placeholder="Password"
                    error={[passwordError, setPasswordError]} />
                <Input add={ add({name:"confirm_password", matches:'password'}) }
                    type="password"
                    placeholder="Confirm password"
                    error={[confirmPasswordError, setConfirmPasswordError]} />
                
                <button className="mt-5 btn btn-lg btn-primary btn-block" type="submit">Sign up</button>
            </form>
        </div>
            {
                fail && 
                <Toast
                    toastList={[{
                    id: 1,
                    title: 'Failed',
                    description: `Sign up failed.. Error code: ${fail}`,
                    backgroundColor: '#d9534f',
                    icon: 'error'
                    }]}
                    position="bottom-right"
                    autoDelete={true}
                    dismissTime={10000}
                />
            }
        </div>
    )
}