import React, {useState} from 'react';
import {useNavigate} from "react-router-dom";
import {Button, Modal} from "react-bootstrap";


interface GoodCardProps {
    good: {
        id: string;
        name: string;
        description: string;
        producer: string;
        quantity: number;
        price: number;
        createdAt: string;
        updatedAt: string;
        group: {
            id: string;
            name: string;
        };
    };
    handleDelete: (id: string) => void;
}

const GoodCardComponent: React.FC<GoodCardProps> = ({good, handleDelete}) => {

    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);
    const [quantity, setQuantity] = useState(good.quantity);
    const [dQuantity, setDQuantity] = useState(1);
    const [addMode, setAddMode] = useState(false);
    const [error, setError] = useState("");
    const [addError, setAddError] = useState("");


    const handleEdit = () => {
        navigate(`/goods/edit/${good.id}`)
    };

    const handleDeleteClick = () => {
        fetch(`http://localhost:8000/api/good/${good.id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
        })
            .then((response) => {
                if (response.status === 204) {
                    handleDelete(good.id);
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
    };

    const handleAddClick = () => {
        setAddMode(true);
        setShowModal(true);
    };

    const handleModalClose = () => {
        setShowModal(false);
        setDQuantity(1);
        setError("")
    };

    const handleWriteOffClick = () => {
        setAddMode(false);
        setShowModal(true);
    };

    const handleDQuantityChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setDQuantity(parseInt(e.target.value));
    };

    const handleAddSubmit = () => {
        setAddError("");
        const changeQuantityRequest = {
            dQuantity: dQuantity,
        };

        // Send the POST request to the server
        fetch(`http://localhost:8000/api/good/${addMode ? 'add' : 'write-off'}/${good.id}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Include your authorization token here
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
            body: JSON.stringify(changeQuantityRequest),
        })
            .then((response) => {
                if (response.ok) {
                    if (addMode) {
                        setQuantity(quantity + dQuantity)
                    } else {
                        setQuantity(quantity - dQuantity)
                    }
                    navigate(`/groups/${good.group.id}`)
                    setShowModal(false);
                    setDQuantity(1);
                } else {
                    response.json()
                        .then(data => {
                            setAddError(data.message)
                        });
                }
            })
            .catch((error) => {
                setAddError(error)
            });
    };

    return (
        <div className="card good-card">
            <div className="card-body">
                {
                    error && (
                        <div className="alert alert-danger" role="alert">
                            {error}
                        </div>
                    )
                }
                <h5 className="card-title">{good.name}</h5>
                <p className="card-text">{good.description}</p>
                <ul className="list-group">
                    <li className="list-group-item">
                        <strong>Producer:</strong> {good.producer}
                    </li>
                    <li className="list-group-item">
                        <strong>Quantity:</strong> {quantity}
                    </li>
                    <li className="list-group-item">
                        <strong>Price:</strong> {good.price}
                    </li>
                    <li className="list-group-item">
                        <strong>Overall price:</strong> {good.price * quantity}
                    </li>
                    <li className="list-group-item">
                        <strong>Created At:</strong> {good.createdAt}
                    </li>
                    <li className="list-group-item">
                        <strong>Updated At:</strong> {good.updatedAt}
                    </li>
                    <li className="list-group-item">
                        <strong>Group:</strong> {good.group.name}
                    </li>
                </ul>
                <div className="button-group d-flex justify-content-center mt-3">
                    <button className="btn btn-secondary" onClick={handleEdit}>
                        Edit
                    </button>
                    <button className="btn btn-danger mx-4" onClick={handleDeleteClick}>
                        Delete
                    </button>
                    <button className="btn btn-success" onClick={handleAddClick}>
                        Add
                    </button>
                    <button className="btn btn-primary mx-4" onClick={handleWriteOffClick}>
                        Write Off
                    </button>
                </div>
            </div>


            {/* Modal */}
            <Modal show={showModal} onHide={handleModalClose}>
                <Modal.Header closeButton>
                    <Modal.Title>
                        {addMode ? "Add quantity" : "Write off quantity"}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {
                        addError && (
                            <div className="alert alert-danger" role="alert">
                                {addError}
                            </div>
                        )
                    }
                    <div className="form-group">
                        <label htmlFor="dQuantity">
                            {addMode ? "Quantity to add" : "Quantity to write off"}
                        </label>
                        <input
                            type="number"
                            id="dQuantity"
                            className="form-control"
                            value={dQuantity}
                            onChange={handleDQuantityChange}
                            min="0"
                            step="1"
                            max={quantity}
                            required
                        />
                    </div>
                    {error && <div className="alert alert-danger" role="alert">
                        error
                    </div>}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={handleAddSubmit}>
                        Submit
                    </Button>
                    <Button variant="secondary" onClick={handleModalClose}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default GoodCardComponent;
