

// https://upmostly.com/tutorials/build-a-react-switch-toggle-component
export default function({ isOn, handleToggle, id, color }) {

    //label을 클릭해도 input의 onClick에 걸린다
    return (
        <div className="switch-container">
            <input 
                onClick={handleToggle}
                className="switch-checkbox"
                id={`switch-new${id}`}
                type="checkbox"
            />
            <label 
                style={{background: isOn && color}}
                className="switch-label"
                htmlFor={`switch-new${id}`}
            >
                <span className="switch-button" />
            </label>
        </div>
    );
}