import React from 'react';
import {RiAddFill} from 'react-icons/ri';
import './AddButton.css';

interface AddButtonProps {
    onClick: () => void;
}

const AddButton: React.FC<AddButtonProps> = ({onClick}) => {
    return (
        <button className="circle-button" onClick={onClick}>
            <RiAddFill className="plus-icon"/>
        </button>
    );
};

export default AddButton;
