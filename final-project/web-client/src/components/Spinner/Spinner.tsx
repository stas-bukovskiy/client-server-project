import React from "react";

const Spinner: React.FC = () => {
    return (
        <div className="spinner-container">
            <div className="spinner-border" role="status">
                <span className="visually-hidden">Loading...</span>
            </div>
        </div>
    );
}

export default Spinner;