import { useReducer } from "react";

export default function Input({error, add, type, placeholder}) {
    const [errormsg, setError] = error;

    return (
        <div>
            <input
                type={type}
                placeholder={placeholder}
                onFocus={() => setError("")}
                className="form-control"
                {...add} />
            <p className="error">{errormsg}</p>
        </div>
    );
}