import { useCallback, useEffect, useMemo, useState } from "react"
import _ from 'lodash';

// https://phrygia.github.io/react/2021-09-21-react-modal/
export default function({header, open, closeModal, getUsers, handleSubmit, setSuccess}) {
    const [users, setUsers] = useState([]);
    const [selectedUsers, setSelectedUsers] = useState([]);

    const onChange = ({target}) => {
        getUsers(target.value, setUsers, () => {});
    }
    const onClickUser = (username) => {
        if(selectedUsers.find(selectedUsername => username == selectedUsername) == undefined)
            setSelectedUsers([...selectedUsers, username]);
    };
    const onClickRemoveUser = (username) => {
        const newUsers = [];
        selectedUsers.forEach(selectedUsername => {
            if(selectedUsername != username) newUsers.push(selectedUsername);
        })
        setSelectedUsers(newUsers);
    };

    const renderSelectedUsers = () => {
        return selectedUsers.map((username) => {
            return (
                <div className='selected-user' key={`selcted${username}`}>
                    <span>{username}</span>
                    <i className='bx bx-x remove'
                       onClick={() => onClickRemoveUser(username)}></i>
                </div>
            )
        });
    };

    const renderUsers = () => {
        return users.map(({username}) => {
            return (
                <li className="user" key = {username} onClick={() => onClickUser(username)}>
                    <span> {username} </span>
                </li>
            );
        })
    };

    const onSuccess = () => {
        setSuccess(true);
        closeModal();
    }

    return (
        <div className={'modal '+ (open? 'open': '')}>
            <section>
                <header>{header}
                    <button className="close" onClick={closeModal}>
                        <i className='bx bx-x'></i>
                    </button>
                </header>
                <main>
                    <li className='search-box'>
                        <i className='bx bx-search icon'></i>
                        <input type="search" placeholder='Search..' onChange={onChange} />
                    </li>
                    <ul className="user-list">
                        {renderUsers()}
                    </ul>
                    <ul className="selected-user-list">
                        {renderSelectedUsers()}
                    </ul>
                </main>
                <footer>
                    <button className="btn btn-outline-primary" onClick={() => handleSubmit(selectedUsers, onSuccess, () => {})}>
                        invite
                    </button>
                    <button className="btn btn-outline-dark" onClick={closeModal}>
                        close
                    </button>
                </footer>
            </section>
        </div>
    )
}