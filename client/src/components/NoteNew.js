import { useState } from "react";
import { useParams } from "react-router"
import useForm from "../hooks/useForm";
import { useNavigate } from "react-router";
import { post } from "../api/api";
import Input from "./Input";
import Toast from "./Toast";


export default function(props) {
    const {id} = useParams();

    const {add, handleSubmit} = useForm();
    const [titleError, setTitleError] = useState("");
    const [fail, setFail] = useState(null);

    const navigate = useNavigate();
    const navigateToNoteList = () => {
        navigate(`/project/${id}/notes`);
    }

    const onSuccess = async (data) => {
        try{
            const response = await post(`note/create`, {
              title: data.title.value,
              pid: id,
              isMain: false
            });
            if(response.status == 201) { // CREATED
                navigateToNoteList();
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
        <div style={{marginTop: '48px'}}>
            <div className="text-center form-div max-height">
                <form className="form-container" onSubmit={e => handleSubmit(e, onSuccess, onFail)}>
                    <h2 className="mb-5">Note Creation Form</h2>
                    <Input add={ add({name:"title"}) }
                        placeholder="Title"
                        error={[titleError, setTitleError]} />
                    
                    <button className="mt-5 btn btn-lg btn-primary btn-block" type="submit">노트 생성</button>
                </form>
            </div>
            <div>
                {
                    fail && 
                    <Toast
                        toastList={[{
                        id: 1,
                        title: 'Failed',
                        description: `Note creation failed.. Error code: ${fail}`,
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