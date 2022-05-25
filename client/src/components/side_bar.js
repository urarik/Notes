//cdreact에서 button의 fa-bars 등등이 import되는듯. 의존성 해결 필요
import {} from 'cdbreact';
import React, { useState } from "react";
import Logo from '../assets/logo.png'

import { NavLink } from "react-router-dom";
import { useDispatch, useSelector } from 'react-redux';
import { fetchUser } from '../actions';


export default function({id, setCloseMain}) {
    const [close, setClose] = useState("");
    const project = useSelector(state => state.project);
    const dispatch = useDispatch();

    const toggle = () => {
        if(close == "") {
            setCloseMain("closed");
            setClose("closed");
        }
        else {
            setCloseMain("");
            setClose("");
        }
    };

    const onLogoutClick = () => {
        dispatch(fetchUser(''));
    };

    return (
        <nav className={`sidebar ${close}`}>
            <header>
                <div className='image-text'>
                    <span className='image'>
                        <img src={Logo} alt='logo' />
                    </span>

                    <div className='text header-text'>
                        <span className='name'>Notes!</span>
                        <span className='profession'>{project.title}</span>
                    </div>
                </div>

                <i className='bx bx-chevron-right toggle'
                   onClick={() => toggle()} />
            </header>
            
            <div className='menu-bar'>
                <div className='menu'>
                    <li className='search-box'>
                        <i className='bx bx-search icon'></i>
                        <input type="search" placeholder='Search..' />
                    </li>
                    <ul className='menu-links' style={{paddingLeft: 0}}>
                        <li className='nav-link' style={{padding: 0}}>
                            <NavLink to={`/project/${id}/main`} >
                                <i className='bx bx-home icon' ></i>
                                <span className='text nav-text'>Main</span>
                            </NavLink>
                        </li>
                        <li className='nav-link' style={{padding: 0}}>
                            <NavLink to={`/project/${id}/notes`} >
                                <i className='bx bx-edit icon' ></i>
                                <span className='text nav-text'>Note</span>
                            </NavLink>
                        </li>
                        <li className='nav-link' style={{padding: 0}}>
                            <NavLink to={`/project/${id}/dash`} >
                                <i className='bx bxs-dashboard icon' ></i>
                                <span className='text nav-text'>Dashboard</span>
                            </NavLink>
                        </li>
                        <li className='nav-link' style={{padding: 0}}>
                            <NavLink to={`/project/${id}/classdiagram`} >
                                <i className='bx bx-check-square icon' ></i>
                                <span className='text nav-text'>Class Diagram</span>
                            </NavLink>
                        </li>
                        <li className='nav-link' style={{padding: 0}}>
                            <NavLink to={`/project/${id}/sequencediagram`} >
                                <i className='bx bx-check-square icon' ></i>
                                <span className='text nav-text'>Sequence Diagram</span>
                            </NavLink>
                        </li>
                    </ul>
                </div>
                <div className='botton-content'>
                    <li className='' style={{padding: 0}}>
                        <NavLink to={`/project/${id}/setting`} >
                            <i className='bx bxs-brightness icon'></i>
                            <span className='text nav-text'>Setting</span>
                        </NavLink>
                    </li>
                    <li className='' style={{padding: 0}}>
                        <NavLink to={`/projects`} >
                            <i className='bx bx-arrow-back icon'></i>
                            <span className='text nav-text'>Back</span>
                        </NavLink>
                        <NavLink to={'/'} onClick = {() => onLogoutClick()}>
                            <i className='bx bx-log-out icon second' ></i>
                            <span className='text nav-text second'>Logout</span>
                        </NavLink>
                    </li>
                </div>
            </div>
        </nav>
    );
}

// bx bx-chevron-right 는 boxicons.
// index.html에서 import함