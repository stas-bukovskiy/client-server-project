import React from "react";
import {Link} from "react-router-dom";

type GroupCardProps = {
    group: {
        id: string;
        name: string;
        description: string;
        createdAt: Date;
        updatedAt: Date;
    };
    handleDelete: (id: string) => void;
}


const GroupCardComponent: React.FC<GroupCardProps> = ({group, handleDelete}) => {
    return (
        <div className="card">
            <div className="card-body">
                <Link to={`/groups/${group.id}`} className="link">
                    <h5 className="card-title">{group.name}</h5>
                    <p className="card-text">
                        {group.description}
                    </p>
                    <hr/>
                    <p className="card-text">
                        Created at: <strong>{group.createdAt.toString()}</strong>
                    </p>
                    <p className="card-text">
                        Updated at: <strong>{group.updatedAt.toString()}</strong>
                    </p>
                </Link>
                <div className="d-flex justify-content-center mt-3">
                    <Link to={`/groups/edit/${group.id}`} className="btn btn-secondary">
                        Edit
                    </Link>
                    <button className="btn btn-danger mx-3" onClick={() => {
                        handleDelete(group.id)
                    }}>
                        Delete
                    </button>
                </div>
            </div>
        </div>
    )
}

export default GroupCardComponent;