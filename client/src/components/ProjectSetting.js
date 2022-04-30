import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

import useForm from "../hooks/useForm";
import Input from "./Input";
import { api, post } from "../api/api";
import Toast from "./Toast";
import jsCookies from 'js-cookies';
import { fetchProject } from "../actions";
import { useNavigate } from "react-router";
import InviteModal from "./util/InviteModal";

export default function(props) {
    const project = useSelector(state => state.project);
    const username= useSelector(state => state.username);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const navigateToProjectList = () => {
      navigate("/projects");
    };

    const [forOwner, setForOwner] = useState(false);
    const [forMember, setForMember] = useState(false);

    const {add, handleSubmit} = useForm();
    const [titleError, setTitleError] = useState("");
    const [fail, setFail] = useState(null);
    const [success, setSuccess] = useState(null);

    //for update
    const onSuccess = async (data) => {
        try{
            const newProject = { ...project, title: data.title.value };
            const response = await post(`projects/update`, {
              ...newProject
            });
            if(response.status == 200) {
                dispatch(fetchProject(newProject));
                setSuccess(true);
            } else setFail(response);
          } catch(error) {
            setFail(error);
          }
    };
    const onFail = async (data, error) => {
        if(error['title'] != null)
            setTitleError("오잉?!");
    };

    const onDeleteConfirm = async () => {
        const response = await post(`projects/delete`, {
          pid: project.pid
        });

        if(response.status == 200) {
            navigateToProjectList();
        } else setFail(response);
    };

    useEffect(() => {
        if(project.owner.username == username) {
            setForOwner(true);
        }
        else setForMember(true);
        
    }, [project]);

    const [modalOpen, setModalOpen] = useState(false);
    const [invite, setInvite] = useState(false);
    const openModal = (isInvite) => {
        setModalOpen(true);
        if(isInvite) setInvite(true);
        else setInvite(false);
    };
    const closeModal = () => {
        setModalOpen(false);
    };

    const onExitClick = async () => {
        try{
            const response = await post(`projects/exit`, {
              pid: project.pid
            });
            if(response.status == 200) {
                navigateToProjectList();
                dispatch(fetchProject({}));
            } else setFail(response);
          } catch(error) {
            setFail(error);
          }
    };

    const getUsers = (url) => {
        return (prefix, setUsers, handleFail) => {
            const config = {
                headers: {Authorization: `Bearer ${jsCookies.getItem("token")}`},
                params: {
                    pid: project.pid,
                    prefix
                }
            };

            api.get(url, config)
                .then( (response) => setUsers(response.data))
                .catch(e => handleFail(e));
        }
    };
    const inviteGetUsers = getUsers('project/invite');
    const grantGetUsers = getUsers('project/grant');
    const handleInvitationSubmit = (url, userNames) => {
        return (selectedUsers, handleSuccess, handleFail) => {
            const config = {headers: {Authorization: `Bearer ${jsCookies.getItem("token")}`}};

            api.post(url, {
                id: project.pid,
                [userNames]: selectedUsers
            } ,config)
                .then( (response) => handleSuccess(response) )
                .catch(e => handleFail(e));
        };
    };
    const inviteHandleSubmit = handleInvitationSubmit('messages/invite', 'userNames');
    const grantHandleSubmit = handleInvitationSubmit('projects/update/add/roles', 'admins');


    return (
        <div className="setting pt-5">
            {
                forOwner &&
                (
                    <div>
                    <div className="text-center form-div">
                        <form className="form-container" onSubmit={e => handleSubmit(e, onSuccess, onFail)}>
                            <Input add={ add({name:"title"}) }
                                placeholder="Title"
                                error={[titleError, setTitleError]} />
                            
                            <button className="btn btn-sm btn-primary btn-block" type="submit">Update</button>
                        </form>
                    </div>
                    <hr />
                    <button className="mt-3 btn btn-sm btn-danger btn-block"
                        onClick={() => { if (window.confirm('Are you sure you wish to delete this project   ?')) onDeleteConfirm(); } }>
                            Delete
                    </button>
                    
                    <button className="mt-3 btn btn-sm btn-primary btn-block" onClick={() => openModal(false)}>Grant permission</button>
                    <button className="mt-3 btn btn-sm btn-primary btn-block" onClick={() => openModal(true)}>Invite</button>

                    </div>
                )
            }
            {forMember?
                <button className="mt-3 btn btn-sm btn-primary btn-block"
                        onClick={() => onExitClick()}>
                        Exit
                </button>
            :null}


            <div>
                {
                    fail && 
                    <Toast
                        toastList={[{
                        id: 1,
                        title: 'Failed',
                        description: `Project update failed.. Error code: ${fail}`,
                        backgroundColor: '#d9534f',
                        icon: 'error'
                        }]}
                        position="bottom-right"
                        autoDelete={true}
                        dismissTime={10000}
                    />
                }
            </div>
            <div>
                {
                    success && 
                    <Toast
                        toastList={[{
                        id: 1,
                        title: 'Success',
                        description: `Project update success!`,
                        backgroundColor: '#5cb85c',
                        icon: 'check'
                        }]}
                        position="bottom-right"
                        autoDelete={true}
                        dismissTime={10000}
                    />
                }
            </div>
            <div>
                <InviteModal header={
                    invite? '초대': '권한 부여'
                } 
                open={modalOpen} 
                closeModal={closeModal} 
                getUsers={invite? inviteGetUsers: grantGetUsers}
                handleSubmit={invite? inviteHandleSubmit: grantHandleSubmit}
                setSuccess={setSuccess} >

                </InviteModal>
            </div>
        </div>
    );
}