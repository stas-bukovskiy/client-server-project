import React, {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import AddButton from "../Buttons/AddButton.tsx";
import 'bootstrap/dist/css/bootstrap.min.css';
import Spinner from "../Spinner/Spinner.tsx";
import UserCardComponent from "./UserCardComponent.tsx";

type User = {
    id: string,
    fullName: string,
    username: string,
    password: string,
    role: string,
    createdAt: Date,
    updatedAt: Date
};

const UserComponent: React.FC = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [isUser, setIsUser] = useState(false);
    const [users, setUsers] = useState([]);
    const [error, setError] = useState('');


    useEffect(() => {
        const fetchUsers = async () => {
            setLoading(true);
            setError('');

            try {
                const token = localStorage.getItem('token');
                const response = await fetch('http://localhost:8000/api/user', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                const data = await response.json();
                if (response.ok) {
                    setUsers(data);
                } else {
                    setIsUser(true);
                    setError(data.message);
                }
            } catch (error) {
                setIsUser(true);
                setError('An error occurred while fetching groups.');
            }

            setLoading(false);
        };

        fetchUsers();
    }, []);

    if (loading) {
        return (
            <Spinner/>
        );
    }

    const handleDelete = async (id: string) => {
        try {
            const token = localStorage.getItem('token');
            const response = await fetch(`http://localhost:8000/api/user/${id}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.ok) {
                setUsers(users.filter((user: User) => user.id !== id));
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
            <h2>Users</h2>
            <hr/>
            {
                error && (
                    <div className="alert alert-danger" role="alert">
                        {error}
                    </div>
                )
            }
            <div className="row">
                {users.map((user: User) => (
                    <div key={user.id} className="col-md-4">
                        <UserCardComponent key={user.id} user={user} handleDelete={() => handleDelete(user.id)}/>
                    </div>
                ))}
            </div>
            {
                !isUser && (
                    <AddButton onClick={() => {
                        navigate("/users/create")
                    }}/>
                )
            }
        </div>
    );
};

export default UserComponent;

