import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import md5 from "md5";


const EditUser: React.FC = () => {
    const {id} = useParams();
    const [fullName, setFullname] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [passwordConfirm, setPasswordConfirm] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");
    const [passwordNotConfirmed, setPasswordNotConfirmed] = useState(false);
    const navigate = useNavigate();


    useEffect(() => {
        const fetchUser = async () => {
            setIsLoading(true);
            setError('');

            try {
                const token = localStorage.getItem('token');
                const response = await fetch(`http://localhost:8000/api/user/${id}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                const data = await response.json();
                if (response.ok) {
                    setFullname(data.fullName);
                    setUsername(data.username);
                } else {
                    setError(data.message);
                }
            } catch (error) {
                setError("Something went wrong");
            }

            setIsLoading(false);
        };

        fetchUser();
    }, [id]);

    const handleFullNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFullname(e.target.value);
    };

    const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUsername(e.target.value);
    };

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPassword(e.target.value);
        setPasswordNotConfirmed(password === passwordConfirm);
    };

    const handlePasswordConfirmChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPasswordConfirm(e.target.value);
        setPasswordNotConfirmed(password === passwordConfirm);
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        console.log(password)

        if (!fullName || !username || !password || !passwordConfirm || passwordNotConfirmed) {
            return;
        }

        setIsLoading(true);
        const encodedPassword = md5(password).toUpperCase();
        console.log(encodedPassword)

        const groupRequest = {
            fullName,
            username,
            password: encodedPassword
        };

        // Send the POST request to the server
        fetch(`http://localhost:8000/api/user/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
            body: JSON.stringify(groupRequest),
        })
            .then((response) => {
                if (response.status === 200) {
                    navigate('/users');
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
                        <label htmlFor="fullName">Full name:</label>
                        <input
                            type="text"
                            id="fullName"
                            className="form-control"
                            value={fullName}
                            onChange={handleFullNameChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="username">Username:</label>
                        <div className="input-group">
                            <span className="input-group-text" id="input-group-left-example">@</span>
                            <input
                                type="text"
                                id="username"
                                className="form-control"
                                value={username}
                                onChange={handleUsernameChange}
                                required
                            />
                        </div>
                    </div>

                    {
                        passwordNotConfirmed && (
                            <div className="alert alert-warning" role="alert">
                                Password are not equals!
                            </div>
                        )
                    }
                    <div className="form-group">
                        <label htmlFor="password">Password:</label>
                        <input
                            type="password"
                            id="password"
                            className="form-control"
                            value={password}
                            onChange={handlePasswordChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="passwordConfirm">Confirm password:</label>
                        <input
                            type="password"
                            id="passwordConfirm"
                            className="form-control"
                            value={passwordConfirm}
                            onChange={handlePasswordConfirmChange}
                            required
                        />
                    </div>
                    <div className="form-group d-flex justify-content-center">
                        <button type="submit" className="btn btn-primary" disabled={isLoading}>
                            {isLoading ? 'Updating...' : 'Update User'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditUser;
