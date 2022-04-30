import { useEffect, useState } from "react";
import checkIcon from '../assets/check.svg';
import errorIcon from '../assets/error.svg';
import infoIcon from '../assets/info.svg';
import warningIcon from '../assets/warning.svg';

// https://blog.logrocket.com/how-to-create-a-custom-toast-component-with-react/
export default function Toast({ toastList, position, autoDelete, dismissTime }) {
    const [list, setList] = useState(toastList);

    useEffect(() => {
        setList([...toastList]);
        // eslint-disable-next-line
    }, [toastList]);

    useEffect(() => {
        const interval = setInterval(() => {
            if (autoDelete && toastList.length && list.length) {
                deleteToast(toastList[0].id);
            }
        }, dismissTime);
        
        return () => {
            clearInterval(interval);
        }

        // eslint-disable-next-line
    }, [toastList, autoDelete, dismissTime, list]);

    const renderIcon = (icon) => {
        switch(icon) {
            case 'check':
                return checkIcon;
             case 'error':
                return errorIcon;
             case 'info':
                return infoIcon;
             case 'warning':
                return warningIcon;
             default:
                return errorIcon;
        }
    };

    const deleteToast = id => {
        const listItemIndex = list.findIndex(e => e.id === id);
        const toastListItem = toastList.findIndex(e => e.id === id);
        list.splice(listItemIndex, 1);
        toastList.splice(toastListItem, 1);
        setList([...list]);
    }

    return (
        <>
            <div className={`notification-container ${position}`}>
                {
                    list.map((toast, i) => {    
                        return (
                            <div 
                                key={i}
                                className={`notification custom-toast ${position}`}
                                style={{ backgroundColor: toast.backgroundColor }}>
                                <button onClick={() => deleteToast(toast.id)}>
                                    X
                                </button>
                                <div className="notification-image">
                                    <img src={renderIcon(toast.icon)} alt="" />
                                </div>
                                <div>
                                    <p className="notification-title">{toast.title}</p>
                                    <p className="notification-message">
                                        {toast.description}
                                    </p>
                                </div>
                            </div>
                        );
                    }
                    )
                }
            </div>
        </>
    );
}