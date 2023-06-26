import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';


const EditGroup: React.FC = () => {
    const {id} = useParams();
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchGroup = async () => {
            setIsLoading(true);
            setError('');

            try {
                const token = localStorage.getItem('token');
                const response = await fetch(`http://localhost:8000/api/group/${id}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                const data = await response.json();
                if (response.ok) {
                    setName(data.name);
                    setDescription(data.description)
                } else {
                    setError(data.message);
                }
            } catch (error) {
                setError("Something went wrong");
            }

            setIsLoading(false);
        };

        fetchGroup();
    }, [id]);

    const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setName(e.target.value);
    };

    const handleDescriptionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setDescription(e.target.value);
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        if (!name || !description) {
            return;
        }

        setIsLoading(true);

        const groupRequest = {
            name,
            description,
        };

        // Send the POST request to the server
        fetch(`http://localhost:8000/api/group/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
            body: JSON.stringify(groupRequest),
        })
            .then((response) => {
                if (response.status === 200) {
                    navigate('/groups');
                } else {
                    response.json()
                        .then(data => {
                            setError(data.message)
                        });
                }
            })
            .catch((error) => {
                setError(error.toString());
            })
            .finally(() => {
                setIsLoading(false);
            });
    };

    return (
        <div className="wrapper">
            <div className="create-group-container">
                <h2>Edit Group</h2>
                {
                    error && (
                        <div className="alert alert-danger" role="alert">
                            {error}
                        </div>
                    )
                }
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="name">Name:</label>
                        <input
                            type="text"
                            id="name"
                            className="form-control"
                            value={name}
                            onChange={handleNameChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="description">Description:</label>
                        <input
                            type="text"
                            id="description"
                            className="form-control"
                            value={description}
                            onChange={handleDescriptionChange}
                            required
                        />
                    </div>
                    <div className="form-group d-flex justify-content-center">
                        <button type="submit" className="btn btn-primary" disabled={isLoading}>
                            {isLoading ? 'Updating...' : 'Update Group'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditGroup;
