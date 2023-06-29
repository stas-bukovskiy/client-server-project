import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import md5 from "md5";

const CreateUser: React.FC = () => {
    const [fullName, setFullname] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [passwordConfirm, setPasswordConfirm] = useState('');
    const [role, setRole] = useState('ADMIN');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");
    const [passwordNotConfirmed, setPasswordNotConfirmed] = useState(false);
    const navigate = useNavigate();

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

    const handleRoleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setRole(e.target.value);
    };

    const handleSubmit = (e: React.FormEvent) => {
        setError("")
        e.preventDefault();

        if (!fullName || !username || !password || !passwordConfirm || passwordNotConfirmed || !role) {
            return;
        }

        setIsLoading(true);
        const encodedPassword = md5(password).toUpperCase();

        const groupRequest = {
            fullName,
            username,
            password: encodedPassword,
            role
        };

        // Send the POST request to the server
        fetch('http://localhost:8000/api/user', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Include your authorization token here
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
            body: JSON.stringify(groupRequest),
        })
            .then((response) => {
                if (response.status === 201) {
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
                <h2>Create User</h2>
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
                        (passwordNotConfirmed) && (
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
                    <div className="form-group">
                        <label htmlFor="role">Role:</label>
                        <select
                            id="role"
                            className="form-control"
                            value={role}
                            onChange={handleRoleChange}
                            required
                        >
                            <option value="ADMIN">Admin</option>
                            <option value="USER">User</option>
                        </select>
                    </div>

                    <div className="form-group d-flex justify-content-center">
                        <button type="submit" className="btn btn-primary" disabled={isLoading}>
                            {isLoading ? 'Creating...' : 'Create User'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateUser;
