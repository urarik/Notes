import useForm from "../../hooks/useForm";
import {useState} from 'react';
import { useNavigate } from "react-router-dom";
import Input from "../Input";
import Toast from "../Toast";
import {post} from "../../api/api";
import jsCookies from 'js-cookies';


export default function(props) {
    const {add, handleSubmit} = useForm();
    const [titleError, setTitleError] = useState("");
    const [fail, setFail] = useState(null);

    const navigate = useNavigate();
    const navigateToProjectList = () => {
        navigate("/projects");
    }

    const onSuccess = async (data) => {
        try{
            const response = await post(`projects/create`, {
              title: data.title.value
            });
            if(response.status == 201) { // CREATED
                navigateToProjectList();
            } else setFail(response);
          } catch(error) {
            setFail(error);
          }
    };

    const onFail = async (data, error) => {
        if(error['title'] != null)
            setTitleError("오잉?!");
    };

    return (
        <div >
            <div className="text-center form-div max-height">
                <form className="form-container" onSubmit={e => handleSubmit(e, onSuccess, onFail)}>
                    <h2 className="mb-5">Creation Form</h2>
                    <Input add={ add({name:"title"}) }
                        placeholder="Title"
                        error={[titleError, setTitleError]} />
                    
                    <button className="mt-5 btn btn-lg btn-primary btn-block" type="submit">Sign up</button>
                </form>
            </div>
            <div>
                {
                    fail && 
                    <Toast
                        toastList={[{
                        id: 1,
                        title: 'Failed',
                        description: `Project creation failed.. Error code: ${fail}`,
                        backgroundColor: '#d9534f',
                        icon: 'error'
                        }]}
                        position="bottom-right"
                        autoDelete={true}
                        dismissTime={10000}
                    />
                }
            </div>
        </div>
    );
}