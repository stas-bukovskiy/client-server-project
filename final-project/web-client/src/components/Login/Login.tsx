import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import './Login.css';
import md5 from 'md5';

const Login: React.FC = () => {
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            const encodedPassword = md5(password).toUpperCase();
            const response = await fetch('http://localhost:8000/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    login: username,
                    password: encodedPassword,
                }),
            });

            if (response.ok) {
                const data = await response.json();
                const token = data.token;
                localStorage.setItem('token', token)
                navigate('/groups');
            } else if (response.status === 401) {
                const data = await response.json();
                setErrorMessage(data.message);
            } else {
                setErrorMessage('An error occurred during login.');
            }
        } catch (error) {
            setErrorMessage('An error occurred during login.');
            console.log(error);
        }

        setLoading(false);
    };

    return (
        <div className="login-container">
            <form className="login-form" onSubmit={handleLogin}>
                <h2>Login</h2>
                <div className="form-group">
                    <label htmlFor="username">Username:</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                {errorMessage && <div className="error-message">{errorMessage}</div>}
                <button type="submit" className="submit-button" disabled={loading}>
                    {loading ? 'Logging In...' : 'Log In'}
                </button>
            </form>
        </div>
    );
};

export default Login;
