import React from "react";
import {Link} from "react-router-dom";

type UserCardProps = {
    user: {
        id: string,
        fullName: string,
        username: string,
        password: string,
        role: string,
        createdAt: Date,
        updatedAt: Date
    };
    handleDelete: (id: string) => void;
}


const UserCardComponent: React.FC<UserCardProps> = ({user, handleDelete}) => {
    return (
        <div className="card">
            <div className="card-body">
                <h5 className="card-title">{user.fullName}</h5>
                <p className="card-text">
                    Username: <strong>{user.username}</strong>
                </p>
                <p className="card-text">
                    Role: <span
                    className={user.role === "ADMIN" ? "badge bg-primary" : "badge bg-secondary"}>{user.role}</span>
                </p>
                <hr/>
                <p className="card-text">
                    Created at: <strong>{user.createdAt.toString()}</strong>
                </p>
                <p className="card-text">
                    Updated at: <strong>{user.updatedAt.toString()}</strong>
                </p>
                <div className="d-flex justify-content-center mt-3">
                    <Link to={`/users/edit/${user.id}`} className="btn btn-secondary">
                        Edit
                    </Link>
                    <button className="btn btn-danger mx-3" onClick={() => {
                        handleDelete(user.id)
                    }}>
                        Delete
                    </button>
                </div>
            </div>
        </div>
    )
}

export default UserCardComponent;