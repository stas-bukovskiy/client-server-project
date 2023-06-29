import React from 'react';
import {Nav, OverlayTrigger, Tooltip} from 'react-bootstrap';
import {BsBagFill, BsGrid, BsPerson, BsSearch} from 'react-icons/bs';
import './Navbar.css';
import {RiLoginBoxFill, RiLogoutBoxFill} from "react-icons/ri";
import {useNavigate} from "react-router-dom";

const Navbar: React.FC = () => {
    const token = localStorage.getItem("token");
    const navigate = useNavigate();
    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate("/login")
    };
    return (
        <div className="navbar-wrapper">
            <Nav className="navbar">
                {token ? <>
                    <Nav.Item>
                        <OverlayTrigger
                            placement="right"
                            overlay={<Tooltip id="tooltip-users">Users</Tooltip>}
                        >
                            <Nav.Link href="/users">
                                <BsPerson/>
                            </Nav.Link>
                        </OverlayTrigger>
                    </Nav.Item>
                    <Nav.Item>
                        <OverlayTrigger
                            placement="right"
                            overlay={<Tooltip id="tooltip-search">Search</Tooltip>}
                        >
                            <Nav.Link href="/search">
                                <BsSearch/>
                            </Nav.Link>
                        </OverlayTrigger>
                    </Nav.Item>
                    <Nav.Item>
                        <OverlayTrigger
                            placement="right"
                            overlay={<Tooltip id="tooltip-calendar">Groups</Tooltip>}
                        >
                            <Nav.Link href="/groups">
                                <BsGrid/>
                            </Nav.Link>
                        </OverlayTrigger>
                    </Nav.Item>
                    <Nav.Item>
                        <OverlayTrigger
                            placement="right"
                            overlay={<Tooltip id="tooltip-goods">Goods</Tooltip>}
                        >
                            <Nav.Link href="/goods">
                                <BsBagFill/>
                            </Nav.Link>
                        </OverlayTrigger>
                    </Nav.Item>

                    <Nav.Item>
                        <OverlayTrigger
                            placement="right"
                            overlay={<Tooltip id="tooltip-calendar">Logout</Tooltip>}
                        >
                            <Nav.Link onClick={handleLogout}>
                                <RiLogoutBoxFill className="logout-icon"/>
                            </Nav.Link>
                        </OverlayTrigger>
                    </Nav.Item>
                </> : <Nav.Item>
                    <OverlayTrigger
                        placement="right"
                        overlay={<Tooltip id="tooltip-calendar">Login</Tooltip>}
                    >
                        <Nav.Link href="/login">
                            <RiLoginBoxFill className="logout-icon"/>
                        </Nav.Link>
                    </OverlayTrigger>
                </Nav.Item>
                }
            </Nav>
        </div>
    );
};

export default Navbar;
