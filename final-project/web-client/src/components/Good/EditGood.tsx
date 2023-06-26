import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';

type GoodRequest = {
    name: string;
    description: string;
    producer: string;
    quantity: number;
    price: number;
    groupId: string;
}

const EditGood: React.FC = () => {
    const {id} = useParams();
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [producer, setProducer] = useState('');
    const [quantity, setQuantity] = useState(0);
    const [price, setPrice] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [groupId, setGroupId] = useState('');
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const fetchGood = async () => {
            setIsLoading(true);
            setError('');

            try {
                const token = localStorage.getItem('token');
                const response = await fetch(`http://localhost:8000/api/good/${id}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                const data = await response.json();
                if (response.ok) {
                    setName(data.name);
                    setDescription(data.name)
                    setProducer(data.producer)
                    setQuantity(data.quantity)
                    setPrice(data.price)
                    setGroupId(data.group.id)
                } else {
                    setError(data.message);
                }
            } catch (error) {
                setError('An error occurred while fetching groups.');
            }

            setIsLoading(false);
        };

        fetchGood().then(r => console.log(r));
    }, [id]);

    if (error) {
        return <div>Error: {error}</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }
    const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setName(e.target.value);
    };

    const handleDescriptionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setDescription(e.target.value);
    };

    const handleProducerChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setProducer(e.target.value);
    };

    const handleQuantityChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setQuantity(Number(e.target.value));
    };

    const handlePriceChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPrice(Number(e.target.value));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        if (!name || !description || !producer || !quantity || !price || !groupId) {
            return;
        }


        setIsLoading(true);

        const goodRequest: GoodRequest = {
            name,
            description,
            producer,
            quantity,
            price,
            groupId
        };


        // Send the POST request to the server
        fetch(`http://localhost:8000/api/good/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                // Include your authorization token here
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
            body: JSON.stringify(goodRequest),
        })
            .then((response) => {
                if (response.status === 200) {
                    // Good created successfully, navigate to the corresponding group page
                    navigate(`/groups/${groupId}`);
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
                <h2>Edit Good</h2>
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
                    <div className="form-group">
                        <label htmlFor="producer">Producer:</label>
                        <input
                            type="text"
                            id="producer"
                            className="form-control"
                            value={producer}
                            onChange={handleProducerChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="quantity">Quantity:</label>
                        <input
                            type="number"
                            id="quantity"
                            className="form-control"
                            value={quantity}
                            onChange={handleQuantityChange}
                            required
                            min="0"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="price">Price:</label>
                        <input
                            type="number"
                            id="price"
                            className="form-control"
                            value={price}
                            onChange={handlePriceChange}
                            step="0.01"
                            min="0.01"
                            required
                        />
                    </div>
                    <div className="form-group d-flex justify-content-center">
                        <button type="submit" className="btn btn-primary" disabled={isLoading}>
                            {isLoading ? 'Saving...' : 'Save Changes'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditGood;
