import React, {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import AddButton from "../Buttons/AddButton.tsx";
import 'bootstrap/dist/css/bootstrap.min.css';
import './GroupsPage.css';
import GroupCardComponent from "./GroupCardComponent.tsx";
import Spinner from "../Spinner/Spinner.tsx"; // Import the CSS file for custom styling

type Group = {
    id: string;
    name: string;
    description: string;
    createdAt: Date;
    updatedAt: Date;
};

const GroupComponent: React.FC = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [groups, setGroups] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchGroups = async () => {
            setLoading(true);
            setError('');

            try {
                const token = localStorage.getItem('token');
                const response = await fetch('http://localhost:8000/api/group', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                const data = await response.json();
                if (response.ok) {
                    setGroups(data);
                } else {
                    setError(data.message);
                }
            } catch (error) {
                setError('An error occurred while fetching groups.');
            }

            setLoading(false);
        };

        fetchGroups();
    }, []);

    if (loading) {
        return (
            <Spinner/>
        );
    }

    const handleDelete = async (id: string) => {
        try {
            const token = localStorage.getItem('token');
            const response = await fetch(`http://localhost:8000/api/group/${id}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.ok) {
                // Remove the deleted group from the list
                setGroups(groups.filter((group: Group) => group.id !== id));
            } else {
                response.json()
                    .then(data => {
                        setError(data.message)
                    });
            }
        } catch (error) {
            setError('An error occurred while deleting the group.');
        }
    };

    return (
        <div className="container">
            <h2>Groups</h2>
            <hr/>
            {
                error && (
                    <div className="alert alert-danger" role="alert">
                        {error}
                    </div>
                )
            }
            <div className="row">
                {groups.map((group: Group) => (
                    <div key={group.id} className="col-md-4">
                        <GroupCardComponent key={group.id} group={group} handleDelete={handleDelete}/>
                    </div>
                ))}
            </div>
            <AddButton onClick={() => {
                navigate("/groups/create")
            }}/>
        </div>
    );
};

export default GroupComponent;

